package com.example.mymall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog2VO {
    //一级分类id
    private Long catalog1Id;
    //三级分类
    private List<Catalog3VO> catalog3List;

    private String name;

    private Long id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3VO{
        private Long catalog2Id;//二级分类id
        private String name;
        private Long id;
    }
}
