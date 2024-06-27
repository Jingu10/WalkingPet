package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.TeamRanking;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRankingResponse {
    private List<TeamRanking> rankings;

    @Builder
    public TeamRankingResponse(List<TeamRanking> rankings) {
        this.rankings = rankings;
    }

    public static TeamRankingResponse from(List<TeamRanking> teamRankings) {
        return TeamRankingResponse.builder()
                .rankings(teamRankings)
                .build();
    }
}
