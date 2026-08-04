package com.gjq.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.gjq.train.common.context.LoginMemberContext;
import com.gjq.train.common.resp.MemberLoginResp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        //1. 获取请求头中的Token
        String token = request.getHeader("token");

        //2. 从Token中解析会员信息
        if (StrUtil.isNotBlank(token)) {
            JWT jwt = JWTUtil.parseToken(token);
            Object memberId = jwt.getPayload("id");
            Object mobile = jwt.getPayload("mobile");

            MemberLoginResp member = new MemberLoginResp();
            member.setId(Long.valueOf(memberId.toString()));
            member.setMobile(mobile.toString());

            //3. 将会员信息保存到登录上下文
            LoginMemberContext.setMember(member);
        }

        //4. 放行当前请求
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        //5. 请求结束后清除登录上下文
        LoginMemberContext.remove();
    }
}
