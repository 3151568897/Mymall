package com.example.mymall.ware.web;

import com.example.common.constant.AuthServerConstant;
import com.example.mymall.ware.intercepotr.CartInterceptor;
import com.example.mymall.ware.service.CartService;
import com.example.mymall.ware.vo.CartItemVO;
import com.example.mymall.ware.vo.CartVO;
import com.example.mymall.ware.vo.UserInfoTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/allCheck")
    public String allCheck(@RequestParam("check") boolean check){
        //重定向到成功页,再次查询购物车就行了
        cartService.allCheck(check);

        return "redirect:http://cart.mymall.com/cart.html";
    }

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItemVO> currentUserCartItems() {
        List<CartItemVO> cartItemVOS = cartService.getCartItems();

        return cartItemVOS;
    }

    @GetMapping("/currentUserCheckedCartItems")
    @ResponseBody
    public List<CartItemVO> currentUserCheckedCartItems() {
        List<CartItemVO> cartItemVOS = cartService.getCheckedCartItems();

        return cartItemVOS;
    }

    /**
     * 浏览器有一个cookid:user-key 来标识临时用户的身份
     * 浏览器以后保存后,每次访问都会有这个cookie
     *
     * @return
     */
    @GetMapping({"/cart.html", "/"})
    public String cartListPage(Model model) {
        CartVO cart = cartService.getCart();

        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes re) {
        CartItemVO cartItemVO = cartService.addToCart(skuId, num);

        re.addAttribute("skuId", skuId);
        return "redirect:http://cart.mymall.com/addToCartSuccess.html";
    }
    //防止在成功页面刷新后,继续增加购物车数据
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        //重定向到成功页,再次查询购物车就行了
        CartItemVO cartItemVO = cartService.getCartItem(skuId);

        model.addAttribute("item", cartItemVO);
        return "success";
    }

    /**
     * checkItem
     * 改变购物车商品是否选中
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") boolean check){
        //重定向到成功页,再次查询购物车就行了
        cartService.checkItem(skuId, check);

        return "redirect:http://cart.mymall.com/cart.html";
    }

    /**
     * 改变购物车商品的数量
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        //重定向到成功页,再次查询购物车就行了
        cartService.countItem(skuId, num);

        return "redirect:http://cart.mymall.com/cart.html";
    }
    /**
     * deleteItem
     * 删除购物车商品
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        //重定向到成功页,再次查询购物车就行了
        cartService.deleteItem(skuId);

        return "redirect:http://cart.mymall.com/cart.html";
    }
}
