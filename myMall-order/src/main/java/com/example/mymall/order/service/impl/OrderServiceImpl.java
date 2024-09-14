package com.example.mymall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.constant.OrderConstant;
import com.example.common.constant.OrderStatusEnum;
import com.example.common.exception.NoStockException;
import com.example.common.to.FareTO;
import com.example.common.to.MemberAddressVO;
import com.example.common.to.MemberEntity;
import com.example.common.to.SkuHasStockTO;
import com.example.common.utils.R;
import com.example.mymall.order.entity.OrderItemEntity;
import com.example.mymall.order.feign.MemberFeignService;
import com.example.mymall.order.feign.ProductFeignService;
import com.example.mymall.order.feign.WareFeignService;
import com.example.mymall.order.intercepotr.LoginUserInterceptor;
import com.example.mymall.order.service.OrderItemService;
import com.example.mymall.order.to.OrderCreateTO;
import com.example.mymall.order.vo.*;
import lombok.ToString;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.order.dao.OrderDao;
import com.example.mymall.order.entity.OrderEntity;
import com.example.mymall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVO> submitVOThreadLocal = new ThreadLocal<>();

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVO confirmOrder() {
        //加入线程,提高速度
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();

        //保证异步线程也可以获取cookid数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //1.远程获取用户地址
        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            List<MemberAddressVO> address = memberFeignService.getAddress(memberEntity.getId());
            orderConfirmVO.setAddress(address);
        }, executor);
        //2.远程查询购物车选中的购物项
        CompletableFuture<Void> orderItemFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);

            List<OrderItemVO> orderItemVOS = wareFeignService.currentUserCartItems();
            //2.1筛选出其中选中的商品,并且更新最新价格
            //如果选中商品为0,直接报错
            if (orderItemVOS == null || orderItemVOS.size() == 0) {
                throw new RuntimeException("没有选中商品");
            }
            List<OrderItemVO> items = orderItemVOS.
                    stream().
                    filter(OrderItemVO::getCheck).
                    map(item -> {
                        //更新最新价格
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);

                        return item;
                    }).
                    collect(Collectors.toList());
            orderConfirmVO.setItems(items);
        }, executor).thenRunAsync(() -> {
            //2.2查询库存信息
            List<OrderItemVO> items = orderConfirmVO.getItems();
            //获取所有商品id
            List<Long> ids = items.stream().map(OrderItemVO::getSkuId).collect(Collectors.toList());
            //远程查询库存
            R r = wareFeignService.getSkusHasStock(ids);
            List<SkuHasStockTO> hasStockTOS = r.getData(new TypeReference<List<SkuHasStockTO>>() {});
            //更新库存
            List<OrderItemVO> collect = items.stream().map(item -> {
                for (SkuHasStockTO to : hasStockTOS) {
                    if (item.getSkuId().equals(to.getSkuId())) {
                        item.setHasStock(to.getHasStock());
                    }
                }
                return item;
            }).collect(Collectors.toList());
            orderConfirmVO.setItems(collect);
        }, executor);

        //3.获取用户积分
        Integer integration = memberEntity.getIntegration();
        orderConfirmVO.setIntegration(integration);

        //4.防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //redis一个,页面一个
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntity.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVO.setOrderToken(token);

        //保证所有线程完成
        CompletableFuture.allOf(memberFuture, orderItemFuture).join();

        System.out.println(orderConfirmVO);
        return orderConfirmVO;
    }

//    @GlobalTransactional
    @Transactional
    @Override
    public OrderSubmitResponseVO submitOrder(OrderSubmitVO vo) {
        submitVOThreadLocal.set(vo);

        OrderSubmitResponseVO responseVO = new OrderSubmitResponseVO();
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();

        //1.验证令牌(令牌的对比和删除必须保持原子性)
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderSn = vo.getOrderToken();
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntity.getId()),
                orderSn);
        if (execute != 1L) {
            //令牌验证失败
            responseVO.setCode(1);
            responseVO.setMsg("令牌验证失败");
            return responseVO;
        }
        //2.创建订单
        OrderCreateTO order = createOrder();
        //3.验证价格
        BigDecimal payAmount = order.getOrder().getPayAmount();
        BigDecimal payPrice = vo.getPayPrice();
        //3.1 杜绝浮点数误差
        if(Math.abs(payAmount.subtract(payPrice).doubleValue()) > 1){
            //价格不一致
            responseVO.setCode(2);
            responseVO.setMsg("价格不一致");
//            return responseVO;
        }
        //4.保存订单
        savaOrder(order);
        //5.锁库存,只要有异常就回滚订单数据
        WareSkuLockVO lockVO = new WareSkuLockVO();
        lockVO.setOrderSn(order.getOrder().getOrderSn());

        List<OrderItemVO> collect = order.getOrderItems().stream().map(item -> {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, orderItemVO);
            orderItemVO.setCount(item.getSkuQuantity());
            orderItemVO.setTitle(item.getSkuName());
            return orderItemVO;
        }).collect(Collectors.toList());
        lockVO.setLocks(collect);

        R r = wareFeignService.orderLockStock(lockVO);
        if(r.getCode() != 0){
            //锁定库存失败
            responseVO.setCode(3);
            return responseVO;
        }
        //锁定库存成功
        responseVO.setOrder(order.getOrder());
        responseVO.setCode(0);
        //订单创建成功 发送给mq
        try {
            //TODO 保证消息一定发送出去,每一个都要有日志记录(给数据库保存每一个消息的详细信息)
            rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
        }catch (Exception e){
            //TODO 重试
            log.error("发送订单创建事件异常", e);
        }
        return responseVO;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);
        //总额
        BigDecimal payAmount = orderEntity.getPayAmount().setScale(2, RoundingMode.UP);
        payVo.setTotal_amount(payAmount.toString());
        //订单号
        payVo.setOut_trade_no(orderSn);
        //订单名称
        List<OrderItemEntity> orderItemEntityList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity itemEntity = orderItemEntityList.get(0);
        payVo.setSubject("mymall");
//        payVo.setSubject(itemEntity.getSkuName());
        //订单描述
//        payVo.setBody(itemEntity.getSkuAttrsVals());
        payVo.setBody("mymall Goods");

        return payVo;
    }

    /**
     * 保存订单数据
     * @param order
     */
    private void savaOrder(OrderCreateTO order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItemEntities = order.getOrderItems();
        orderItemService.saveBatch(orderItemEntities);
    }

    private OrderCreateTO createOrder() {
        OrderCreateTO orderCreateTO = new OrderCreateTO();
        //1.生成订单号
        String orderSn = IdWorker.getTimeId();
        //2.创建订单
        OrderEntity order = buildOrder(orderSn);
        orderCreateTO.setOrder(order);
        //3.获取所有的订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        orderCreateTO.setOrderItems(orderItemEntities);
        //4.计算价格相关数据
        computePrice(order, orderItemEntities);

        return orderCreateTO;
    }

    /**
     * 计算订单价格
     * @param order
     * @param orderItemEntities
     */
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItemEntities) {
        //1.订单相关价格
        //1订单总额
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal coupon = BigDecimal.ZERO;
        BigDecimal integration = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        Integer giftIntegration = 0;
        Integer giftGrowth = 0;
        for (OrderItemEntity orderItem : orderItemEntities) {
            total = total.add(orderItem.getRealAmount());
            BigDecimal couponAmount = orderItem.getCouponAmount();
            coupon = coupon.add(couponAmount);
            BigDecimal integrationAmount = orderItem.getIntegrationAmount();
            integration = integration.add(integrationAmount);
            BigDecimal promotionAmount = orderItem.getPromotionAmount();
            promotion = promotion.add(promotionAmount);
            //订单项的积分和成长值
            giftGrowth += orderItem.getGiftGrowth();
            giftIntegration += orderItem.getGiftIntegration();
        }
        order.setTotalAmount(total);
        //2 优惠券优惠
        order.setCouponAmount(coupon);
        //3 积分优惠
        order.setIntegrationAmount(integration);
        //4 促销优惠
        order.setPromotionAmount(promotion);
        //5 应付总额
        BigDecimal add = order.getFreightAmount().add(order.getFreightAmount());
        BigDecimal subtract = order.getFreightAmount().subtract(order.getFreightAmount());
        BigDecimal payAmount = total.add(order.getFreightAmount()).
                subtract(coupon).
                subtract(integration).
                subtract(promotion);
        order.setPayAmount(payAmount);
        //6.订单成长值等信息
        order.setGrowth(giftGrowth);
        order.setIntegration(giftIntegration);

    }

    /**
     * 构建orderEntity
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberEntity memberEntity = LoginUserInterceptor.loginUser.get();
        OrderSubmitVO orderSubmitVO = submitVOThreadLocal.get();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        //2.订单状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //3.获取收货地址等信息
        R r = wareFeignService.getFare(orderSubmitVO.getAddrId());
        FareTO fareTO = r.getData(new TypeReference<FareTO>() {});
        orderEntity.setFreightAmount(fareTO.getFare());
        MemberAddressVO address = fareTO.getAddress();
        orderEntity.setReceiverCity(address.getCity());
        orderEntity.setReceiverDetailAddress(address.getDetailAddress());
        orderEntity.setReceiverName(address.getName());
        orderEntity.setReceiverPhone(address.getPhone());
        orderEntity.setReceiverPostCode(address.getPostCode());
        orderEntity.setReceiverProvince(address.getProvince());
        orderEntity.setReceiverRegion(address.getRegion());
        //4.订单的用户信息
        orderEntity.setMemberId(memberEntity.getId());
        orderEntity.setMemberUsername(memberEntity.getUsername());

        //5.订单的相关信息
        orderEntity.setCreateTime(new Date());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setDeleteStatus(0);//0代表未删除

        return orderEntity;
    }

    //构建整个订单项
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVO> orderItemVOS = wareFeignService.currentUserCheckedCartItems();
        if(CollectionUtils.isEmpty(orderItemVOS)){
            throw new RuntimeException("购物车为空");
        }
        List<OrderItemEntity> orderItemEntities = orderItemVOS.stream().map(item -> {
            OrderItemEntity itemEntity = buildOrderItem(item);
            itemEntity.setOrderSn(orderSn);

            return itemEntity;
        }).collect(Collectors.toList());

        return orderItemEntities;
    }

    //构建每一个订单项
    private OrderItemEntity buildOrderItem(OrderItemVO item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1.订单信息

        //2.商品SPU信息
        Long skuId = item.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoEntity spuInfoEntity = r.getData(new TypeReference<SpuInfoEntity>() {});
        orderItemEntity.setSpuId(spuInfoEntity.getId());
        orderItemEntity.setSpuName(spuInfoEntity.getSpuName());
        orderItemEntity.setSpuPic(spuInfoEntity.getSpuDescription());
        orderItemEntity.setCategoryId(spuInfoEntity.getCatalogId());
        //2.1.商品品牌名
        R brandR = productFeignService.getBrandInfo(spuInfoEntity.getBrandId());
        BrandEntity brand = brandR.getDataByKey("brand", new TypeReference<BrandEntity>() {});
        orderItemEntity.setSpuBrand(brand.getName());

        //3.商品SKu信息
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuPic(item.getDefaultImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        String skuAttrsVals = StringUtils.collectionToDelimitedString(item.getSkuSaleVO(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrsVals);
        orderItemEntity.setSkuQuantity(item.getCount());
        //4.商品优惠信息(不实现)
        //5.积分信息
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());

        //6.商品价格信息
        //TODO 6.1促销价格
        orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
        //TODO 6.2优惠券优惠
        orderItemEntity.setCouponAmount(BigDecimal.ZERO);
        //TODO 6.3积分优惠
        orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
        //6.4该商品的金额
        BigDecimal total = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        //减去优惠信息
        total = total.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(total);

        return orderItemEntity;
    }

}
