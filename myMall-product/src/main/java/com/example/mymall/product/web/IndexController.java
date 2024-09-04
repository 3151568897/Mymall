package com.example.mymall.product.web;

import com.example.mymall.product.entity.CategoryEntity;
import com.example.mymall.product.service.CategoryService;
import com.example.mymall.product.vo.Catalog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        //查出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    //index/json/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalogJson() throws InterruptedException {
        Map<String, List<Catalog2VO>> map = categoryService.getCatalogJson();

        return map;
    }
}
