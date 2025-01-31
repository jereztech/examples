package com.jereztech.orders.controllers;

import com.jereztech.orders.data.entities.Order;
import com.jereztech.orders.services.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseController<Order, OrderService> {

    public OrderController(OrderService service) {
        super(service);
    }

}
