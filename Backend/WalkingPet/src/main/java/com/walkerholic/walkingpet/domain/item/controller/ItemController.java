package com.walkerholic.walkingpet.domain.item.controller;

import com.walkerholic.walkingpet.domain.item.service.ItemService;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {

}
