package com.example.mymall.product.vo;

import com.example.mymall.product.entity.SkuImagesEntity;
import com.example.mymall.product.entity.SkuInfoEntity;
import com.example.mymall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVO {
    //1.sku基本信息获取 pms_sku_info
    SkuInfoEntity info;
    //2.sku的图片信息 pms_sku_images
    List<SkuImagesEntity> images;
    //3.查询当前spu的销售属性组合 pms_sku_sale_attr_value
    List<ItemSaleAttrsVO> saleAttr;
    //4.查询当前spu的描述信息 pms_product_attr_value
    SpuInfoDescEntity desp;
    //5.获取spu的规格参数信息
    List<SpuItemAttrGroupVO> groupAttrs;

    //是否有货
    Boolean hasStock = false;

    @Data
    public static class SpuItemAttrGroupVO{
        private String groupName;
        private List<SpuBaseAttrsVO> attrs;
    }

    @Data
    public static class SpuBaseAttrsVO{
        private String attrName;
        private String attrValues;
    }
}
