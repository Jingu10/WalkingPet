package com.walkerholic.walkingpet.global.auth.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SocialLoginDTO {
    private String socialEmail;
    private String nickname;

    public SocialLoginDTO(String socialEmail, String nickname){
        this.socialEmail= socialEmail;
        this.nickname = nickname;
    }

//    private String socialProvider;
}
