package com.example.mymall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.mymall.product.entity.AttrAttrgroupRelationEntity;
import com.example.mymall.product.entity.AttrEntity;
import com.example.mymall.product.service.AttrAttrgroupRelationService;
import com.example.mymall.product.service.AttrService;
import com.example.mymall.product.service.CategoryService;
import com.example.mymall.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mymall.product.entity.AttrGroupEntity;
import com.example.mymall.product.service.AttrGroupService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 属性分组
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-09 18:21:35
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 根据分类属性获取列表
     */
    @RequestMapping("/list/{catelogId}")
    public R listByCatelogId(@RequestParam Map<String, Object> params,
                             @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 获取详细信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }
    /**
     * /product/attrgroup/{catelogId}/withattr
     * 获取分类下所有分组&关联属性
     */
    @RequestMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupWithAttrsVO> attrGroupWithAttrs = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);

        return R.ok().put("data", attrGroupWithAttrs);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIdsWithoutCascade(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    /**
     * 批量关联
     */
    @RequestMapping("/attr/relation")
    public R relation(@RequestBody List<AttrAttrgroupRelationEntity> relationList){
        attrAttrgroupRelationService.saveBatch(relationList);
        return R.ok();
    }


    /**
     * 批量解除关联表
     */
    @RequestMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrAttrgroupRelationEntity> relationList){
        attrAttrgroupRelationService.removeByList(relationList);
        return R.ok();
    }
    /**
     * 批量查询关联表
     */
    @RequestMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> relationList = attrService.attrRelation(attrgroupId);
        return R.ok().put("data", relationList);
    }
    /**
     * 获取属性分组没有关联的其他属性
     */
    @RequestMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryNoRelationAttrPage(params, attrgroupId);
        return R.ok().put("page", page);
    }

}
