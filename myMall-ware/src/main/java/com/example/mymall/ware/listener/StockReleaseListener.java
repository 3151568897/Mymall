package com.example.mymall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.constant.OrderStatusEnum;
import com.example.common.to.StockDetailTO;
import com.example.common.to.mq.OrderTO;
import com.example.common.to.mq.StockLockedTO;
import com.example.common.utils.R;
import com.example.mymall.ware.entity.WareOrderTaskDetailEntity;
import com.example.mymall.ware.entity.WareOrderTaskEntity;
import com.example.mymall.ware.entity.WareSkuEntity;
import com.example.mymall.ware.feign.OrderFeignService;
import com.example.mymall.ware.service.WareOrderTaskDetailService;
import com.example.mymall.ware.service.WareOrderTaskService;
import com.example.mymall.ware.service.WareSkuService;
import com.example.mymall.ware.vo.OrderVO;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private WareOrderTaskService wareOrderTaskService;
    @Autowired
    private WareSkuService wareSkuService;

    //处理库存释放逻辑
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTO to, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的信息：" + to);
        Long id = to.getId();
        StockDetailTO detail = to.getDetail();
        Long skuId = detail.getSkuId();
        Long detailId = detail.getId();
        //1.因为异常导致订单回滚,所以需要解锁库存
        //1.1查询数据库 库存详情,判断是因为库存锁定失败导致的异常,还是其他异常
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detailId);
        if(detailEntity == null){
            //没有库存详细信息,删除mq中数据
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        //有库存详细信息,证明库存锁定成功了
        //1.2 查看订单情况,先获得订单号
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
        String orderSn = taskEntity.getOrderSn();
        R r = orderFeignService.getOrderStatus(orderSn);
        if(r.getCode() != 0){
            //远程调用失败的情况下,拒绝消息并且重新放回队列,让其他机器继续解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            throw new RuntimeException("远程调用失败");
        }
        OrderVO orderVO = r.getData(new TypeReference<OrderVO>() {});
        //1.2.1 如果订单不存在
        if(orderVO == null){
            //订单不存在的情况下,删除mq中数据
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        // 如果订单状态不是已经取消
        if(!Objects.equals(OrderStatusEnum.CANCLED.getCode(), orderVO.getStatus())){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            return;
        }
        //当前库存工作单必须是已锁定的才可以解锁
        if(detailEntity.getLockStatus() != 1){
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        //1.2.2 如果订单没有,那就必须解锁
        //2.解锁库存
        unlockStock(skuId, detailEntity.getWareId(), detailEntity.getSkuNum());
        //更新库存工作单的状态
        detailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(detailEntity);
        //解锁的情况下,删除mq中数据
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void unlockStock(Long skuId, Long wareId, Integer skuNum) {
        //解锁库存
        WareSkuEntity wareSkuEntity = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        wareSkuEntity.setStockLocked(wareSkuEntity.getStockLocked() - skuNum);
        wareSkuService.updateById(wareSkuEntity);
    }

    /**
     * 这个目的是放在订单服务卡顿,导致订单状态一直改不了,库存优先到期,什么都做不了
     */
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTO order, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁库存：" + order);
        //根据订单id获取仓库工作单
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getOne(new QueryWrapper<WareOrderTaskEntity>().eq("order_sn", order.getOrderSn()));
        Long taskId = taskEntity.getId();
        //根据仓库工作单获取仓库工作详情单
        List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId));
        if(detailEntities == null || detailEntities.size() == 0){
            //没有库存详细信息,删除mq中数据
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
            Long skuId = detailEntity.getSkuId();
            //1.因为异常导致订单回滚,所以需要解锁库存
            //1.2 查看订单情况,先获得订单号
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            //当前库存工作单必须是已锁定的才可以解锁
            if(detailEntity.getLockStatus() != 1){
                continue;
            }
            //1.2.2 如果订单没有,那就必须解锁
            //2.解锁库存
            unlockStock(skuId, detailEntity.getWareId(), detailEntity.getSkuNum());
            //更新库存工作单的状态
            detailEntity.setLockStatus(2);
            wareOrderTaskDetailService.updateById(detailEntity);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
