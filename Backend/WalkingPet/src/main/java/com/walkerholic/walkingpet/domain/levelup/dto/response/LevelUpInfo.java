package com.walkerholic.walkingpet.domain.levelup.dto.response;

import com.walkerholic.walkingpet.domain.levelup.dto.LevelUpReward;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LevelUpInfo {
    private int nowLevel;
    private int nextLevel;
    private LevelUpReward levelUpReward;
}
