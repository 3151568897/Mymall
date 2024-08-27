package com.example.mymall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.example.common.valid.ListValue;
import com.example.mymall.product.entity.AttrEntity;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuInfoSaveVO {
    /**
     * 商品名称
     */
    @NotEmpty
    private String spuName;
    /**
     * 商品描述
     */
    @NotEmpty
    private String spuDescription;
    /**
     * 所属分类id
     */
    @NotNull
    private Long catalogId;
    /**
     * 品牌id
     */
    @NotNull
    private Long brandId;
    /**
     *商品重量
     */
    @NotNull
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    @ListValue(vals = {0, 1})
    private Integer publishStatus;
    /**
     * 展示图集
     */
    @NotEmpty
    private String[] decript;
    /**
     * 图集
     */
    @NotEmpty
    private String[] images;
    /**
     * 成长值奖励之类的
     */
    private SpuInfoBoundsSaveVO bounds;
    /**
     * spu属性集合
     */
    private List<SpuInfoBaseAttrsSaveVO> baseAttrs;
    /**
     * sku集合
     */
    @NotEmpty
    private List<SkuVO> skus;
}
