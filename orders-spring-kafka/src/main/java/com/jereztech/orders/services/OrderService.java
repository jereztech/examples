package com.jereztech.orders.services;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.data.entities.OrderItem;
import com.jereztech.orders.data.entities.Product;
import com.jereztech.orders.data.repositories.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jereztech.orders.data.enums.OrderStatus.*;
import static org.apache.commons.collections4.CollectionUtils.*;

@Service
public class OrderService extends BaseService<Order, OrderRepository> {

    private final OrderItemService orderItemService;
    private final ProductService productService;

    public OrderService(OrderRepository repository, OrderItemService orderItemService, ProductService productService) {
        super(repository);
        this.orderItemService = orderItemService;
        this.productService = productService;
    }

    @Override
    public Order save(@Valid Order order) {
        repository.findByNumber(order.getNumber()).ifPresentOrElse(
                existingOrder -> merge(order),
                () -> {
                    order.setStatus(RECEIVED);
                    handleNewItemsCascade(order.getItems());
                    super.save(order);
                }
        );
        return order;
    }

    private void handleNewItemsCascade(Collection<OrderItem> items) {
        AtomicInteger index = new AtomicInteger(0);
        items.forEach(item -> {
            item.setId(UUID.randomUUID());
            Product product = productService.save(item.getProduct());
            item.setIndex(index.incrementAndGet());
            item.setProductRef(AggregateReference.to(product.getId()));
        });
    }

    private void merge(Order order) {
        List<OrderItem> existingItems = orderItemService.findByOrderId(order.getId());
        List<OrderItem> newItems = order.getItems();

        // Add items that are new
        List<OrderItem> toAdd = (List<OrderItem>) subtract(newItems, existingItems);
        if (isNotEmpty(toAdd)) {
            handleNewItemsCascade(toAdd);
            orderItemService.saveAll(toAdd);
        }

        // Remove items that are no longer present
        List<OrderItem> toDelete = (List<OrderItem>) subtract(existingItems, newItems);
        if (isNotEmpty(toDelete)) {
            orderItemService.deleteAll(toDelete);
        }

        // Update items that remain
        List<OrderItem> toUpdate = (List<OrderItem>) intersection(existingItems, newItems);
        if (isNotEmpty(toUpdate)) {
            toUpdate.forEach(existingItem -> newItems.stream()
                    .filter(newVersion -> newVersion.equals(existingItem))
                    .findFirst()
                    .ifPresent(newVersion -> existingItem.setQuantity(newVersion.getQuantity()))
            );
            orderItemService.saveAll(toUpdate);
        }
    }

    // Expensive PROCESSING here
    @Transactional
    public Order process(Order order) {
        order = getById(order.getId());
        order.setStatus(PROCESSING);
        repository.updateStatus(order.getId(), order.getStatus());

        List<OrderItem> orderItems = orderItemService.findByOrderId(order.getId());
        AtomicInteger index = new AtomicInteger(0);
        orderItems.forEach(item -> {
            item.setIndex(index.incrementAndGet());
            Product product = productService.getById(item.getProductRef().getId());
            item.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        });

        BigDecimal subtotalAmount = orderItems.stream()
                .map(OrderItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add),
                totalAmount = subtotalAmount.add(order.getTotalCharges()).subtract(order.getTotalDiscounts());

        order.setSubtotalAmount(subtotalAmount);
        order.setTotalAmount(totalAmount);
        order.setStatus(PICKUP);
        order.setItems(orderItems);
        return super.save(order);
    }

    public void cancel(Order order) {
        repository.updateStatus(order.getId(), CANCELED);
    }

    @Override
    public Page<Order> findAll(Pageable pageRequest) {
        Page<Order> page = repository.findAll(pageRequest);
        if (page.hasContent()) {
            page.getContent().forEach(order -> {
                order.getItems().forEach(item -> item.setProduct(productService.getById(item.getProductRef().getId())));
            });
        }
        return page;
    }
}
