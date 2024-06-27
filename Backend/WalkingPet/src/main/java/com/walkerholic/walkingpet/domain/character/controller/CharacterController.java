package com.walkerholic.walkingpet.domain.character.controller;

import com.walkerholic.walkingpet.domain.character.dto.request.ChangeUserCharacterIdRequest;
import com.walkerholic.walkingpet.domain.character.dto.response.*;
import com.walkerholic.walkingpet.domain.character.service.UserCharacterService;
import com.walkerholic.walkingpet.domain.item.service.ItemService;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
public class CharacterController {

    private final UserCharacterService userCharacterService;
    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "캐릭터 정보 확인", description = "유저의 userCharacterId로  캐릭터 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 해당 캐릭터를 찾기 성공", content = @Content(schema = @Schema(implementation = UserCharacterInfoResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 유저의 해당 캐릭터를 찾기 실패")
    public ResponseEntity<CommonResponseEntity> getUserCharacterInfo(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("캐릭터 정보 확인 getUserCharacterInfo - userId: {}", userId);

        UserCharacterInfoResponse userCharacterInfo = userCharacterService.getUserCharacterInfo(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterInfo);
    }

    @GetMapping("/exp")
    @Operation(summary = "캐릭터 경험치 정보 확인", description = "유저의 userCharacterId로  캐릭터 경험치 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 해당 캐릭터의 경험치 정보를 찾기 성공", content = @Content(schema = @Schema(implementation = UserCharacterExpInfoResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 유저의 해당 캐릭터의 경험치 정보를 찾기 실패")
    public ResponseEntity<CommonResponseEntity> getUserCharacterExpInfo(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("캐릭터 경험치 정보 확인 getUserCharacterExpInfo - userId: {}", userId);

        UserCharacterExpInfoResponse userCharacterExpInfo = userCharacterService.getUserCharacterExpInfo(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterExpInfo);
    }

    @GetMapping("/list")
    @Operation(summary = "유저의 캐릭터 목록 정보 가져오기", description = "유저가 보유한 캐릭터와 보유하지 않은 캐릭터 목록 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 정보 찾기 성공", content = @Content(schema = @Schema(implementation = UserCharacterInfoResponse.class)))
    public ResponseEntity<CommonResponseEntity> getUserCharacterListInfo(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("유저의 캐릭터 목록 정보 가져오기 getUserCharacterListInfo - userId: {}", userId);

        UserCharacterListInfoResponse userCharacterInfoList = userCharacterService.getUserCharacterInfoList(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterInfoList);
    }

    // /api/character/stat?value=power&userCharacterId=3
    @GetMapping("/stat")
    @Operation(summary = "스탯 분배", description = "유저의 캐릭터 스탯을 분배")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 스탯 분배 성공", content = @Content(schema = @Schema(implementation = UserCharacterStatResponse.class)))
    @ApiResponse(responseCode = "403", description = "C400 - 유저의 해당 캐릭터 스탯 포인트가 부족")
    @ApiResponse(responseCode = "404", description = "C400 - 유저의 해당 캐릭터를 찾기 실패")
    public ResponseEntity<CommonResponseEntity> statDistribution(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("value") String value) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("스탯 분배 statDistribution - userId: {}, value: {}", userId, value);

        UserCharacterStatResponse userCharacterStatInfo = userCharacterService.addStatPoint(userId, value);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterStatInfo);
    }

//    @PostMapping("/stat/reset")
    @GetMapping("/stat/reset")
    @Operation(summary = "스탯 분배 초기화", description = "유저의 캐릭터 스탯을 분배 초기화")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 스탯 분배 초기화 성공", content = @Content(schema = @Schema(implementation = ResetStatResponse.class)))
    @ApiResponse(responseCode = "400", description = "C400 - 이미 스탯 초기화 버튼 누름")
    public ResponseEntity<CommonResponseEntity> resetStatDistribution(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("스탯 분배 초기화 resetStatDistribution - userIdv: {}", userId);

        ResetStatResponse resetStatResponse = userCharacterService.resetInitStatus(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, resetStatResponse);
    }

    @PostMapping("/change")
    @Operation(summary = "캐릭터 변경", description = "유저의 현재 캐릭터 변경")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 변경 성공")
    public ResponseEntity<CommonResponseEntity> changeUserCharacter(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody ChangeUserCharacterIdRequest changeUserCharacterIdRequest) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("캐릭터 변경 changeUserCharacter - userId: {}, userCharacterId: {}", userId, changeUserCharacterIdRequest.getSelectCharacterId());
        ChangeCharacterIdResponse changeCharacter = userCharacterService.changeUserCharacter(userId, changeUserCharacterIdRequest);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, changeCharacter);
    }

    @GetMapping("/test")
    @Operation(summary = "통신 테스트", description = "통신 테스트")
    @ApiResponse(responseCode = "200", description = "S200 - 통신 테스트 성공", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<CommonResponseEntity> test() {
        log.info("통신 테스트");

        TimeZone serverTimeZone = TimeZone.getDefault();
        System.out.println("스프링 서버의 현재 시간대: " + serverTimeZone.getID());

        // 현재 시간을 밀리초로 가져옵니다.
        long currentTimeMillis = System.currentTimeMillis();

        // 밀리초를 인스턴트로 변환합니다.
        Instant instant = Instant.ofEpochMilli(currentTimeMillis);

        // 인스턴트를 현지 시간으로 변환합니다.
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // 출력할 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 현재 시간을 지정된 형식으로 출력
        String formattedDateTime = localDateTime.format(formatter);
        System.out.println("스프링 서버의 현재 시간: " + formattedDateTime);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, "통신 테스트2 - " + serverTimeZone.getID() + " , " + formattedDateTime);
    }

    @Operation(summary = "경험치 아이템 사용", description = "경험치가 5씩 증가하는 아이템을 사용한다.")
    @ApiResponse(responseCode = "200", description = "S200 - 아이템 사용 성공")
    @GetMapping("/item/use/expitem")
    public ResponseEntity<CommonResponseEntity> useExpItem(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("quantity") int quantity){
        int userId = userDetail.getUsers().getUserId();
        log.info("경험치 아이템 사용 확인 useExpItem - userId: {}, quantity: {}", userId, quantity);
        LevelUpResponse levelUpResponse = itemService.usingExpItem(userId, quantity);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, levelUpResponse);
    }
}
