package com.example.mymall.ware.web;

import com.example.mymall.ware.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage() {

        return "cartList";
    }

    @GetMapping("/success.html")
    public String successPage() {

        return "success";
    }
}
