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
    public static ResetStatResponse from(UserCharacter userCharacter, int upgradeHealth, int upgradePower, int upgradeDefense){
        return ResetStatResponse.builder()
                .health(userCharacter.getHealth())
                .power(userCharacter.getPower())
                .defense(userCharacter.getDefense())
                .addHealth(userCharacter.getHealth() - userCharacter.getCharacter().getFixHealth() - upgradeHealth)
                .addPower(userCharacter.getPower() - userCharacter.getCharacter().getFixPower() - upgradePower)
                .addDefense(userCharacter.getDefense() - userCharacter.getCharacter().getFixDefense() - upgradeDefense)
                .statPoint(userCharacter.getStatPoint())
                .build();
    }
}
