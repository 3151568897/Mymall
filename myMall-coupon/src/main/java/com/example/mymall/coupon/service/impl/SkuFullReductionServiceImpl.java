package com.example.mymall.coupon.service.impl;

import com.example.common.to.MemberPriceVO;
import com.example.common.to.SkuReductionTO;
import com.example.mymall.coupon.entity.MemberPriceEntity;
import com.example.mymall.coupon.entity.SkuLadderEntity;
import com.example.mymall.coupon.service.MemberPriceService;
import com.example.mymall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.coupon.dao.SkuFullReductionDao;
import com.example.mymall.coupon.entity.SkuFullReductionEntity;
import com.example.mymall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTO skuReductionTO) {
        //1.保存满减打折,会员价 sms_sku_ladder、sms_sku_full_reduction、sms_member_price
        //1.1 sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTO.getSkuId());
        skuLadderEntity.setDiscount(skuReductionTO.getDiscount());
        skuLadderEntity.setFullCount(skuReductionTO.getFullCount());
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if(skuReductionTO.getFullCount() > 0){
            skuLadderService.save(skuLadderEntity);
        }

        //1.2 sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO, skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0){
            this.save(skuFullReductionEntity);
        }

        //1.3 sms_member_price
        List<MemberPriceEntity> memberPriceEntities = new ArrayList<>();
        List<MemberPriceVO> memberPriceVOS = skuReductionTO.getMemberPrice();
        for (MemberPriceVO memberPriceVO : memberPriceVOS) {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPriceVO.getId());
            memberPriceEntity.setMemberLevelName(memberPriceVO.getName());
            memberPriceEntity.setMemberPrice(memberPriceVO.getPrice());
            //判断价格是否大于0,大于0才有意义
            if(memberPriceVO.getPrice().compareTo(new BigDecimal("0")) > 0){
                memberPriceEntities.add(memberPriceEntity);
            }
        }
        memberPriceService.saveBatch(memberPriceEntities);
    }

}
