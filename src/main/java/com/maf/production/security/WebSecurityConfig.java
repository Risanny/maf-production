package com.maf.production.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maf.production.dto.ApiResponse;
import com.maf.production.security.jwt.AuthEntryPointJwt;
import com.maf.production.security.jwt.AuthTokenFilter;
import com.maf.production.security.services.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Регистрируем провайдер
        http.authenticationProvider(authenticationProvider());

        // Отключаем CSRF, делаем сессии stateless
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Обработка ошибок аутентификации и доступа
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authEx) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ApiResponse<?> error = ApiResponse.error("Необходима аутентификация: " + authEx.getMessage());
                    try {
                        createObjectMapper().writeValue(response.getOutputStream(), error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .accessDeniedHandler((request, response, accessEx) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ApiResponse<?> error = ApiResponse.error("Доступ запрещен: " + accessEx.getMessage());
                    try {
                        createObjectMapper().writeValue(response.getOutputStream(), error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
        );

        // Права доступа
        http.authorizeHttpRequests(auth -> auth
                // Раздача SPA и статики
                .requestMatchers(
                        "/", "/index.html", "/app.js", "/styles.css",
                        "/favicon.ico", "/uploads/**"
                ).permitAll()
                // Логин/регистрация и тест публичный
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/public").permitAll()
                // Публичные GET-эндпоинты каталога
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/subcategories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/order").permitAll()
                .requestMatchers("/admin.html", "/admin.js").permitAll()
                .requestMatchers("/api/auth/signin").permitAll()
                // Всё остальное — по JWT
                .anyRequest().authenticated()
        );

        // JWT-фильтр перед проверкой логина
        http.addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
