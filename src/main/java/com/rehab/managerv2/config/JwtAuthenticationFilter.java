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
 * 真正适配 Spring Security 的 JWT 保安
 * 继承 OncePerRequestFilter 确保每次请求只过滤一次
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 获取我们 Vue 前端发来的 token (注意：前端请求头里叫 'token')
        String token = request.getHeader("token");

        // 2. 如果存在 token，就开始验真伪
        if (StringUtils.hasText(token)) {
            try {
                // 调用你的 JwtUtils 解析 token
                Claims claims = JwtUtils.getClaimsByToken(token);

                // 如果解析成功且没过期
                if (claims != null) {
                    String username = claims.getSubject();

                    // 🔥 核心关键点：告诉 Spring Security "这个人是合法的！"
                    // 必须把用户信息塞进 SecurityContext 里，否则 Spring Security 依然会报 403
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 解析失败（比如被篡改或过期），什么都不做，让 SecurityContext 保持为空
                // 这样后面的 Spring Security 就会自动拦截并返回 403
                System.out.println("Token 解析异常：" + e.getMessage());
            }
        }

        // 3. 无论如何，保安必须放行，让请求继续往下走。
        // 如果没权限，走到后面的 Spring Security 拦截器时自然会被踹出去(报403)
        filterChain.doFilter(request, response);
    }
}