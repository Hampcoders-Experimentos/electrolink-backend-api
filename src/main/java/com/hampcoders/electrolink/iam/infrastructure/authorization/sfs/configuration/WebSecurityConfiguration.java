package com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.configuration;

import com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.hampcoders.electrolink.iam.infrastructure.hashing.bcrypt.BcryptHashingService;
import com.hampcoders.electrolink.iam.infrastructure.tokens.jwt.BearerTokenService;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Web security configuration class.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

  private final UserDetailsService userDetailsService;
  private final BearerTokenService tokenService;
  private final BcryptHashingService hashingService;
  private final AuthenticationEntryPoint unauthorizedRequestHandler;

  /**
   * Constructs the WebSecurityConfiguration.
   *
   * @param userDetailsService The user details the service.
   * @param tokenService The bearer token service.
   * @param hashingService The hashing service.
   * @param authenticationEntryPoint The authentication entry point.
   */
  public WebSecurityConfiguration(
      @Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService,
      BearerTokenService tokenService,
      BcryptHashingService hashingService,
      AuthenticationEntryPoint authenticationEntryPoint) {

    this.userDetailsService = userDetailsService;
    this.tokenService = tokenService;
    this.hashingService = hashingService;
    this.unauthorizedRequestHandler = authenticationEntryPoint;
  }

  /**
   * Defines the authorization request filter.
   *
   * @return The BearerAuthorizationRequestFilter instance.
   */
  @Bean
  public BearerAuthorizationRequestFilter authorizationRequestFilter() {
    return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
  }

  /**
   * Defines the authentication manager bean.
   *
   * @param authenticationConfiguration The authentication configuration.
   * @return The AuthenticationManager instance.
   * @throws Exception if configuration fails.
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * Defines the DAO authentication provider.
   *
   * @return The DaoAuthenticationProvider instance.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    var authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(hashingService);
    return authenticationProvider;
  }

  /**
   * Defines the password encoder bean.
   *
   * @return The PasswordEncoder instance.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return hashingService;
  }

  /**
   * Configures the security filter chain.
   *
   * @param http The HttpSecurity object to configure.
   * @return The configured SecurityFilterChain.
   * @throws Exception if configuration fails.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
      var cors = new CorsConfiguration();
      cors.setAllowedOrigins(List.of("*"));
      cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
      cors.setAllowedHeaders(List.of("*"));
      return cors;
    }))
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(unauthorizedRequestHandler))
        .sessionManagement(customizer -> customizer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(
                "/api/v1/authentication/**",   // autenticación pública
                "/api/v1/profiles/**",         // 👈 perfiles públicos
                "/api/v1/requests/**",         // 👈 requests públicos
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/actuator/**"
            ).permitAll()
            .anyRequest().authenticated());

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}