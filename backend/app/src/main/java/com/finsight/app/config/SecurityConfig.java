package com.finsight.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .csrf(csrf -> csrf
              .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
              .ignoringRequestMatchers("/auth/**", "/auth/register")
          )
          .sessionManagement(session -> session
              .maximumSessions(1)
              .maxSessionsPreventsLogin(false)
              .and()
              .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
              .invalidSessionUrl("/auth/login")
              .sessionFixation().migrateSession()
          )
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/auth/**", "/auth/register").permitAll()
              .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
              .anyRequest().authenticated()
          )
          .formLogin(AbstractHttpConfigurer::disable)
          .httpBasic(AbstractHttpConfigurer::disable);

      return http.build();
  }
}
