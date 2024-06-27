package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.*;

import java.io.Serializable;

// Redis에서 사용
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccStepRankingAndUserInfo implements Serializable {
    private int userId;
    private String nickname; // user
    private int step; // user_step
    private int characterId; // user_detail에서 character

    @Builder
    public AccStepRankingAndUserInfo(int userId, String nickname, int step, int characterId) {
        this.userId = userId;
        this.nickname = nickname;
        this.step = step;
        this.characterId = characterId;
    }

    public static AccStepRankingAndUserInfo entityFrom(UserDetail userDetail, UserStep userStep) {
        return AccStepRankingAndUserInfo.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .step(userStep.getAccumulationStep())
                .build();
    }

    public static AccStepRankingAndUserInfo redisFrom(AccStepRankingAndUserInfo accStepRankingInfo) {
        return AccStepRankingAndUserInfo.builder()
                .userId(1)
                .nickname("test2")
                .characterId(1)
                .step(accStepRankingInfo.getStep())
                .build();
    }

    public AccStepRankingAndUserInfo changeStep(int step) {
        this.step = step;
        return this;
    }
}


