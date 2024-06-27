package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.*;

import java.io.Serializable;

// Redis에서 사용
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccStepRankingInfo implements Serializable {
    private int userId;
    private int accStep;

    @Builder
    public AccStepRankingInfo(int userId, int accStep) {
        this.userId = userId;
        this.accStep = accStep;
    }

    public static AccStepRankingInfo entityFrom(UserStep userStep) {
        return AccStepRankingInfo.builder()
                .userId(userStep.getUser().getUserId())
                .accStep(userStep.getAccumulationStep())
                .build();
    }

}


