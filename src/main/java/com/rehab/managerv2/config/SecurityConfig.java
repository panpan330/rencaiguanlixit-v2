package com.rehab.managerv2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 核心规则配置（最强保安队规）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启 Spring Security 官方跨域支持，它会自动去下面找 corsConfigurationSource
                .cors(Customizer.withDefaults())

                // 2. 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 3. 禁用 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 接口权限规则
                .authorizeHttpRequests(auth -> auth
                        // 必须无条件放行 OPTIONS 预检请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 放行登录等公共接口
                        .requestMatchers("/sys-user/login", "/sys-user/add").permitAll()
                        // 其他全部拦截
                        .anyRequest().authenticated()
                )
                // 5. 插入你的 JWT 过滤器
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 🔥 官方指定的跨域规则：Spring Security 会自动调用这个 Bean 来处理跨域
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 允许所有源
        configuration.addAllowedMethod("*");        // 允许所有请求方法
        configuration.addAllowedHeader("*");        // 允许所有请求头
        configuration.setAllowCredentials(true);    // 允许携带凭证
        configuration.setMaxAge(3600L);             // 缓存 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 映射到所有的接口路径
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}