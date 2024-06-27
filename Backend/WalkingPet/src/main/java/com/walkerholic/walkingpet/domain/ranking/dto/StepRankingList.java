package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class StepRankingList {
    private int userId;
    private String nickname;
    private int step;
    private int characterId;
    private int ranking;

    public static StepRankingList fromAccStepInfo(AccStepRankingAndUserInfo accStepRankingInfo, int ranking) {
        return StepRankingList.builder()
                .userId(accStepRankingInfo.getUserId())
                .nickname(accStepRankingInfo.getNickname())
                .characterId(accStepRankingInfo.getCharacterId())
                .step(accStepRankingInfo.getStep())
                .ranking(ranking)
                .build();
    }

    public static StepRankingList from(UserRedisDto user, int step, int ranking) {
        return StepRankingList.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .step(step)
                .characterId(user.getCharacterId())
                .ranking(ranking)
                .build();
    }

}
