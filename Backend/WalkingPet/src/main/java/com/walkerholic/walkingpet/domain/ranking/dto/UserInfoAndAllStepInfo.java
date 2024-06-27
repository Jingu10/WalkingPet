package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoAndAllStepInfo {
    private int userId;
    private String nickname;
    private int characterId;
    private int accStep;
    private int dailyStep;
    private int yesterdayStep;
    private int battleRating;

    public static UserInfoAndAllStepInfo from(UserDetail userDetail, UserStep userStep) {
        return UserInfoAndAllStepInfo.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .accStep(userStep.getAccumulationStep())
                .dailyStep(userStep.getDailyStep())
                .yesterdayStep(userStep.getYesterdayStep())
                .battleRating(userDetail.getBattleRating())
                .build();
    }
}
