package com.adendl.integration_patterns.utils;


import com.adendl.integration_patterns.model.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.adendl.integration_patterns.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OrderRouter {
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    private OrderRepository orderRepository;

    @JmsListener(destination = "order_router", containerFactory = "myFactory")
    public void receiveMessage(Order newOrder) {
        System.out.println("Received <" + newOrder + ">");
        this.jmsTemplate.convertAndSend(route(newOrder), newOrder);
    }


    //patern 1 - order router
    public static String route(Order order)
    {
        return order.getType().equals("standard") ? "standardQueue" : "expressQueue";
    }
}
