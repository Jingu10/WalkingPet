package com.walkerholic.walkingpet.global.auth.service;

import com.walkerholic.walkingpet.domain.users.dto.UsersDto;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.auth.error.TokenBaseException;
import com.walkerholic.walkingpet.global.auth.error.TokenErrorCode;
import com.walkerholic.walkingpet.global.auth.util.JwtUtil;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

/*
    1.AuthenticationProvider에서 UserDetailsService 인터페이스를 통해 loadUserByUsername을 호출
    2. loadUserByUsername은 유저 정보를 db에서 불러온 후 해당 유저 정보를 검증
    3. 인증이 성공할 경우 해당 유저정보로 authentication객체를 생성한 후 securityContext에 넣어줌. 그 후 AuthenticationSuccessHandle을 실행
    4. 실패할 경우 AuthenticationFailureHandler을 실행
    5. 그 뒤에는 토큰 검증 등의 작업
 */
@Service("mySecurityLoginService")
@RequiredArgsConstructor
public class SecurityLoginService {
    private final JwtUtil jwtUtil;

    // 회원가입 및 로그인 시 사용 controller에서
//    public void saveUserInSecurityContext(SocialLoginDTO socialLoginDTO) {
    public void saveUserInSecurityContext(UsersDto usersDto) {
        Integer socialId = usersDto.getUserId();
//        String socialProvider = socialLoginDTO.getSocialProvider();
        saveUserInSecurityContext(String.valueOf(socialId), "test");
    }

    // jwtAuthorizationFilter에서 사용 -> userId로 탐색
    public void saveUserInSecurityContext(String accessToken) {
        if(accessToken == null) {
            System.out.println("SecurityService saveUserInSecurityContext 토큰 없음");
            throw new TokenBaseException(TokenErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        String socialId = jwtUtil.extractClaim(accessToken,  Claims::getSubject);
        String socialProvider = jwtUtil.extractClaim(accessToken, Claims::getIssuer);

        saveUserInSecurityContext(socialId, socialProvider);
    }

    private void saveUserInSecurityContext(String socialId, String socialProvider) {
        UserDetails userDetails = loadUserBySocialIdAndSocialProvider(socialId, socialProvider);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        if(authentication != null) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
    }

    private UserDetails loadUserBySocialIdAndSocialProvider(String socialId, String socialProvidero) {

        // 회원의 user 데이터를 authentification에 모두 저장
//        Users user = usersRepository.findByUserId(Integer.valueOf(socialId));

        Users user = new Users(Integer.valueOf(socialId));

        if(user == null) {
            System.out.println("SecurityService loadUserBySocialIdAndSocialProvider 유저 못찾음 ");
            throw new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND);
        } else {
            CustomUserDetail userDetails = new CustomUserDetail();
            userDetails.setUser(user);
            return userDetails;
        }
    }
}
