package com.example.mymall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.product.dao.SpuImagesDao;
import com.example.mymall.product.entity.SpuImagesEntity;
import com.example.mymall.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Autowired
    private SpuImagesDao spuImagesDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, String[] images) {
        if(images == null || images.length == 0){
            return;
        }
        List<SpuImagesEntity> spuImagesEntities = new ArrayList<>();
        for (String image : images) {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setSpuId(id);
            spuImagesEntity.setImgUrl(image);
            spuImagesEntities.add(spuImagesEntity);
        }
        this.saveBatch(spuImagesEntities);
    }

}
