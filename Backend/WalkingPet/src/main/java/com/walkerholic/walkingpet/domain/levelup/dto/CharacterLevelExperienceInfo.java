package com.walkerholic.walkingpet.domain.levelup.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CharacterLevelExperienceInfo {
    private int nowLevel;
    private int nowExperience;
}
