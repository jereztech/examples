package com.jereztech.orders.data.repositories;

import com.jereztech.orders.data.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends BaseRepository<Product> {

    Optional<Product> findBySku(String sku);

}
