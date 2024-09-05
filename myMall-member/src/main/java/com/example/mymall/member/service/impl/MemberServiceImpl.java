package com.example.mymall.member.service.impl;

import com.example.common.exception.PhoneExistException;
import com.example.common.exception.UsernameExistException;
import com.example.mymall.member.entity.MemberLevelEntity;
import com.example.mymall.member.service.MemberLevelService;
import com.example.mymall.member.vo.UserLoginVO;
import com.example.mymall.member.vo.UserRegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.mymall.member.dao.MemberDao;
import com.example.mymall.member.entity.MemberEntity;
import com.example.mymall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(UserRegisterVO vo) {
        MemberEntity memberEntity = new MemberEntity();
        //检查用户名和手机号的唯一性
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUsername());
        //设置注册信息
        memberEntity.setUsername(vo.getUsername());
        memberEntity.setMobile(vo.getPhone());
        //密码不可逆加密 md5盐值加密
        String encode = new BCryptPasswordEncoder().encode(vo.getPassword());
        memberEntity.setPassword(encode);
        //1设置默认信息
        //1.1设置默认等级
        MemberLevelEntity defaultStatus = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(defaultStatus.getId());

        memberEntity.setStatus(0);
        memberEntity.setCreateTime(new Date());
        memberEntity.setGrowth(0);
        memberEntity.setIntegration(0);

        //添加到数据库
        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile > 0){
            throw new PhoneExistException("手机号已经被注册");
        }
    }

    @Override
    public void checkUsernameUnique(String username)  throws UsernameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(count > 0){
            throw new UsernameExistException("用户名已经被注册");
        }
    }

    @Override
    public void checkEmailUnique(String email) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("email", email));
        if(count > 0){
            throw new RuntimeException("邮箱已经被注册");
        }
    }

    @Override
    public MemberEntity login(UserLoginVO vo) {
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginacct())
                .or()
                .eq("mobile", vo.getLoginacct()));

        if(memberEntity == null){
            //没有注册
            return null;
        }
        //比较密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(encoder.matches(vo.getPassword(), memberEntity.getPassword())){
            return memberEntity;
        }else {
            return null;
        }
    }

}
