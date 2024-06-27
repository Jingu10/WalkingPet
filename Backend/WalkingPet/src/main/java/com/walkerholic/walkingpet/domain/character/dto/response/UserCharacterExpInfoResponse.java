package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCharacterExpInfoResponse {

    private String nickname;
    private int characterId;
    private int characterLevel;
    private int characterGrade;
    private int experience;
    private int maxExperience;
    private int quantity;

    public static UserCharacterExpInfoResponse from(UserDetail userDetail, UserItem userItem){

        UserCharacter userCharacter = userDetail.getSelectUserCharacter();

        return UserCharacterExpInfoResponse.builder()
                .nickname(userDetail.getUser().getNickname())
                .characterId(userCharacter.getCharacter().getCharacterId())
                .characterLevel(userCharacter.getLevel())
                .characterGrade(userCharacter.getCharacter().getGrade())
                .experience(userCharacter.getExperience())
                .maxExperience(UserCharacterFunction.getMaxExperience(userCharacter.getLevel()))
                .quantity(userItem.getQuantity())
                .build();
    }


}
