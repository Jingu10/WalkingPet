package com.walkerholic.walkingpet.domain.battle.dto.response;

import lombok.*;

import java.util.HashMap;

@Builder
@ToString
@Getter
public class RewardItem {
    private HashMap<String, Integer> reward;
}
