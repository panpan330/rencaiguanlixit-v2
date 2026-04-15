package com.rehab.managerv2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心规则配置（保安队规）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 把刚才那个 BCrypt 密码比对器注册成一个全局 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF 防护（因为我们要用纯正的 JWT，不需要这玩意）
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 禁用 Session（告诉服务器别拿小本本记名字了，全面启用无状态 JWT 模式）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 核心：接口权限规则
                .authorizeHttpRequests(auth -> auth
                        // 给 /sys-user/login 接口开绿灯，任何人都能来尝试登录
                        .requestMatchers("/sys-user/login").permitAll()
                        // 其他所有请求，必须经过认真检查（带 Token）才能访问
                        .anyRequest().authenticated()
                )
                // 4. 把咱们刚写的 JwtAuthenticationFilter 保安，安插在官方默认密码保安的最前面
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}