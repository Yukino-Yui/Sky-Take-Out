package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     * 定义拦截器拦截后要做什么逻辑，比如校验 token
     * 拦截器是 SpringMVC 规定的接口，这个代码是实现管理端拦截器的逻辑
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            //非动态方法比如图片等静态资源
            return true;
        }

        //1、从请求头中获取token
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            //JwtUtil.parseJWT方法已经完成了校验，校验通过就会返回JWT<Claims>
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            //取出empId并转换类型，转成Long
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());

            //把empId放进 ThreadLocal，以后任何操作通过BaseContext.getCurrentId()就知道是谁在发起请求
            BaseContext.setCurrentId(empId);

            log.info("当前员工id：{}", empId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
