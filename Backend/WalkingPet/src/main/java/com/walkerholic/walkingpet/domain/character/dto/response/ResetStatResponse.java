package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResetStatResponse {
    private int health;
    private int power;
    private int defense;
    private int addHealth;
    private int addPower;
    private int addDefense;
    private int statPoint;
    public static ResetStatResponse from(UserCharacter userCharacter){
        return ResetStatResponse.builder()
                .health(userCharacter.getHealth())
                .power(userCharacter.getPower())
                .defense(userCharacter.getDefense())
                .addHealth(userCharacter.getCharacter().getFixHealth())
                .addPower(userCharacter.getCharacter().getFixPower())
                .addDefense(userCharacter.getCharacter().getFixDefense())
                .statPoint(userCharacter.getStatPoint())
                .build();
    }
}
