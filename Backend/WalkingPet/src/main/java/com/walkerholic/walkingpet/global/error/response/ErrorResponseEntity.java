package com.walkerholic.walkingpet.global.error.response;

import com.walkerholic.walkingpet.global.auth.error.TokenErrorCode;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(GlobalErrorCode ec) {
        return ResponseEntity
                .status(ec.getStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ec.getStatus())
                        .code(ec.getCode())
                        .message(ec.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseEntity> toTokenEntity(TokenErrorCode ec) {
        return ResponseEntity
                .status(ec.getStatus())
                .body(ErrorResponseEntity.builder()
                        .status(ec.getStatus())
                        .code(ec.getCode())
                        .message(ec.getMessage())
                        .build());
    }
}
