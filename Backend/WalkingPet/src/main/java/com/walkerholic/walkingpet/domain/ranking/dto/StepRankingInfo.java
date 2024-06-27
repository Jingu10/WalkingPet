package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StepRankingInfo {
    private int userId;
    private String nickname;
    private int step;
    private int characterId;
    private int ranking;

    @Builder
    public StepRankingInfo(int userId, String nickname, int step, int characterId, int ranking) {
        this.userId = userId;
        this.nickname = nickname;
        this.step = step;
        this.characterId = characterId;
        this.ranking = ranking;
    }

    public static StepRankingInfo entityFrom(UserDetail userDetail, UserStep userStep, int ranking) {
        return StepRankingInfo.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .step(userStep.getAccumulationStep())
                .ranking(ranking)
                .build();
    }
}
