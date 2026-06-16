package com.example.order.config;

import com.example.order.common.BizException;
import com.example.order.context.UserContext;
import com.example.order.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 可选认证路径：有 token 则解析，无 token 也放行
     */
    private static final Set<String> OPTIONAL_AUTH_PATHS = Set.of(
            "/api/menu/tree"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String authorization = request.getHeader("Authorization");
        boolean hasToken = authorization != null && authorization.startsWith("Bearer ");

        if (hasToken) {
            Long userId = jwtUtil.parseUserId(authorization.substring(7));
            UserContext.setUserId(userId);
            return true;
        }

        // 无 token：如果是可选认证路径则放行，否则拒绝
        if (OPTIONAL_AUTH_PATHS.contains(path)) {
            return true;
        }

        throw BizException.unauthorized("请先登录");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
