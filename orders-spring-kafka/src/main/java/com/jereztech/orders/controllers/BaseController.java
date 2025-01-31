package com.jereztech.orders.controllers;

import com.jereztech.orders.data.entities.BaseEntity;
import com.jereztech.orders.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;

public abstract class BaseController<T extends BaseEntity, S extends IBaseService<T>> {

    protected final S service;

    public BaseController(S service) {
        this.service = service;
    }

    @GetMapping
    public Page<T> findAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return service.findAll(pageable);
    }

}
