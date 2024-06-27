package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleRankingList {
    private int userId;
    private String nickname;
    private int characterId;
    private int ranking;
    private int battleRating;

    @Builder
    public BattleRankingList(int userId, String nickname, int characterId, int ranking, int battleRating) {
        this.userId = userId;
        this.nickname = nickname;
        this.characterId = characterId;
        this.ranking = ranking;
        this.battleRating = battleRating;
    }

    public static BattleRankingList mysqlFrom(UserDetail userDetail, int ranking) {
        return BattleRankingList.builder()
                .userId(userDetail.getUser().getUserId())
                .nickname(userDetail.getUser().getNickname())
                .battleRating(userDetail.getBattleRating())
                .characterId(userDetail.getSelectUserCharacter().getCharacter().getCharacterId())
                .ranking(ranking)
                .build();
    }

    public static BattleRankingList redisFrom(UserRedisDto userRedisDto,int battleRating ,int ranking) {
        return BattleRankingList.builder()
                .userId(userRedisDto.getUserId())
                .nickname(userRedisDto.getNickname())
                .battleRating(battleRating)
                .characterId(userRedisDto.getCharacterId())
                .ranking(ranking)
                .build();
    }
}
