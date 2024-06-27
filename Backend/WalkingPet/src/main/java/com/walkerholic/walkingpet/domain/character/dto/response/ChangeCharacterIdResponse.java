package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChangeCharacterIdResponse {
    private int changeCharacterId;
    private int characterLevel;
    private int characterGrade;

    public static ChangeCharacterIdResponse from(UserCharacter userCharacter){
        return ChangeCharacterIdResponse.builder()
                .changeCharacterId(userCharacter.getCharacter().getCharacterId())
                .characterLevel(userCharacter.getLevel())
                .characterGrade(userCharacter.getCharacter().getGrade())
                .build();
    }
}
