package com.walkerholic.walkingpet.domain.users.controller;

import com.walkerholic.walkingpet.domain.users.service.SignInService;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/init")
public class InItTableTestController {

    private final SignInService signInService;
    @GetMapping
    public ResponseEntity<CommonResponseEntity> getPersonalRanking(@RequestParam("email") String email) {

        signInService.signInInItTable(email);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, null);
    }
}
