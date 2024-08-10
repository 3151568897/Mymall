package com.example.mymall.order.dao;

import com.example.mymall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:07:09
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
