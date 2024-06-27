package com.walkerholic.walkingpet.domain.gacha.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.Character;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class GachaResultResponse {
    private Integer characterId;
    private String characterName;
    private Integer characterGrade;

    private boolean duplication;

    public static GachaResultResponse from(Character character, boolean duplication){
        return GachaResultResponse.builder()
                .characterId(character.getCharacterId())
                .characterName(character.getName())
                .characterGrade(character.getGrade())
                .duplication(duplication)
                .build();
    }
}
