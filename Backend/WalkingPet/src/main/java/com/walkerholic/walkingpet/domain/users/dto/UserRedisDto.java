package com.walkerholic.walkingpet.domain.users.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserRedisDto implements Serializable {
    private int userId;
    private String nickname;
    private int characterId;

    @Builder
    public UserRedisDto(int userId, String nickname, int characterId) {
        this.userId = userId;
        this.nickname = nickname;
        this.characterId = characterId;
    }

    public static UserRedisDto from(UserDetail userDetail) {
        return UserRedisDto.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .build();
    }

    public UserRedisDto changeCharacterId(int characterId) {
        this.characterId = characterId;
        return this;
    }

    public UserRedisDto changeNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
