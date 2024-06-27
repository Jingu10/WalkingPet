package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalStepRankingInfo {
    private int userId;
    private String nickname;
    private int step;
    private int characterId;
    private int ranking;

    @Builder
    public PersonalStepRankingInfo(int userId, String nickname, int step, int characterId, int ranking) {
        this.userId = userId;
        this.nickname = nickname;
        this.step = step;
        this.characterId = characterId;
        this.ranking = ranking;
    }

    public static PersonalStepRankingInfo entityFrom(UserDetail userDetail, UserStep userStep, int step, int ranking) {
        return PersonalStepRankingInfo.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .step(step)
                .ranking(ranking)
                .build();
    }
}
