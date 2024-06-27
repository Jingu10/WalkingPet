package com.walkerholic.walkingpet.domain.users.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChangeNicknameResponse {
    private String nickname;
    private boolean status;

    @Builder
    public ChangeNicknameResponse(String nickname, boolean status) {
        this.nickname = nickname;
        this.status = status;
    }
}
