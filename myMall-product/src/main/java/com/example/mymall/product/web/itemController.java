package com.example.mymall.product.web;

import com.example.mymall.product.service.SkuInfoService;
import com.example.mymall.product.vo.SkuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class itemController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) {

        SkuItemVO item = skuInfoService.item(skuId);
        model.addAttribute("item", item);


        return "item";
    }
}
