package com.example.mymall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.exception.PhoneExistException;
import com.example.common.exception.UsernameExistException;
import com.example.common.utils.PageUtils;
import com.example.mymall.member.entity.MemberEntity;
import com.example.mymall.member.vo.UserLoginVO;
import com.example.mymall.member.vo.UserRegisterVO;
import com.example.mymall.member.vo.WeiboSocialUserVO;

import java.util.Map;

/**
 * 会员
 *
 * @author wupeng
 * @email wupeng@gmail.com
 * @date 2024-08-10 16:02:18
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVO vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    void checkEmailUnique(String email);

    MemberEntity login(UserLoginVO vo);

    MemberEntity weiboLogin(WeiboSocialUserVO vo) throws Exception;
}

