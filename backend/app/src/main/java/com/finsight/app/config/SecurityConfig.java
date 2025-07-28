package com.finsight.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          .csrf(AbstractHttpConfigurer::disable)
//          .csrf(csrf -> csrf
//              .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//              .ignoringRequestMatchers("/auth/**", "/auth/register")
//          )
          .sessionManagement(session -> session
              .maximumSessions(1)
              .maxSessionsPreventsLogin(false)
              .and()
              .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
              .invalidSessionUrl("/auth/login")
              .sessionFixation().migrateSession()
          )
          .authorizeHttpRequests(auth -> auth
//              .requestMatchers("/auth/**", "/auth/register").permitAll()
//              .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
              .anyRequest().permitAll()
          )
          .formLogin(AbstractHttpConfigurer::disable)
          .httpBasic(AbstractHttpConfigurer::disable);

      return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();

      configuration.setAllowedOrigins(Arrays.asList(
          "http://localhost:5173"
      ));

      configuration.setAllowedMethods(Arrays.asList(
          "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
      ));

      configuration.setAllowedHeaders(List.of("*"));

      configuration.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);

      return source;
  }
}
