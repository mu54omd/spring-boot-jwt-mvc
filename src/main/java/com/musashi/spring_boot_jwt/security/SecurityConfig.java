package com.musashi.spring_boot_jwt.security;

import com.musashi.spring_boot_jwt.service.JwtAuthFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests( auth ->
                        auth
                                .requestMatchers("/")
                                .permitAll()
                                .requestMatchers("/auth/**")
                                .permitAll()
                                .dispatcherTypeMatchers(
                                        DispatcherType.ERROR,
                                        DispatcherType.FORWARD)
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                        )
                .exceptionHandling(configurer ->
                        configurer
//                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                                .authenticationEntryPoint(
                                        (request, response, authException) ->
                                                response.sendRedirect("/auth/login?authenticate")
                                )
                                .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
