package com.walkerholic.walkingpet.domain.goal.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;

@Builder
@Getter
@ToString
public class GoalRewardDTO {
    HashMap<String, Integer> goalReward;
}
