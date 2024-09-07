package com.example.mymall.ware.intercepotr;

import com.example.common.constant.AuthServerConstant;
import com.example.common.constant.CartConstant;
import com.example.common.to.MemberEntity;
import com.example.mymall.ware.vo.UserInfoTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 在执行方法前,判断用户的登录状态,并封装好
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTO userInfoTO = new UserInfoTO();
        HttpSession session = request.getSession();
        MemberEntity loginUser = (MemberEntity) session.getAttribute(AuthServerConstant.LOGIN_USER);

        if(loginUser != null) {
            //如果登录了
            userInfoTO.setUserId(loginUser.getId());
        }
        //如果没有登录
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0) {
            for(Cookie cookie : cookies) {
                if(CartConstant.USER_KEY.equals(cookie.getName())) {
                    userInfoTO.setUserKey(cookie.getValue());
                    userInfoTO.setTempUser(true);
                    break;
                }
            }
        }

        if(StringUtils.isEmpty(userInfoTO.getUserKey())) {
            //生成一个临时用户
            String string = UUID.randomUUID().toString();
            userInfoTO.setUserKey(string);
        }
        //将用户信息存入线程
        threadLocal.set(userInfoTO);
        //放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //保存cookie
        UserInfoTO userInfoTO = threadLocal.get();
        if(userInfoTO != null && StringUtils.isNotEmpty(userInfoTO.getUserKey()) && !userInfoTO.isTempUser()) {
            //查看是否有可用的cookie
            Cookie cookie = new Cookie(CartConstant.USER_KEY, userInfoTO.getUserKey());
            cookie.setDomain("myMall.com");
            cookie.setMaxAge(CartConstant.USER_KEY_TTL);
            response.addCookie(cookie);
        }
    }
}
