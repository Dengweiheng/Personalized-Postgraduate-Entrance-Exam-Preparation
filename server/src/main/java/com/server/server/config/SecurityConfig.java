package com.server.server.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.server.module.user.entity.SysUser;
import com.server.server.module.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SysUserMapper sysUserMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(e -> e.authenticationEntryPoint(noPopupEntryPoint()));
        return http.build();
    }

        @Bean
        public AuthenticationEntryPoint noPopupEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\",\"data\":null}");
        };
        }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, username)
                    .eq(SysUser::getStatus, 1)
                    .last("limit 1"));
            if (user == null) {
                throw new UsernameNotFoundException("user not found");
            }

            UserDetails details = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("admin".equalsIgnoreCase(user.getUsername()) ? "ADMIN" : "USER")
                    .build();
            return details;
        };
    }
}
