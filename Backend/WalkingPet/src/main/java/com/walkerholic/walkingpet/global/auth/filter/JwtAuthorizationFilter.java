package com.walkerholic.walkingpet.global.auth.filter;

import com.walkerholic.walkingpet.domain.ranking.dto.request.RealtimeStepRequest;
import com.walkerholic.walkingpet.global.auth.error.TokenBaseException;
import com.walkerholic.walkingpet.global.auth.error.TokenErrorCode;
import com.walkerholic.walkingpet.global.auth.util.AuthService;
import com.walkerholic.walkingpet.global.auth.util.JwtUtil;
import com.walkerholic.walkingpet.global.redis.service.RealtimeStepRankingRedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * 인증 정보 설정
 * : 요정 헤더에서 키가 'Authorization'인 필드의 값을 가져온 다음, 토큰의 접두사 'Bearer'을 제외한 값을 얻는다.
 * 만약 값이 null이거나 Bearer로 시작하지 않으면 null을 반환한다.
 * 이어서 가져온 토큰이 유효한지 확인하고 유효하다면 인증 정보를 관리하는 시큐리티 컨텍스트에 인증 정보를 설정한다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("토큰 인증 필터 구간");
        if(checkAccessTokenValid(request)) {
            filterChain.doFilter(request, response);
        } else {
            throw new RuntimeException("알 수 없는 오류가 발생했습니다.");
        }
    }

    private boolean checkAccessTokenValid(HttpServletRequest request) {
        String accessToken = jwtUtil.extractTokenFromHeader(request);

        // true: 토큰 만료x, false: 토큰 만료

        boolean result = false;
        try {
            result = jwtUtil.validateAccessToken(accessToken);

            // SecurityContext 에 Authentication 객체를 저장
            authService.saveUserInSecurityContext(accessToken);
        } catch (ExpiredJwtException e) {
            System.out.println("JwtAuthorizationFilter doFilterInternal 토큰 만료");
            throw new TokenBaseException(TokenErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            System.out.println("JwtAuthorizationFilter doFilterInternal 그 외 에러 ");
            throw new TokenBaseException(TokenErrorCode.INVALID_TOKEN);
        }


        String step = jwtUtil.extractStepFromHeader(request);
        if (step != null) {
            String socialId = jwtUtil.extractClaim(accessToken,  Claims::getSubject);
            saveRedisStep(socialId, step);
        } else {
            System.out.println("헤더에 step이 존재 하지 않음");
        }
        return true;
    }

    // 헤더에 있는 step의 값을 redis 에 저장
    private void saveRedisStep(String socialId, String step) {
        realtimeStepRankingRedisService.saveUserDailyStep(Integer.parseInt(socialId), Integer.parseInt(step));
    }

    /*
        SecurityConfig 파일에서 해당 api에 접근시 permitAll로 해두었으나 
        permitAll과 상관없이 jwtAuthorizationFilter 로 계속 들어가서 JwtAuthorizationFilter에서 다시 설정
        -> Spring Security에서는 보안 필터 체인을 통해 모든 요청에 대한 보안 검사를 수행하기 때문
        즉, excludedPath에 포함되어 있으면 true를 반환하고 해당 필터를 타지 않도록 설정
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
                String[] excludePath = {
                        "/auth",
                        "/auth/social-login",
                        "/auth/generate/user",
                        "/user/emailCheck",
                        "/user/nicknameCheck",
//                        "/user/checkstep",
                        "/redis",
                        "/init",
                        "/v3/api-docs",
                        "/v3/api-docs/swagger-config",
                        "/swagger-",
                        "/actuator/prometheus",
//                        "/character/checkstep" //걸음수 가져오는 코드 시 redis에 저장x위함
        };
//        String[] excludePath = {
////                "/character/test",
//                "/auth/social-login",
//                "/user",
//                "/swagger-ui/",
//                "/error"
//        };
//        String[] excludePath = {
//                "/api-docs/json",
//                "/api-docs",
//                "/auth/social-login",
////                "/v3/api-docs/swagger-config",
//                "/swagger-ui/",
//                "/swagger-config",
//                "/swagger.yaml",
//                "/requestBodies",
//                "/swagger-",
//                "/error"
//        };

//        "/ranking",
//                "/auth",
//                "/battle",
//                "/character",
//                "/gacha",
//                "/goal",
//                "/levelup",
//                "/team",
//
//                "/user",
//                "/redis",
//                "/v3/api-docs",
//                "/v3/api-docs/swagger-config",
//                "/swagger-",
//                "/actuator/prometheus"
        String path = request.getRequestURI();

//        System.out.println(path);
//        System.out.println(Arrays.toString(excludePath));
        boolean shouldNotFilter = Arrays.stream(excludePath).anyMatch(path::startsWith);
//        System.out.println(shouldNotFilter);

        return shouldNotFilter;
    }
}
