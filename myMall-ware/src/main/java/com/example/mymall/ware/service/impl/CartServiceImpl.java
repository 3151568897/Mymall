package com.example.mymall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.example.common.utils.R;
import com.example.mymall.ware.feign.ProductFeignService;
import com.example.mymall.ware.intercepotr.CartInterceptor;
import com.example.mymall.ware.service.CartService;
import com.example.mymall.ware.vo.CartItemVO;
import com.example.mymall.ware.vo.CartVO;
import com.example.mymall.ware.vo.SkuInfoVO;
import com.example.mymall.ware.vo.UserInfoTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_RREFIX = "mymall:cart:";

    @Override
    public CartItemVO addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        //判断商品是否存在
        String o = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(o)) {
            //如果商品不存在
            CartItemVO cartItemVO = new CartItemVO();
            //已有信息
            cartItemVO.setCount(num);
            cartItemVO.setCheck(true);
            cartItemVO.setSkuId(skuId);
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //1.远程查询当前要添加的商品的信息
                R r = productFeignService.getSkuInfo(skuId);
                if(r.getCode() == 0) {
                    SkuInfoVO skuInfoVO = r.getDataByKey("skuInfo", new TypeReference<SkuInfoVO>(){});

                    cartItemVO.setDefaultImage(skuInfoVO.getSkuDefaultImg());
                    cartItemVO.setTitle(skuInfoVO.getSkuTitle());
                    cartItemVO.setPrice(skuInfoVO.getPrice());

                }else {
                    log.error("远程调用商品服务查询商品信息失败");
                }
            }, executor);
            CompletableFuture<Void> getSkuSaleAttrTask = CompletableFuture.runAsync(() -> {
                //2.远程查询skuAttr的组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVO.setSkuSaleVO(skuSaleAttrValues);
            }, executor);

            //保证两个线程运行完成
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrTask).join();

            //3.将最终数据放入购物车
            String jsonString = JSON.toJSONString(cartItemVO);
            cartOps.put(skuId.toString(), jsonString);

            return cartItemVO;
        }
        //商品存在
        CartItemVO cartItemVO = JSON.parseObject(o, CartItemVO.class);
        cartItemVO.setCount(cartItemVO.getCount() + num);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVO));

        return cartItemVO;
    }

    @Override
    public CartItemVO getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());

        return JSON.parseObject(o, CartItemVO.class);
    }

    //获取整个购物车
    @Override
    public CartVO getCart() {
        CartVO cart = new CartVO();
        BoundHashOperations<String, Object, Object> operations = null;
        //1.区分用户是否登录
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        if(userInfoTO.getUserId() != null){
            //1.登录状态
            String cartKey = CART_RREFIX + userInfoTO.getUserId();
            //2.如果临时购物车的数据还没有合并,就合并
            String tempCartKey = CART_RREFIX + userInfoTO.getUserKey();
            List<CartItemVO> tempCartItems = getCartItems(tempCartKey);
            if(tempCartItems != null) {
                //临时购物车有数据,需要合并
                for (CartItemVO tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                //合并完后,清除临时购物车数据
                clearCart(tempCartKey);
            }

            //3.获取登录用户的购物车(包含合并的购物车)
            cart.setItems(getCartItems(cartKey));
        }else {
            //未登录状态
            String cartKey = CART_RREFIX + userInfoTO.getUserKey();
            cart.setItems(getCartItems(cartKey));
        }

        return cart;
    }

    /**
     * 获取到要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        String cartKey = "";
        //1.判断是临时购物车还是登录购物车
        if(userInfoTO.getUserId() == null) {
            //临时购物车
            cartKey = CART_RREFIX + userInfoTO.getUserKey();
        }else {
            //如果用户登录
            cartKey = CART_RREFIX+userInfoTO.getUserId();
        }
        //判断购物车是否有这个商品
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItemVO> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if(values != null &&values.size() > 0) {
            List<CartItemVO> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVO.class);
            }).collect(Collectors.toList());

            return collect;
        }

        return null;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, boolean check) {
        //获取购物车项
        CartItemVO cartItem = getCartItem(skuId);
        cartItem.setCheck(check);
        String jsonString = JSON.toJSONString(cartItem);
        //重新保存
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), jsonString);
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        if(num>0){
            //获取购物车项
            CartItemVO cartItem = getCartItem(skuId);
            cartItem.setCount(num);
            String jsonString = JSON.toJSONString(cartItem);
            //重新保存
            cartOps.put(skuId.toString(), jsonString);
        }else {
            cartOps.delete(skuId.toString());
        }

    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }
}
