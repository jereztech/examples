package com.jereztech.orders.data.repositories;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.data.enums.OrderStatus;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends BaseRepository<Order> {

    Optional<Order> findByNumber(String number);

    @Modifying
    @Query("UPDATE orders SET status = :status WHERE id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OrderStatus status);

}
