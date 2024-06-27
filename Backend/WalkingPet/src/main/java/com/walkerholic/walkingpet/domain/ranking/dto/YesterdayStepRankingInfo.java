package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.*;

import java.io.Serializable;

// Redis에서 사용
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YesterdayStepRankingInfo implements Serializable {
    private int userId;
    private int yesterdayStep;

    @Builder
    public YesterdayStepRankingInfo(int userId, int yesterdayStep) {
        this.userId = userId;
        this.yesterdayStep = yesterdayStep;
    }

    public static YesterdayStepRankingInfo entityFrom(UserStep userStep) {
        return YesterdayStepRankingInfo.builder()
                .userId(userStep.getUser().getUserId())
                .yesterdayStep(userStep.getYesterdayStep())
                .build();
    }

    public static YesterdayStepRankingInfo redisFrom(YesterdayStepRankingInfo yesterdayStepRankingInfo) {
        return YesterdayStepRankingInfo.builder()
                .userId(yesterdayStepRankingInfo.getUserId())
                .yesterdayStep(yesterdayStepRankingInfo.getYesterdayStep())
                .build();
    }
}


