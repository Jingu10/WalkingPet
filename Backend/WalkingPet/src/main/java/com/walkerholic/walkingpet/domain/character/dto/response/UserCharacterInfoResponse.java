package com.walkerholic.walkingpet.domain.character.dto.response;


import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Getter
public class UserCharacterInfoResponse {
    private String nickname;
    private int characterId;
    private int characterLevel;
    private int characterGrade;
    private int experience;
    private int maxExperience;
    private int health;
    private int power;
    private int defense;
    private int addHealth;
    private int addPower;
    private int addDefense;
    private int statPoint;
    private int upgrade;
    private int initStatus;

    public static UserCharacterInfoResponse from(UserDetail userDetail, int upgradeHealth, int upgradePower, int upgradeDefense){
        UserCharacter userCharacter = userDetail.getSelectUserCharacter();

        return UserCharacterInfoResponse.builder()
                .nickname(userDetail.getUser().getNickname())
                .characterId(userCharacter.getCharacter().getCharacterId())
                .characterLevel(userCharacter.getLevel())
                .characterGrade(userCharacter.getCharacter().getGrade())
                .experience(userCharacter.getExperience())
                .maxExperience(UserCharacterFunction.getMaxExperience(userCharacter.getLevel()))
                .health(userCharacter.getHealth())
                .power(userCharacter.getPower())
                .defense(userCharacter.getDefense())
                .statPoint(userCharacter.getStatPoint())
                .addHealth(userCharacter.getHealth() - userCharacter.getCharacter().getFixHealth() - upgradeHealth)
                .addPower(userCharacter.getPower() - userCharacter.getCharacter().getFixPower() - upgradePower)
                .addDefense(userCharacter.getDefense() - userCharacter.getCharacter().getFixDefense() - upgradeDefense)
                .upgrade(userCharacter.getUpgrade())
                .initStatus(userDetail.getInitStatus())
                .build();
    }
}
