package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.*;

import java.io.Serializable;

// Redis에서 사용
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReailtimeStepRankingInfo implements Serializable {
    private int userId;
    private int realtimeStep;

    @Builder
    public ReailtimeStepRankingInfo(int userId, int realtimeStep) {
        this.userId = userId;
        this.realtimeStep = realtimeStep;
    }

    public static ReailtimeStepRankingInfo entityFrom(UserStep userStep) {
        return ReailtimeStepRankingInfo.builder()
                .userId(userStep.getUser().getUserId())
                .realtimeStep(userStep.getDailyStep())
                .build();
    }

    public static ReailtimeStepRankingInfo redisFrom(ReailtimeStepRankingInfo yesterdayStepRankingInfo) {
        return ReailtimeStepRankingInfo.builder()
                .userId(yesterdayStepRankingInfo.getUserId())
                .realtimeStep(yesterdayStepRankingInfo.getRealtimeStep())
                .build();
    }
}


