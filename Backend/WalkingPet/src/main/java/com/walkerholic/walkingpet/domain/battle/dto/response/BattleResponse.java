package com.walkerholic.walkingpet.domain.battle.dto.response;

import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import lombok.*;

@Builder
@ToString
@Getter
public class BattleResponse {
    private EnemyInfo enemyInfo;
    private BattleProgressInfo battleProgressInfo;
    private BattleResultInfo battleResultInfo;
    private LevelUpResponse levelUpResponse;
}
