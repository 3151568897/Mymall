package com.example.mymall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, String[] images);
}

