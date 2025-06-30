//package com.hotel.reservation.auth.config;
//
//
//import com.hotel.reservation.auth.JwtAuthenticationEntryPoint;
//import com.hotel.reservation.auth.service.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
//
//    private final AuthenticationProvider authenticationProvider;
//
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider, JwtAuthenticationEntryPoint authenticationEntryPoint) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.authenticationProvider = authenticationProvider;
//        this.authenticationEntryPoint = authenticationEntryPoint;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
//        security.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        security.csrf(CsrfConfigurer::disable)
//                .authorizeHttpRequests(
//                        request -> request
//                                .requestMatchers(
//                                        antMatcher(HttpMethod.POST, "/api/v1/auth/**"),
//                                        antMatcher(HttpMethod.GET, "/api/v1/**"),
//                                        antMatcher(HttpMethod.PATCH, "/api/v1/**"),
//                                        antMatcher("/api/v2/admin/register"),
//                                        antMatcher("/api/v2/admin/verify"),
//                                        antMatcher("/api/v2/admin/login")
//                                )
//                                .permitAll()
//
//                                .requestMatchers(antMatcher("/api/v2/admin/**"))
//                                .hasAuthority("ADMIN")
//
//                                .anyRequest()
//                                .permitAll()
//                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(authenticationEntryPoint))
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .httpBasic(Customizer.withDefaults());
//
//        security.authenticationProvider(authenticationProvider);
//
//        return security.build();
//    }
//}



package com.hotel.reservation.auth.config;

import com.hotel.reservation.auth.JwtAuthenticationEntryPoint;
import com.hotel.reservation.auth.service.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          AuthenticationProvider authenticationProvider,
                          JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/**").permitAll()
                        .requestMatchers("/api/v2/admin/register", "/api/v2/admin/verify", "/api/v2/admin/login").permitAll()

                        .requestMatchers("/api/v2/admin/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
