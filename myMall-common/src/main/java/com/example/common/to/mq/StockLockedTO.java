package com.example.common.to.mq;

import com.example.common.to.StockDetailTO;
import lombok.Data;

@Data
public class StockLockedTO {

    private Long id; //库存工作单

    private StockDetailTO detail; //工作单详情
}
