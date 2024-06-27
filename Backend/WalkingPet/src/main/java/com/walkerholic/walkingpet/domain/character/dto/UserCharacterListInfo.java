package com.walkerholic.walkingpet.domain.character.dto;

import com.walkerholic.walkingpet.domain.character.entity.Character;
import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCharacterListInfo {
    private int characterId;
    private String characterName;
    private int characterGrade;
//    private boolean userCharacterId;
    private int userCharacterLevel;
    private int userCharacterUpgrade;
    private boolean userCharacterStatus;

    // 해당 캐릭터를 사용자가 가지고 있지 않은 경우
    public static UserCharacterListInfo characterFrom(Character character){
        return UserCharacterListInfo.builder()
                .characterId(character.getCharacterId())
                .characterName(character.getName())
                .characterGrade(character.getGrade())
                .userCharacterStatus(false)
                .build();
    }

    // 해당 캐릭터를 사용자가 가지고 있는 경우
    public static UserCharacterListInfo userCharacterFrom(UserCharacter userCharacter){
        return UserCharacterListInfo.builder()
                .characterId(userCharacter.getCharacter().getCharacterId())
                .characterName(userCharacter.getCharacter().getName())
                .characterGrade(userCharacter.getCharacter().getGrade())
                .userCharacterLevel(userCharacter.getLevel())
                .userCharacterUpgrade(userCharacter.getUpgrade())
                .userCharacterStatus(true)
                .build();
    }
}
