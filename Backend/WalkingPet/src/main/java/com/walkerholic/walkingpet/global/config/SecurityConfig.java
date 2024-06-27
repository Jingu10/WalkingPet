package com.walkerholic.walkingpet.global.config;

import com.walkerholic.walkingpet.global.auth.filter.ExceptionHandlerFilter;
import com.walkerholic.walkingpet.global.auth.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    JwtAuthorizationFilter jwtAuthorizationFilter;
    ExceptionHandlerFilter exceptionHandlerFilter;

    @Autowired
    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter, ExceptionHandlerFilter exceptionHandlerFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.exceptionHandlerFilter = exceptionHandlerFilter;
    }

    // HTTP 요청에 대한 보안 필터 체인을 정의, 특정 URL 패턴 또는 요청에 대한 보안 필터의 집합을 정의
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
//                .authorizeRequests(authorizeRequests ->
//                        authorizeRequests
//                                .anyRequest().authenticated()
//
//                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthorizationFilter.class);
//                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());


        return http.build();
    }


    // Spring Security의 구성을 조정하고 사용자 지정
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(
//                "/swagger-ui/**",
//                "/swagger-config",
//                "/error"
//        );
//    }
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.authorizeRequests()
//                .antMatchers("/**").authenticated();
//    }
}
