package com.walkerholic.walkingpet.global.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MainResponse {
    int characterId;
    String nickname;

    @Builder
    public MainResponse(int characterId, String nickname) {
        this.characterId = characterId;
        this.nickname = nickname;
    }

    public static MainResponse from(int characterId, String nickname) {
        return MainResponse.builder()
                .characterId(characterId)
                .nickname(nickname)
                .build();
    }
}
