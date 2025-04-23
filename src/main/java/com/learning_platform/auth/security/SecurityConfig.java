package com.learning_platform.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService; // ✅ Inject user service
    private final JwtUtil jwtUtil;  // ✅ Inject JwtUtil

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll() // Public endpoints
            .requestMatchers(HttpMethod.POST, "/enrollments/**").hasAuthority("ROLE_STUDENT")
            .requestMatchers(HttpMethod.GET, "/enrollments").hasAuthority("ROLE_STUDENT")
            .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT")
            .requestMatchers("/instructor/**").hasAuthority("ROLE_INSTRUCTOR")
            .requestMatchers("/courses/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_INSTRUCTOR", "ROLE_ADMIN")
            .anyRequest().authenticated()
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

    
    @Bean
    public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // ✅ Securely hash and validate passwords
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }
}
