package com.walkerholic.walkingpet.global.auth.error;

import lombok.Getter;

@Getter
public class TokenBaseException extends RuntimeException{
    TokenErrorCode tokenErrorCode;

    public TokenBaseException(TokenErrorCode tokenErrorCode) {
        this.tokenErrorCode = tokenErrorCode;
    }
}