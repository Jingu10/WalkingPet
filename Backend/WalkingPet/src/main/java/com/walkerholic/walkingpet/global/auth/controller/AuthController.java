package com.walkerholic.walkingpet.global.auth.controller;

import com.walkerholic.walkingpet.domain.character.dto.response.UserStepResponse;
import com.walkerholic.walkingpet.domain.character.service.UserCharacterService;
import com.walkerholic.walkingpet.domain.users.dto.UsersDto;
import com.walkerholic.walkingpet.domain.users.service.LoginService;
import com.walkerholic.walkingpet.global.auth.dto.request.SocialLoginDTO;
import com.walkerholic.walkingpet.global.auth.dto.response.AuthResponse;
import com.walkerholic.walkingpet.global.auth.util.AuthService;
import com.walkerholic.walkingpet.global.auth.util.JwtUtil;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final LoginService loginService;
//    @Autowired()
    private final AuthService authService;
//    @Autowired()
    private final JwtUtil jwtUtil;
    private final UserCharacterService userCharacterService;

    @PostMapping("/social-login")
    public ResponseEntity<CommonResponseEntity> socialLogin(@RequestBody SocialLoginDTO socialLoginDTO) //TODO: @Valid 추가
    {
        log.info("로그인 api socialLogin - SocialLoginDTO: {}", socialLoginDTO);
        UsersDto savedOrFindUser = loginService.socialLogin(socialLoginDTO);
//        securityService.saveUserInSecurityContext(socialLoginDTO);
        authService.saveUserInSecurityContext(savedOrFindUser);
        Map<String, String> tokenMap = jwtUtil.initToken(savedOrFindUser);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,
                AuthResponse.builder()
                .accessToken(tokenMap.get("accessToken"))
                .refreshToken((tokenMap.get("refreshToken")))
                .userId(savedOrFindUser.getUserId())
                .build());
    }

    @GetMapping("/generate/user")
    public void generateUsers()
    {
        String[] gameNicknames = {
                "하늘", "바다", "나무", "바람", "구름", "태양", "이슬", "노을", "꽃잎", "새싹",
                "나비", "벌꿀", "강아지", "고양이", "토끼", "곰돌이", "병아리", "다람쥐", "기린", "호랑이",
                "사자", "코끼리", "원숭이", "펭귄", "물개", "고래", "거북이", "개구리", "뱀", "악어",
                "돌고래", "가재", "새우", "소라", "조개", "불가사리", "참치", "연어", "고등어", "갈치",
                "멸치", "오징어", "가오리", "해마", "해파리", "미역", "김", "다시마", "사과", "배",
                "바나나", "오렌지", "망고", "수박", "참외", "메론", "감자", "옥수수", "당근", "양파",
                "배추", "상추", "깻잎", "고추", "마늘", "펭귄", "고래", "가오리", "해마", "배",
                "포도", "바나나", "망고", "수박", "참외", "감자", "고구마", "옥수수", "당근",
                "양파", "배추", "상추", "깻잎", "고추", "마늘", "펭귄", "고래", "가오리", "해마",
                "딸기", "바나나", "망고", "수박", "참외", "감자", "옥수수", "당근",
                "양파", "배추", "상추", "깻잎", "고추", "마늘"
        };

        for(int i = 0; i < gameNicknames.length ; i++){
            String testEmail = "dummy test2" + i + "@gmail.com";
            String testNickname = gameNicknames[i] + "중독";
            SocialLoginDTO socialLoginDTO = new SocialLoginDTO(testEmail,testNickname);
            UsersDto savedOrFindUser = loginService.socialLogin(socialLoginDTO);
            authService.saveUserInSecurityContext(savedOrFindUser);
        }
    }

    @GetMapping("/checkstep")
    @Operation(summary = "유저의 걸음수 측정", description = "앱 시작시 걸음수 측정")
    @ApiResponse(responseCode = "200", description = "S200 - 걸음수 측정 성공")
    public ResponseEntity<CommonResponseEntity> getUserStep(@RequestHeader("step") int frontStep, @RequestParam("userId") int userId) {
//        Integer userId = userDetail.getUsers().getUserId();

        log.info("AuthController getUserStep - userId: {}, step: {}", userId, frontStep);

        UserStepResponse userStepResponse = userCharacterService.checkUserStep(userId, frontStep);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userStepResponse);
    }

    @GetMapping("/info")
    public ResponseEntity<CommonResponseEntity> getMainInfo(@RequestParam("userId") int userId) {
        log.info("메인 페이지용 캐릭터 정보 받기 AuthController getUserStep - userId: {}", userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterService.getUserCharacterId(userId));
    }
}
