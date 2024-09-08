package com.example.mymall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.example.common.to.FareTO;
import com.example.common.to.MemberAddressVO;
import com.example.common.utils.R;
import com.example.mymall.ware.feign.MemberFeignService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.ware.dao.WareInfoDao;
import com.example.mymall.ware.entity.WareInfoEntity;
import com.example.mymall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(t ->
                    t.eq("id", key).or().like("name", key))
                    .or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareTO getFare(Long addrId) {
        FareTO fareTO = new FareTO();

        R r = memberFeignService.getMemberAddressInfo(addrId);
        if(r.getCode() != 0){
            throw new RuntimeException("获取用户收货地址异常");
        }
        MemberAddressVO address = r.getDataByKey("memberReceiveAddress", new TypeReference<MemberAddressVO>() {});
        if(address == null){
            throw new RuntimeException("用户收货地址不存在");
        }
        MemberAddressVO memberAddressVO = new MemberAddressVO();
        BeanUtils.copyProperties(address, memberAddressVO);
        fareTO.setAddress(memberAddressVO);

        //TODO 运费计算 现在先用两位随机数
        BigDecimal fare = new BigDecimal(RandomUtils.nextInt(new Random(), 20));
        fareTO.setFare(fare);

        return fareTO;
    }

}
