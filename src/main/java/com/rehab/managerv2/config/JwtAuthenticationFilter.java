package com.rehab.managerv2.config;

import com.rehab.managerv2.common.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 保安：拦截每一个请求，检查通行证
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 从 HTTP 请求头中获取名为 "Authorization" 的字段
        String header = request.getHeader("Authorization");

        // 2. 国际惯例：JWT 的 Token 通常以 "Bearer " 开头
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            // 剔除前缀，只保留后面的乱码字符串
            String token = header.substring(7);

            // 3. 呼叫“印钞机”验真伪
            Claims claims = JwtUtils.getClaimsByToken(token);
            if (claims != null) {
                // 如果 Token 是真的，且没过期，拿到用户名
                String username = claims.getSubject();

                // 4. 重点：告诉 Spring Security 总部，这哥们身份合法，记录在案！
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 5. 检查完毕，放行（如果上面没通过，身份信息为空，后面会被总部拦死）
        filterChain.doFilter(request, response);
    }
}