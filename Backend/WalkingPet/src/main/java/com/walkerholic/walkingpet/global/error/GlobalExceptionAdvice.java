package com.walkerholic.walkingpet.global.error;

import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import com.walkerholic.walkingpet.global.error.response.ErrorResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * RestControllerAdvice = ControllerAdvice + ResponseBody
 * 여러 컨트롤러에 대해 전역적으로 ExceptionHandler를 적용하여 json 형태로 반환
 * 하나의 클래스로 모든 컨트롤러에 대해 전역적으로 예외 처리가 가능함
 * 직접 정의한 에러 응답을 일관성있게 클라이언트에게 내려줄 수 있음
 * 별도의 try-catch문이 없어 코드의 가독성이 높아짐
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    /**
     * Valid 검증 실패시 오류 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    //TODO @Valid 예외 처리 코드 추가


    /**
     * 우리가 만든 커스텀 예외 처리
     * @param e
     * @return
     */
    @ExceptionHandler(GlobalBaseException.class)
    public ResponseEntity<ErrorResponseEntity> ExceptionHandler(GlobalBaseException e) {
        return ErrorResponseEntity.toResponseEntity(e.getGlobalErrorCode());
    }
}
