package com.example.mymall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderPageController {

    @GetMapping("/")
    public String listPage(){

        return "list";
    }

    @GetMapping( "/{page}.html")
    public String confirmPage(@PathVariable("page")String page){

        return page;
    }
}
