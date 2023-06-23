package com.tota.ecommercebackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private  JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter){
        this.jwtRequestFilter=jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class).authorizeHttpRequests(authorize -> authorize.requestMatchers("/product" ,"auth/register", "/auth/login", "auth/forgot",
                        "auth/reset", "/auth/verify").permitAll().anyRequest().authenticated())
                .cors(cors->cors.disable())
                .csrf(csrf-> csrf.disable());
        return http.build();
    }

}
