package com.example.mymall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseMergeVO {

    private Long purchaseId;

    @NotNull
    private List<Long> items;
}
