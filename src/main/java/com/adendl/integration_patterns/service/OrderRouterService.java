package com.adendl.integration_patterns.service;


import com.adendl.integration_patterns.model.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import com.adendl.integration_patterns.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRouterService {
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    private OrderRepository orderRepository;

    @JmsListener(destination = "order_router", containerFactory = "myFactory")
    public void routeMessage(Order newOrder) {
        System.out.println("Received <" + newOrder + ">");
        this.jmsTemplate.convertAndSend(route(newOrder), newOrder);
    }

    @JmsListener(destination = "standardQueue", containerFactory = "myFactory")
    public void filterMessage(Order newOrder) {
        System.out.println("StandardQueue Received <" + newOrder + ">");
        if (OrderFilterService.accept(newOrder))
        {
            System.out.println("Routing message to gt10 queue");
            this.jmsTemplate.convertAndSend("gt10queue", newOrder);
        } else {
            System.out.println("not routing message");
        }
    }


    //patern 1 - order router
    public static String route(Order order)
    {
        return order.getType().equals("standard") ? "standardQueue" : "expressQueue";
    }
}
