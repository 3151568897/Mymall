package com.example.mymall.member.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.common.exception.LoginException;
import com.example.common.exception.PhoneExistException;
import com.example.common.exception.UsernameExistException;
import com.example.common.utils.HttpUtils;
import com.example.mymall.member.entity.MemberLevelEntity;
import com.example.mymall.member.service.MemberLevelService;
import com.example.mymall.member.vo.UserLoginVO;
import com.example.mymall.member.vo.UserRegisterVO;
import com.example.mymall.member.vo.WeiboSocialUserVO;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
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

        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());
        memberEntity.setGrowth(0);
        memberEntity.setIntegration(0);
        //1.2设置默认头像
        //1.3设置默认昵称
        memberEntity.setNickname("默认用户");

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

    @Override
    public MemberEntity weiboLogin(WeiboSocialUserVO vo) throws Exception {
        //登录注册合并逻辑
        //1.如果用户是第一次登录 那就自动注册(生成会员信息) 根据uid
        String uid = vo.getUid();
        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("weibo_uid", uid));
        if(member != null){
            //2.如果用户不是第一次登录 那就直接登录
            //2.1 更新凭证
            member.setWeiboAccessToken(vo.getAccess_token());
            member.setWeiboExpiresIn(vo.getExpires_in());
            saveOrUpdate(member);
            //3.返回用户信息
            return member;
        }
        //2.自动注册
        MemberEntity memberEntity = new MemberEntity();
        //2.1设置社交登录信息
        memberEntity.setWeiboUid(uid);
        memberEntity.setWeiboAccessToken(vo.getAccess_token());
        memberEntity.setWeiboExpiresIn(vo.getExpires_in());
        memberEntity.setCreateTime(new Date());
        //2.2从微博获取用户基本信息
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", vo.getAccess_token());
        map.put("uid", vo.getUid());
        try{
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com",
                    "/2/users/show.json",
                    "get",
                    new HashMap<>(),
                    map);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new LoginException("获取用户微博信息失败");
            }
            String json = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(json);
            //获取用户数据
            memberEntity.setNickname(jsonObject.getString("name"));
            memberEntity.setGender("m".equals(jsonObject.getString("gender")) ? 1 : 0);
            memberEntity.setHeader(jsonObject.getString("profile_image_url"));
            memberEntity.setCity(jsonObject.getString("location"));
        }catch (Exception e){
            e.printStackTrace();
            throw new LoginException("获取用户微博信息失败");
        }
        //2.3设置默认等级
        MemberLevelEntity defaultStatus = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(defaultStatus.getId());
        //2.4设置默认状态
        memberEntity.setStatus(0);
        memberEntity.setGrowth(0);
        memberEntity.setIntegration(0);

        //添加到数据库
        baseMapper.insert(memberEntity);

        //3.返回用户信息
        return memberEntity;
    }

}
