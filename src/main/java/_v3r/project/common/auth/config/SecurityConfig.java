package _v3r.project.common.auth.config;

import _v3r.project.common.auth.exception.CustomAccessDeniedHandler;
import _v3r.project.common.auth.exception.CustomAuthenticationEntryPoint;
import _v3r.project.common.auth.exception.CustomAuthenticationFailureHandler;
import _v3r.project.common.auth.filter.CustomLoginFilter;
import _v3r.project.common.auth.filter.JwtAuthenticationFilter;
import _v3r.project.common.util.JwtUtil;
import _v3r.project.common.util.RedisUtil;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationSec;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationSec;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager =
                authenticationManager(authenticationConfiguration);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/user/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAt(
                        customLoginFilter(authenticationManager),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private CustomLoginFilter customLoginFilter(
            AuthenticationManager authenticationManager
    ) {
        CustomLoginFilter filter =
                new CustomLoginFilter(authenticationManager, jwtUtil, redisUtil,accessTokenExpirationSec, refreshTokenExpirationSec);

        filter.setFilterProcessesUrl("/api/v1/auth/login");
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return filter;
    }
}
