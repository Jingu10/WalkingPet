package com.walkerholic.walkingpet.global.error;

import lombok.Getter;

@Getter
public class GlobalBaseException extends RuntimeException{
    GlobalErrorCode globalErrorCode;

    public GlobalBaseException(GlobalErrorCode globalErrorCode) {
        this.globalErrorCode = globalErrorCode;
    }
}