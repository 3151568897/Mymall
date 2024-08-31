package com.example.mymall.product.feign;

import com.example.common.to.es.SkuEsModel;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("myMall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    public R productStuckUp(@RequestBody List<SkuEsModel> skuEsModels);
}
