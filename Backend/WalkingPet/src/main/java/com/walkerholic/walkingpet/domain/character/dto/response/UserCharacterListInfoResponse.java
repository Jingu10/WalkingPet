package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.character.dto.UserCharacterListInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserCharacterListInfoResponse {
    private Integer selectCharacterId;
    private List<UserCharacterListInfo> characters;

    public static UserCharacterListInfoResponse from(List<UserCharacterListInfo> userCharacterListInfos, int userCharacterId) {
        return UserCharacterListInfoResponse.builder()
                .selectCharacterId(userCharacterId)
                .characters(userCharacterListInfos)
                .build();
    }
}
