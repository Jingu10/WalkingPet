package com.walkerholic.walkingpet.domain.levelup.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LevelUpResponse {
    private Boolean isLevelUp;
    private LevelUpInfo levelUpInfo;
}
