package com.jereztech.orders;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.data.entities.OrderItem;
import com.jereztech.orders.data.entities.Product;
import com.jereztech.orders.data.repositories.OrderRepository;
import com.jereztech.orders.services.OrderItemService;
import com.jereztech.orders.services.OrderService;
import com.jereztech.orders.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.jereztech.orders.data.enums.OrderStatus.CANCELED;
import static com.jereztech.orders.data.enums.OrderStatus.RECEIVED;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private final Product productMock = Product.builder()
            .sku("SNK123456789")
            .barcode("987654321012")
            .name("Red Sneakers")
            .description("High-quality running shoes")
            .thumbnailUrl("https://cdn.example.com/images/sneakers.jpg")
            .price(BigDecimal.valueOf(25.12))
            .build();

    private final OrderItem orderItemMock = OrderItem.builder()
            .description("2 Red Sneakers")
            .quantity(2)
            .build();

    private final Order orderMock = Order.builder()
            .number("20250129-123456")
            .build();

    @BeforeEach
    void setUp() {
        orderMock.setId(randomUUID());
        productMock.setId(randomUUID());
        orderItemMock.setId(randomUUID());
        orderItemMock.setProduct(productMock);
        orderItemMock.setProductRef(AggregateReference.to(productMock.getId()));
        orderItemMock.setOrderRef(AggregateReference.to(orderMock.getId()));
        orderMock.setItems(List.of(orderItemMock));
        orderMock.setStatus(RECEIVED);
    }

    @Test
    void testSaveNewOrder() {
        when(orderRepository.findByNumber(orderMock.getNumber())).thenReturn(Optional.empty());
        when(productService.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            if (product.getId() == null) {
                product.setId(randomUUID());
            }
            return product;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(orderMock);

        Order savedOrder = orderService.save(orderMock);

        assertEquals(RECEIVED, savedOrder.getStatus());
        assertFalse(savedOrder.getItems().isEmpty());

        verify(orderRepository).findByNumber(orderMock.getNumber());
        verify(productService).save(productMock);
        verify(orderRepository).save(orderMock);
    }

    @Test
    void testCancelOrder() {
        orderService.cancel(orderMock);
        verify(orderRepository).updateStatus(orderMock.getId(), CANCELED);
    }

    @Test
    void testFindAllOrders() {
        Pageable pageable = mock(Pageable.class);
        when(orderRepository.findAll(pageable)).thenReturn(new PageImpl<Order>(List.of(orderMock)));
        when(productService.getById(productMock.getId())).thenReturn(productMock);

        Page<Order> resultPage = orderService.findAll(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.hasContent());

        verify(orderRepository).findAll(pageable);
        verify(productService).getById(productMock.getId());
    }

}
