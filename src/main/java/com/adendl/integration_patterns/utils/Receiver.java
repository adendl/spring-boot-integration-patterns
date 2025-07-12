package com.adendl.integration_patterns.utils;


import com.adendl.integration_patterns.model.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import com.adendl.integration_patterns.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class Receiver {

    @Autowired
    private OrderRepository orderRepository;

    @JmsListener(destination = "orders", containerFactory = "myFactory")
    public void receiveMessage(Order newOrder) {
        System.out.println("Received <" + newOrder + ">" + ", saved to database");
        orderRepository.save(newOrder);
    }
}
