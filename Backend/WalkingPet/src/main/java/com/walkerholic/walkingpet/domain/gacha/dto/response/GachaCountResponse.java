package com.walkerholic.walkingpet.domain.gacha.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class GachaCountResponse {
    private Integer normalBoxCount;
    private Integer luxuryBoxCount;

    public static GachaCountResponse from(Integer normalBoxCount, Integer luxuryBoxCount){
        return GachaCountResponse.builder()
                .normalBoxCount(normalBoxCount)
                .luxuryBoxCount(luxuryBoxCount)
                .build();
    }
}

