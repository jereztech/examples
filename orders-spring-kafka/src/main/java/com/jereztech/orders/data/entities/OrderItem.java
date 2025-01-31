package com.jereztech.orders.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    private Integer index;

    @Size(max = 20)
    private String description;

    @Min(1)
    @NotNull
    private Integer quantity;

    @Min(1)
    private BigDecimal totalAmount;

    @Transient
    private Product product;

    @JsonIgnore
    @Column("order_id")
    private AggregateReference<Order, UUID> orderRef;

    @JsonIgnore
    @Column("product_id")
    private AggregateReference<Product, UUID> productRef;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (OrderItem.class.isInstance(other)) {
            OrderItem orderItem = (OrderItem) other;
            if (productRef != null) {
                return Objects.equals(productRef, orderItem.productRef);
            }
            if (product != null) {
                return Objects.equals(product, orderItem.product);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), product, productRef);
    }
}
