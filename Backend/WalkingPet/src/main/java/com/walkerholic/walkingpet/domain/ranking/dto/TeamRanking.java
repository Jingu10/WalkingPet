package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.team.entity.Team;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRanking {
    private Integer ranking;
    private String teamName;
    private String explain;
    private Integer teamPoint;

    @Builder
    public TeamRanking(Integer ranking, String teamName, String explain, Integer teamPoint) {
        this.ranking = ranking;
        this.teamName = teamName;
        this.explain = explain;
        this.teamPoint = teamPoint;
    }

    public static TeamRanking from(Team team, Integer ranking){
        return TeamRanking.builder()
                .ranking(ranking)
                .teamName(team.getName())
                .explain(team.getExplanation())
                .teamPoint(team.getPoint())
                .build();
    }
}
