package com.walkerholic.walkingpet.global.error;

import lombok.Getter;

@Getter
public enum GlobalSuccessCode {
    //기본
//    OK(HttpStatus.OK, "OK"),
    SUCCESS(200, "S200", "요청에 성공하였습니다.");

    //기타

    private final int status;
    private final String code;
    private final String message;

    GlobalSuccessCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
