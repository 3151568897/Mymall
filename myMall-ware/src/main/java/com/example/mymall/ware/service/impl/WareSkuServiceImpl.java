package com.example.mymall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.constant.OrderStatusEnum;
import com.example.common.exception.NoStockException;
import com.example.common.to.SkuHasStockTO;
import com.example.common.to.StockDetailTO;
import com.example.common.to.mq.StockLockedTO;
import com.example.common.utils.R;
import com.example.mymall.ware.entity.WareOrderTaskDetailEntity;
import com.example.mymall.ware.entity.WareOrderTaskEntity;
import com.example.mymall.ware.feign.OrderFeignService;
import com.example.mymall.ware.feign.ProductFeignService;
import com.example.mymall.ware.service.WareOrderTaskDetailService;
import com.example.mymall.ware.service.WareOrderTaskService;
import com.example.mymall.ware.vo.OrderItemVO;
import com.example.mymall.ware.vo.OrderVO;
import com.example.mymall.ware.vo.WareSkuLockVO;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.ware.dao.WareSkuDao;
import com.example.mymall.ware.entity.WareSkuEntity;
import com.example.mymall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId) && !"0".equalsIgnoreCase(skuId)){
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId) && !"0".equalsIgnoreCase(wareId)){
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuEntity wareSkuEntity = new WareSkuEntity();
        WareSkuEntity one = this.getOne(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId)
        );

        if(one == null){
            //1、判断如果没有这个库存记录新增
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程查询sku的名字
            try{
                R info = productFeignService.getSkuInfo(skuId);
                Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName(skuInfo.get("skuName").toString());
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
            this.save(wareSkuEntity);
        }else{
            //2、判断如果有这个库存记录更新
            one.setStock(one.getStock() + skuNum);
            this.updateById(one);
        }
    }

    @Override
    public List<SkuHasStockTO> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockTO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockTO skuHasStockVO = new SkuHasStockTO();

            //检查是否有库存 stockLocked代表被购买了多少
            Long count = baseMapper.getSkuStock(skuId);

            //保证count=null时 也可以返回数据
            skuHasStockVO.setHasStock(count != null && count > 0);
            skuHasStockVO.setSkuId(skuId);
            return skuHasStockVO;
        }).collect(Collectors.toList());

        return collect;
    }

    /**
     * 为某个订单锁定库存
     *
     * @param locks
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVO locks) {
        //保持库存工作单的详情
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(locks.getOrderSn());
        taskEntity.setCreateTime(new Date());

        wareOrderTaskService.save(taskEntity);

        //TODO 1.按照下单的收货地址 找到一个就近的仓库 锁定库存(现在直接找所有仓库)
        //2.找到所有没有锁定的库存，扣减库存
        List<OrderItemVO> orderIocks = locks.getLocks();
        //2.1找到有库存的仓库
        List<SkuWareHasStock> hasStock = orderIocks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            //查询
            List<Long> wareIds = baseMapper.ListWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        //3.锁定库存
        Boolean allLock = true;
        for (SkuWareHasStock stock : hasStock) {
            Boolean skuStocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareIds();
            if(wareIds == null || wareIds.size() == 0){
                //任何仓库都没有足够库存
                throw new NoStockException(skuId+"号商品没有库存,任何仓库都没有足够库存");
            }
            // 如果每个商品都锁定成功到还好
            // 如果锁定失败,那就前面保存的工作单就会回滚
            for (Long wareId : wareIds) {
                //成功就返回1,否则就是0
                Long count = baseMapper.lockSkuStock(skuId, wareId,stock.getNum());
                if(count == 1){
                    //锁定库存
                    skuStocked = true;
                    //告诉MQ库存锁定成功
                    WareOrderTaskDetailEntity detailEntity = WareOrderTaskDetailEntity.builder().
                            skuId(skuId).
                            wareId(wareId).
                            skuNum(stock.getNum()).
                            lockStatus(1).
                            taskId(taskEntity.getId()).
                            build();
                    wareOrderTaskDetailService.save(detailEntity);

                    StockLockedTO lockedTO = new StockLockedTO();
                    lockedTO.setId(taskEntity.getId());
                    //相当于每锁定一个库存都发送一次,
                    //全部数据都发送,是为了回滚找不到数据
                    StockDetailTO stockDetailTO = new StockDetailTO();
                    BeanUtils.copyProperties(detailEntity, stockDetailTO);
                    lockedTO.setDetail(stockDetailTO);

                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTO);
                    //锁定成功就break
                    break;
                }
            }
            if(!skuStocked){
                //当前商品没有锁定成功
                throw new NoStockException(skuId+"号商品没有库存");
            }
        }
        //4.所有成功的锁定信息

        return allLock;
    }

    /**
     * 这个商品在那个仓库有库存
     */
    @Data
    class SkuWareHasStock{
        private Long skuId;
        //锁定件数
        private Integer num;
        //仓库的id
        private List<Long> wareIds;
    }

}
