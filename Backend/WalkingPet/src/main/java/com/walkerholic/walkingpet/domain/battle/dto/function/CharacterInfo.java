package com.walkerholic.walkingpet.domain.battle.dto.function;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class CharacterInfo {
    private int health;
    private int power;
    private int defense;
}
