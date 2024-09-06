package com.example.mymall.member.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.common.exception.LoginException;
import com.example.mymall.member.feign.CouponFeignService;
import com.example.mymall.member.vo.UserLoginVO;
import com.example.mymall.member.vo.UserRegisterVO;
import com.example.mymall.member.vo.WeiboSocialUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import com.example.mymall.member.entity.MemberEntity;
import com.example.mymall.member.service.MemberService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 会员
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:02:18
 */
@RefreshScope
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 注册功能
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVO vo) {
        memberService.register(vo);
        return R.ok();
    }
    /**
     * 登录功能
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVO vo) {
        MemberEntity member = memberService.login(vo);
        if(member != null) {
            return R.ok().put("data", member);
        } else {
            throw new LoginException("用户名或密码错误");
        }
    }
    /**
     * 微博社交登录功能
     */
    @PostMapping("/oauth2/weibo/login")
    public R weiboLogin(@RequestBody WeiboSocialUserVO vo) throws Exception {
        MemberEntity member = memberService.weiboLogin(vo);
        if(member != null) {
            return R.ok().put("data", member);
        } else {
            throw new LoginException("微博社交登录错误");
        }
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
