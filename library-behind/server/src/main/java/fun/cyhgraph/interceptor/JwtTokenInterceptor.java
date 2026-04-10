package fun.cyhgraph.interceptor;

import fun.cyhgraph.constant.JwtClaimsConstant;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.context.BaseContext;
import fun.cyhgraph.properties.JwtProperties;
import fun.cyhgraph.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT token interceptor.
 * Manager token can access management endpoints.
 * Reader token can access /reader/client/** endpoints only.
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getManagerTokenName());
        String uri = request.getRequestURI();
        boolean readerPath = uri.startsWith("/reader/client");

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getManagerSecretKey(), token);
            Integer managerId = claims.get(JwtClaimsConstant.MANAGER_ID, Integer.class);
            Integer readerId = claims.get(JwtClaimsConstant.READER_ID, Integer.class);

            if (readerPath) {
                if (readerId == null) {
                    response.setStatus(403);
                    response.getWriter().write(MessageConstant.FORBIDDEN);
                    return false;
                }
                BaseContext.setCurrentId(readerId);
            } else {
                if (managerId == null) {
                    response.setStatus(403);
                    response.getWriter().write(MessageConstant.FORBIDDEN);
                    return false;
                }
                BaseContext.setCurrentId(managerId);
            }
            return true;
        } catch (Exception ex) {
            log.warn("jwt verify failed, uri={}, error={}", uri, ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
