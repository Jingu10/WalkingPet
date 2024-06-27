package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCharacterStatResponse {
    private int health;
    private int power;
    private int defense;
    private int addHealth;
    private int addPower;
    private int addDefense;
    private int statPoint;

    public static UserCharacterStatResponse from(UserCharacter userCharacter, int upgradeHealth, int upgradeAttack, int upgradeDefense){
        return UserCharacterStatResponse.builder()
                .health(userCharacter.getHealth())
                .power(userCharacter.getPower())
                .defense(userCharacter.getDefense())
                .addHealth(userCharacter.getHealth() - userCharacter.getCharacter().getFixHealth() - upgradeHealth)
                .addPower(userCharacter.getPower() - userCharacter.getCharacter().getFixPower() - upgradeAttack)
                .addDefense(userCharacter.getDefense() - userCharacter.getCharacter().getFixDefense() - upgradeDefense)
                .statPoint(userCharacter.getStatPoint())
                .build();
    }
}
