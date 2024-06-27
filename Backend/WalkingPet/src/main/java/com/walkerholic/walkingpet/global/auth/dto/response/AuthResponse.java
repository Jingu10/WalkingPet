package com.walkerholic.walkingpet.global.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthResponse {
    String accessToken;
    String refreshToken;
    Integer userId;

    @Builder
    public AuthResponse(String accessToken, String refreshToken, Integer userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
