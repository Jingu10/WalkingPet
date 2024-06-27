package com.walkerholic.walkingpet.global.error.response;

import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class CommonResponseEntity {
    private int status;
    private String code;
    private String message;
    private Object data;

    public static ResponseEntity<CommonResponseEntity> toResponseEntity(GlobalSuccessCode sc, Object data) {
        return ResponseEntity
                .status(sc.getStatus())
                .body(CommonResponseEntity.builder()
                        .status(sc.getStatus())
                        .code(sc.getCode())
                        .message(sc.getMessage())
                        .data(data)
                        .build());
    }

    public static ResponseEntity<CommonResponseEntity> toResponseEntity(GlobalSuccessCode sc) {
        return ResponseEntity
                .status(sc.getStatus())
                .body(CommonResponseEntity.builder()
                        .status(sc.getStatus())
                        .code(sc.getCode())
                        .message(sc.getMessage())
                        .build());
    }
}
