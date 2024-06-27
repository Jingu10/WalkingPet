package com.walkerholic.walkingpet.domain.levelup.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;

@Builder
@Getter
public class LevelUpReward {
    int statPoint;
    @Builder.Default
    HashMap<String,Integer> itemReward = new HashMap<>();
}
