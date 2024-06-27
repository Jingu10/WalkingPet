package com.walkerholic.walkingpet.domain.ranking.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GroupCountResponse {
    int groupCount;
    public static GroupCountResponse from(int groupCount){
        return GroupCountResponse.builder()
                .groupCount(groupCount)
                .build();
    }
}
