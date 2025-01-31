package com.jereztech.orders.data.entities;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "products")
public class Product extends BaseEntity {

    @NotBlank
    @EqualsAndHashCode.Include
    @Pattern(regexp = "\\w{12}")
    @InsertOnlyProperty
    private String sku;

    @Pattern(regexp = "\\d{12}")
    private String barcode;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotBlank
    private String thumbnailUrl;

    @Min(1)
    @NotNull
    private BigDecimal price;

}
