package com.example.mymall.ware.service.impl;

import com.example.common.utils.R;
import com.example.mymall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.ware.dao.WareSkuDao;
import com.example.mymall.ware.entity.WareSkuEntity;
import com.example.mymall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

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

}
