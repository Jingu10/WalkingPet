package com.walkerholic.walkingpet.global.auth.error;

import lombok.Getter;

@Getter
public enum TokenErrorCode {
    ACCESS_DENIED(401, "U400", "허용되지 않은 사용자입니다"),
    TOKEN_EXPIRED(401, "U401", "토큰이 만료되었습니다."),
    ACCESS_TOKEN_NOT_FOUND(401, "U400", "액세스 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "U400", "리프레시 토큰이 존재하지 않습니다."),
    INVALID_TOKEN(401, "U400", "토큰 만료 및 토큰 없음 외 유효하지 않은 토큰");
    
    private final int status;
    private final String code;
    private final String message;

    TokenErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
