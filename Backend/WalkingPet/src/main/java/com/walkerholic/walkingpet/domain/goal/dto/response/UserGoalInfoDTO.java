package com.walkerholic.walkingpet.domain.goal.dto.response;

import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class UserGoalInfoDTO {
    public static final int DAILY_GOAL_COUNT = 5;
    public static final int WEEKLY_GOAL_COUNT = 7;

    private int step;

    @Builder.Default
    private boolean[] dailyGoal = new boolean[DAILY_GOAL_COUNT];
    @Builder.Default
    private boolean[] weeklyGoal = new boolean[WEEKLY_GOAL_COUNT];
}
