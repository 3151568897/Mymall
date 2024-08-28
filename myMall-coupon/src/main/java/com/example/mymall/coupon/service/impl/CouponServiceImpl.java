package com.example.mymall.coupon.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.coupon.dao.CouponDao;
import com.example.mymall.coupon.entity.CouponEntity;
import com.example.mymall.coupon.service.CouponService;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<CouponEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(t -> t
                    .eq("id", key)
                    .or().like("coupon_name", key)
            );
        }

        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}
