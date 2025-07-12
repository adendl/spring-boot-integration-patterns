package com.adendl.integration_patterns.controller;

import com.adendl.integration_patterns.model.Order;
import com.adendl.integration_patterns.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.jms.core.JmsTemplate;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    @PostMapping
    public Order createOrder(@RequestBody Order newOrder)
    {
        System.out.println("Sending order to queue");
        this.jmsTemplate.convertAndSend("orders", newOrder);
        return newOrder;
    }

}
