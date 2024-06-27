package com.walkerholic.walkingpet.domain.ranking.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
public class RealtimeStepRequest {
    private int userId;
    private int realtimeStep;
}
