package com.walkerholic.walkingpet.domain.data.Controller;

import com.walkerholic.walkingpet.domain.data.DataService;
import com.walkerholic.walkingpet.domain.item.service.ItemService;
import com.walkerholic.walkingpet.domain.users.dto.UsersDto;
import com.walkerholic.walkingpet.domain.users.service.LoginService;
import com.walkerholic.walkingpet.domain.users.service.UserService;
import com.walkerholic.walkingpet.global.auth.dto.request.SocialLoginDTO;
import com.walkerholic.walkingpet.global.auth.util.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/data")
public class DataController {
    private final AuthService authService;
    private final LoginService loginService;
    private final UserService userService;
    private final ItemService itemService;
    private final DataService dataService;

    @GetMapping("/set/battleRating")
    private void setBattleRating(){
        userService.setBattleRating();
    }

    @GetMapping("/generate/user")
    public void generateUsers()
    {
        String[] gameNicknames = {
                "하늘", "바다", "나무", "바람", "구름", "태양", "이슬", "노을", "꽃잎", "새싹",
                "나비", "벌꿀", "강아지", "고양이", "토끼", "곰돌이", "병아리", "다람쥐", "기린", "호랑이",
                "사자", "코끼리", "원숭이", "펭귄", "물개", "고래", "거북이", "개구리", "뱀", "악어"
        };

//        String[] nickname = { "하늘", "바다", "나무", "바람", "구름", "태양", "이슬", "노을", "꽃잎", "새싹",
//                "나비", "벌꿀", "강아지", "고양이", "토끼", "곰돌이", "병아리", "다람쥐", "기린", "호랑이",
//                "사자", "코끼리", "원숭이", "펭귄", "물개", "고래", "거북이", "개구리", "뱀", "악어", "돌고래", "가재", "새우", "소라", "조개", "불가사리", "참치", "연어", "고등어", "갈치",
//                "멸치", "오징어", "가오리", "해마", "해파리", "미역", "김", "다시마", "사과", "배",
//                "바나나", "오렌지", "망고", "수박", "참외", "메론", "감자", "옥수수", "당근", "양파",
//                "배추", "상추", "깻잎", "고추", "마늘", "펭귄", "고래", "가오리", "해마", "배",
//                "포도", "바나나", "망고", "수박", "참외", "감자", "고구마", "옥수수", "당근",
//                "양파", "배추", "상추", "깻잎", "고추", "마늘", "펭귄", "고래", "가오리", "해마",
//                "딸기", "바나나", "망고", "수박", "참외", "감자", "옥수수", "당근",
//                "양파", "배추", "상추", "깻잎", "고추", "마늘"};

        for(int i = 0; i < gameNicknames.length ; i++){
            String testEmail = "dummy test2" + i + "@gmail.com";
            String testNickname = gameNicknames[i] + "중독";
            SocialLoginDTO socialLoginDTO = new SocialLoginDTO(testEmail,testNickname);
            UsersDto savedOrFindUser = loginService.socialLogin(socialLoginDTO);
            authService.saveUserInSecurityContext(savedOrFindUser);
        }
    }

    @GetMapping("/set/usercharacter")
    public void setDummyCharacter(){
        dataService.setUserCharacter();
    }

    @GetMapping("/set/reset/battlecount")
    public void setBattleCount(){
        dataService.resetBattleCount();
    }
}
