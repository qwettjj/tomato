package com.example.tomatomall.config;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    TokenUtil tokenUtil;

    // 定义不需要登录认证的路径白名单
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/alipay/notify",       // 支付宝回调接口
            "/swagger-ui.html",     // Swagger UI
            "/swagger-resources/**", // Swagger资源
            "/v2/api-docs",          // API文档
            "/webjars/**",           // Webjars
            "/doc.html",             // Knife4j文档
            "/favicon.ico",          // 网站图标
            "/error"                 // 错误页面
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 检查请求路径是否在白名单中
        for (String excludePath : EXCLUDE_PATHS) {
            if (pathMatches(requestURI, excludePath)) {
                return true; // 放行白名单请求
            }
        }

        // 需要认证的请求
        String token = request.getHeader("token");
        if (token != null && tokenUtil.verifyToken(token)) {
            request.getSession().setAttribute("currentAccount", tokenUtil.getUser(token));
            return true;
        } else {
            throw TomatoMallException.notLogin();
        }
    }

    /**
     * 匹配路径模式 (支持简单通配符)
     * @param requestPath 请求路径
     * @param pattern 模式 (如 /api/**)
     * @return 是否匹配
     */
    private boolean pathMatches(String requestPath, String pattern) {
        if (pattern.endsWith("/**")) {
            String basePath = pattern.substring(0, pattern.length() - 3);
            return requestPath.startsWith(basePath);
        } else {
            return requestPath.equals(pattern);
        }
    }
}