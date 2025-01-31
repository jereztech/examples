package com.jereztech.orders.data.entities;

import com.jereztech.orders.data.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "orders")
public class Order extends BaseEntity {

    private OrderStatus status;

    @NotBlank
    @Pattern(regexp = "\\w{1,36}")
    @InsertOnlyProperty
    @EqualsAndHashCode.Include
    private String number;

    /**
     * Sum of all order items prices before applying fees, discounts, or taxes.
     */
    private BigDecimal subtotalAmount;

    @Transient
    @Builder.Default
    private BigDecimal totalCharges = BigDecimal.ZERO;

    @Transient
    @Builder.Default
    private BigDecimal totalDiscounts = BigDecimal.ZERO;

    private BigDecimal totalAmount;

    @NotEmpty
    @MappedCollection(idColumn = "order_id", keyColumn = "index")
    private List<OrderItem> items;

}
