package com.tota.ecommercebackend.api.controller.order;

import com.tota.ecommercebackend.model.LocalUser;
import com.tota.ecommercebackend.model.WebOrder;
import com.tota.ecommercebackend.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService=orderService;
    }

    @GetMapping
    public List<WebOrder> getOrder(@AuthenticationPrincipal LocalUser user){
        return orderService.getOrders(user);
    }
}
