package com.example.mymall.order.intercepotr;

import com.example.common.constant.AuthServerConstant;
import com.example.common.constant.CartConstant;
import com.example.common.to.MemberEntity;
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
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberEntity> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        MemberEntity attribute = (MemberEntity) session.getAttribute(AuthServerConstant.LOGIN_USER);

        if(attribute == null) {
            //如果没有登录了
            request.getSession().setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.mymall.com/login.html");
            return false;
        }
        //如果登录了
        loginUser.set(attribute);
        //放行
        return true;
    }
}
