package com.walkerholic.walkingpet.domain.team.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class EnterGroupRequest {

    private Integer teamId;
    private String password;

}
