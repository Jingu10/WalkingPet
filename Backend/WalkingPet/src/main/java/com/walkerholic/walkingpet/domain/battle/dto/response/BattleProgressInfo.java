package com.walkerholic.walkingpet.domain.battle.dto.response;

import lombok.*;

import java.util.List;

@Builder
@ToString
@Getter
public class BattleProgressInfo {
    private List<Integer> userAttackDamage;
    private List<Integer> enemyAttackDamage;
    private List<Integer> userHealth;
    private List<Integer> enemyHealth;
    private List<Double> userLoseDamage;
    private List<Double> enemyLoseDamage;
}
