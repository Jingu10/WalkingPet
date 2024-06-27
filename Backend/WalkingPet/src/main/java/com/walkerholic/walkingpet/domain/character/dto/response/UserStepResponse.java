package com.walkerholic.walkingpet.domain.character.dto.response;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserStepResponse {
    int step;
    boolean isReboot; //true: 재부팅함(db값), false: 재부팅안함(플러터값)

    public static UserStepResponse from(int step, boolean isReboot) {
        return UserStepResponse.builder()
                .step(step)
                .isReboot(isReboot)
                .build();
    }
}
