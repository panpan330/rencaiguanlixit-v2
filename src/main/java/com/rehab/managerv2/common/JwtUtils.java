package com.rehab.managerv2.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 数字通行证“印钞机”（企业级动态配置版）
 */
@Component // 1. 极其关键：必须加上这个注解，让 Spring 来接管这个类
public class JwtUtils {

    // 这里不再直接赋值，而是等待 Spring 注入
    private static long expireTime;
    private static Key KEY;

    /**
     * 2. 核心技巧：利用非静态的 set 方法，读取 yml 里的值，然后赋给静态变量！
     * 面试官要是问“如何给静态变量注入配置”，你就把这套连招砸他脸上。
     */
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        // 读取到乱码后，立即转化为加密算法所需的 Key 对象
        JwtUtils.KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.expire-time}")
    public void setExpireTime(long expireTime) {
        JwtUtils.expireTime = expireTime;
    }

    /**
     * 功能一：发证 (代码逻辑不变，但此时用的已经是 yml 里的动态数据了)
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 功能二：验真伪 (代码逻辑不变)
     */
    public static Claims getClaimsByToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}