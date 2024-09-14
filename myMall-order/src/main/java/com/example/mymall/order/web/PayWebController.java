package com.example.mymall.order.web;

import com.alipay.api.AlipayApiException;
import com.example.mymall.order.config.AlipayTemplate;
import com.example.mymall.order.service.OrderService;
import com.example.mymall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;
    @Autowired
    OrderService orderService;

    @GetMapping(value = "/payOrder", produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPay(orderSn);

        // 调用 AlipayTemplate 返回 HTML 表单
        String alipayHtml = alipayTemplate.pay(payVo);

        // 返回 HTML 响应，浏览器会渲染并显示支付页面
        return alipayHtml;
    }

}
