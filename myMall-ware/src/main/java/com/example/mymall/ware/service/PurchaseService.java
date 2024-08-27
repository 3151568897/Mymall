package com.example.mymall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.mymall.ware.entity.PurchaseEntity;
import com.example.mymall.ware.vo.PurchaseDoneVO;
import com.example.mymall.ware.vo.PurchaseMergeVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:10:48
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageWithUnreceive(Map<String, Object> params);

    /**
     * 合并采购需求
     * @param purchaseMergeVO
     */
    void purchaseMerge(PurchaseMergeVO purchaseMergeVO);

    void received(List<Long> ids);

    void done(PurchaseDoneVO doneVO);
}

