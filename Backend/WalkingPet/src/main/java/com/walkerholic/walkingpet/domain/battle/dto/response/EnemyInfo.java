package com.walkerholic.walkingpet.domain.battle.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.*;

@Builder
@Getter
@ToString
public class EnemyInfo {
    private String nickname;
    private int level;
    private int characterId;
    private int rating;
    private int health;
    private int power;
    private int defense;

    public static EnemyInfo from(UserDetail userDetail){
        return EnemyInfo.builder()
                .level(userDetail.getSelectUserCharacter().getLevel())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .rating(userDetail.getBattleRating())
                .health(userDetail.getSelectUserCharacter().getHealth())
                .power(userDetail.getSelectUserCharacter().getPower())
                .defense(userDetail.getSelectUserCharacter().getDefense())
                .build();
    }
}
