package com.jereztech.orders.services;

import com.jereztech.orders.data.entities.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBaseService<T extends BaseEntity> {

    Page<T> findAll(Pageable pageable);

}
