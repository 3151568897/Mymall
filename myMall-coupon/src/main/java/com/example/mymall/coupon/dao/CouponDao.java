package com.example.mymall.coupon.dao;

import com.example.mymall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:44:34
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
