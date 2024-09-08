package com.example.mymall.order.web;

import com.example.mymall.order.service.OrderService;
import com.example.mymall.order.vo.OrderConfirmVO;
import com.example.mymall.order.vo.OrderSubmitResponseVO;
import com.example.mymall.order.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrabe(Model model){
        //获取订单确认的信息
        OrderConfirmVO orderConfirmVO = orderService.confirmOrder();

        model.addAttribute("orderConfirmData", orderConfirmVO);

        return "confirm";
    }

    /**
     * 下单提交
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO vo, Model model, RedirectAttributes redirectAttributes){
        OrderSubmitResponseVO orderSubmitResponseVO = orderService.submitOrder(vo);

        if(orderSubmitResponseVO.getCode() != 0){
            switch (orderSubmitResponseVO.getCode()){
                case 1:
                    redirectAttributes.addFlashAttribute("msg", "订单信息过期,请刷新后再提交");
                    break;
                case 2:
                    redirectAttributes.addFlashAttribute("msg", "订单商品价格发生变化,请确认后再提交");
                    break;
                case 3:
                    redirectAttributes.addFlashAttribute("msg", "库存锁定失败,商品库存不足");
                    break;
            }
            //下单失败回到订单确认页
            return "redirect:http://order.mymall.com/toTrade";
        }
        //下单成功取支付选择页
        model.addAttribute("submitOrderResp", orderSubmitResponseVO);
        return "pay";
    }
}
