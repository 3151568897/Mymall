package com.example.mymall;

import com.example.mymall.product.entity.BrandEntity;
import com.example.mymall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyMallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setDescript("华为手机品牌");
        brandEntity.setShowStatus(1);
        brandService.save(brandEntity);
    }

}
