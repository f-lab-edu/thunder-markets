package com.tmarket.model.conf;

import com.tmarket.service.authentication.AuthenticationService;
import com.tmarket.service.authentication.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    private final AuthenticationService authenticationService;

    public SecurityConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(new MvcRequestMatcher(introspector, "/")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/products/list")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/h2-console/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, "/error")).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(authenticationService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
