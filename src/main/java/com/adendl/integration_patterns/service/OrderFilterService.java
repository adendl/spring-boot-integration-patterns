package com.adendl.integration_patterns.service;


import com.adendl.integration_patterns.model.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderFilterService {

    public static boolean accept(Order order)
    {
        return order.getAmount() >= 10;
    }
}
