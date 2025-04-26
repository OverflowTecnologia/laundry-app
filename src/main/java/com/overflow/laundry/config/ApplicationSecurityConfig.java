package com.overflow.laundry.config;

import com.overflow.laundry.exception.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;


@Component
@Profile("!local")
public class ApplicationSecurityConfig {

  String[] whitelistEndpoints = {
      "/farewell", "/home"
  };
  String[] protectedEndpoints = {
      "/machines", "/machines/**", "/condominiums", "/condominiums/**"
  };

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CognitoRoleConverter());
    http.sessionManagement(sessionConfig ->
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((requests) -> requests
        .requestMatchers(protectedEndpoints).hasRole("laundry-manager")
        .requestMatchers(whitelistEndpoints).permitAll());
    http.oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer ->
        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    http.exceptionHandling(ehc ->
        ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
    return http.build();
  }

}
