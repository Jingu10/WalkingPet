package com.walkerholic.walkingpet.domain.battle.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.*;

@Builder
@ToString
@Getter
public class UserBattleInfoDTO {
    private String nickname;
    private int level;
    private int characterGrade;
    private int characterId;
    private int health;
    private int power;
    private int defense;
    private int rating;
    private int battleCount;
    private int upgrade;

    public static UserBattleInfoDTO from(UserDetail userDetail){
        return UserBattleInfoDTO.builder()
                .nickname(userDetail.getUser().getNickname())
                .rating(userDetail.getBattleRating())
                .level(userDetail.getSelectUserCharacter().getLevel())
                .characterGrade(userDetail.getSelectUserCharacter().getCharacter().getGrade())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .health(userDetail.getSelectUserCharacter().getHealth())
                .power(userDetail.getSelectUserCharacter().getPower())
                .defense(userDetail.getSelectUserCharacter().getDefense())
                .battleCount(userDetail.getBattleCount())
                .upgrade(userDetail.getSelectUserCharacter().getUpgrade())
                .build();
    }
}
