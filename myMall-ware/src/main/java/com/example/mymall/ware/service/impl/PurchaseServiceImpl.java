package com.example.mymall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.common.constant.WareConstant;
import com.example.mymall.ware.entity.PurchaseDetailEntity;
import com.example.mymall.ware.service.PurchaseDetailService;
import com.example.mymall.ware.service.WareSkuService;
import com.example.mymall.ware.vo.PurchaseDoneVO;
import com.example.mymall.ware.vo.PurchaseItemDoneVO;
import com.example.mymall.ware.vo.PurchaseMergeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.ware.dao.PurchaseDao;
import com.example.mymall.ware.entity.PurchaseEntity;
import com.example.mymall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void purchaseMerge(PurchaseMergeVO purchaseMergeVO) {
        Long purchaseId = purchaseMergeVO.getPurchaseId();
        //如果采购单id为空则 新建采购单
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setStatus(WareConstant.purchaseStatusEnum.CREATED.getCode());

            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }else {
            //检查采购单是不是新建或已分配状态
            PurchaseEntity byId = this.getById(purchaseId);
            if(byId.getStatus() != WareConstant.purchaseStatusEnum.CREATED.getCode() || byId.getStatus() != WareConstant.purchaseStatusEnum.ASSIGNED.getCode()){
                throw new RuntimeException("该采购单不是新建或已分配状态");
            }
        }
        //处理采购需求,加上采购单id
        List<Long> items = purchaseMergeVO.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detailEntities = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();

            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setStatus(WareConstant.purchaseDetailStatusEnum.ASSIGNED.getCode());

            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntities);

        //设置一下更新时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(purchaseId);
        this.updateById(purchaseEntity);
    }

    /**
     * [1,2,3,4]//采购单id
     * @param ids
     */
    @Transactional
    @Override
    public void received(List<Long> ids) {
        for(Long id : ids){
            //确认当前采购单为新建或已分配状态
            PurchaseEntity byId = this.getById(id);
            if(byId.getStatus() != WareConstant.purchaseStatusEnum.CREATED.getCode() && byId.getStatus() != WareConstant.purchaseStatusEnum.ASSIGNED.getCode()){
                throw new RuntimeException("该采购单不是新建或已分配状态,不能领取");
            }
            //修改采购单的状态
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setId(id);
            purchaseEntity.setStatus(WareConstant.purchaseStatusEnum.RECEIVE.getCode());
            this.updateById(purchaseEntity);

            //修改采购需求的状态
            List<PurchaseDetailEntity> detailEntityList = ListDetailByPurchaseId(id);
            for(PurchaseDetailEntity detail:detailEntityList){
                detail.setStatus(WareConstant.purchaseDetailStatusEnum.BUYING.getCode());
            }
            purchaseDetailService.updateBatchById(detailEntityList);
        }
    }

    private List<PurchaseDetailEntity> ListDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> detailEntityList = purchaseDetailService.list(
                new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id)
        );
        return detailEntityList;
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVO doneVO) {
        //确认当前采购单为已领取状态
        PurchaseEntity byId = this.getById(doneVO.getId());
        if(byId.getStatus() != WareConstant.purchaseStatusEnum.RECEIVE.getCode()){
            throw new RuntimeException("该采购单不是已领取状态,不能完成");
        }
        //确认采购单的所有采购需求都已经完成
        Integer purchaseStatus = WareConstant.purchaseStatusEnum.FINISH.getCode();

        List<PurchaseDetailEntity> detailEntityList = new ArrayList<>();
        for(PurchaseItemDoneVO item : doneVO.getItems()) {
            if(item.getStatus() != WareConstant.purchaseDetailStatusEnum.FINISH.getCode()) {
                purchaseStatus = WareConstant.purchaseStatusEnum.HASERROR.getCode();
            }
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getItemId());
            detailEntity.setStatus(item.getStatus());
            if(item.getStatus() == WareConstant.purchaseDetailStatusEnum.HASERROR.getCode()) {
                detailEntity.setReason(item.getReason());
            }else if(item.getStatus() == WareConstant.purchaseDetailStatusEnum.FINISH.getCode()) {
                //将成功采购的采购需求入库
                PurchaseDetailEntity byId1 = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId1.getSkuId(), byId1.getWareId(), byId1.getSkuNum());
            }
            detailEntityList.add(detailEntity);
        }
        //修改采购单的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setUpdateTime(new Date());
        purchaseEntity.setId(doneVO.getId());
        purchaseEntity.setStatus(purchaseStatus);
        this.updateById(purchaseEntity);

        //修改采购需求的状态
        purchaseDetailService.updateBatchById(detailEntityList);
    }

}
