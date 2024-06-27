package com.walkerholic.walkingpet.domain.team.dto.response;

import com.walkerholic.walkingpet.domain.team.entity.Team;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class TeamResponse {

    private Integer teamId;
    private String teamName;
    private String teamExplain;
    private Integer teamPoint;
    private Integer userCount;
    private boolean hasPassword;

    public static TeamResponse from(Team team, Integer userCount){
        return TeamResponse.builder()
                .teamId(team.getTeamId())
                .teamName(team.getName())
                .teamExplain(team.getExplanation())
                .teamPoint(team.getPoint())
                .userCount(userCount)
                .hasPassword(!team.getPassword().isEmpty())
                .build();
    }
}
