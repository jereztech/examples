package com.jereztech.orders.services;

import com.jereztech.orders.data.entities.Product;
import com.jereztech.orders.data.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends BaseService<Product, ProductRepository> {

    public ProductService(ProductRepository repository) {
        super(repository);
    }

    @Override
    public Product save(@Valid Product product) {
        repository.findBySku(product.getSku())
                .ifPresent(existingProduct -> product.setId(existingProduct.getId()));
        return super.save(product);
    }
}
