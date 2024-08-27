package com.example.mymall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVO {

    @NotNull
    private Long id;
    @NotNull
    private List<PurchaseItemDoneVO> items;
}
