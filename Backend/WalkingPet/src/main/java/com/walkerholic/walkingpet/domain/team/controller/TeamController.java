package com.walkerholic.walkingpet.domain.team.controller;

import com.walkerholic.walkingpet.domain.team.dto.request.CreateGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.EnterGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.ExitGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.JoinGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamDetailResponse;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamResponse;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamUsersResponse;
import com.walkerholic.walkingpet.domain.team.service.TeamService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    @Operation(summary = "전체 그룹 확인", description = "전체 그룹 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 전체 그룹 조회 성공", content = @Content(schema = @Schema(implementation = TeamResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 전체 그룹 조회 실패")
    @GetMapping("/all")
    public ResponseEntity<CommonResponseEntity> getAllTeam(@AuthenticationPrincipal CustomUserDetail userDetail){
        Integer userId = userDetail.getUsers().getUserId();
        List<TeamResponse> allTeam = teamService.getAllTeam(userId);
        log.info("전체 그룹 확인 getAllTeam");
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,allTeam);
    }

    @Operation(summary = "소속된 그룹 확인", description = "유저의 userId로 소속된 그룹 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저가 소속된 그룹들 조회 성공", content = @Content(schema = @Schema(implementation = TeamResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 유저가 소속된 그룹들 조회 실패")
    @GetMapping("/belong")
    public ResponseEntity<CommonResponseEntity> getUserTeams(@AuthenticationPrincipal CustomUserDetail userDetail){
        Integer userId = userDetail.getUsers().getUserId();
        List<TeamResponse> allTeam = teamService.getUserTeams(userId);
        log.info("소속된 그룹 확인 getUserTeams -  userId: {}", userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,allTeam);
    }

    @Operation(summary = "그룹 검색", description = "검색어 content가 이름에 포함된 그룹 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 검색 성공", content = @Content(schema = @Schema(implementation = TeamResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 검색 실패")
    @GetMapping("/search")
    public ResponseEntity<CommonResponseEntity> getSearchTeams(@RequestParam("content") String content){
        List<TeamResponse> searchTeams = teamService.getSearchTeams(content);
        log.info("그룹 검색 getSearchTeams -  content: {}", content);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,searchTeams);
    }

    @Operation(summary = "그룹 입장", description = "teamId와 비밀번호를 가지고 그룹에 입장하기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 입장 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 입장 실패")
    @PostMapping("/enter")
    public ResponseEntity<CommonResponseEntity> enterGroup(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody EnterGroupRequest enterGroupRequest){
        Integer userId = userDetail.getUsers().getUserId();
        teamService.enterGroup(enterGroupRequest,userId);
        log.info("그룹 입장 enterGroup -  enterGroupRequest: {}, userId:{}", enterGroupRequest,userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @Operation(summary = "그룹 상세보기", description = "특정 teamId를 통해 그룹 정보 상세보기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 상세보기 성공", content = @Content(schema = @Schema(implementation = TeamDetailResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 상세보기 실패")
    @GetMapping("/detail/{teamId}")
    public ResponseEntity<CommonResponseEntity> getSearchTeams(@AuthenticationPrincipal CustomUserDetail userDetail,@PathVariable("teamId") int teamId){
        Integer userId = userDetail.getUsers().getUserId();
        TeamDetailResponse teamDetail = teamService.getTeamDetail(teamId,userId);
        log.info("그룹 상세보기 teamDetail -  teamId: {}", teamId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,teamDetail);
    }

    @Operation(summary = "그룹 가입", description = "teamId를 가지고 그룹에 가입하기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 가입 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 가입 실패")
    @PostMapping("/join")
    public ResponseEntity<CommonResponseEntity> joinGroup(@AuthenticationPrincipal CustomUserDetail userDetail,  @RequestBody JoinGroupRequest joinGroupRequest){
        Integer userId = userDetail.getUsers().getUserId();
        teamService.joinGroup(joinGroupRequest,userId);
        log.info("그룹 가입 joinGroup -  teamId: {}, joinGroupRequest:{}", joinGroupRequest,userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @Operation(summary = "그룹 생성", description = "새로운 그룹 생성하기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 생성 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 생성 실패")
    @PostMapping("/create")
    public ResponseEntity<CommonResponseEntity> createGroup(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody CreateGroupRequest createGroupRequest){
        Integer userId = userDetail.getUsers().getUserId();
        teamService.createGroup(createGroupRequest,userId);
        log.info("그룹 생성 createGroup - createGroupRequest: {}, userId: {}", createGroupRequest, userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @Operation(summary = "그룹원 정보 조회", description = "특정 teamId에 해당하는 그룹원들의 상세정보 조회")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹원 정보 조회 성공", content = @Content(schema = @Schema(implementation = TeamUsersResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹원 정보 조회 실패")
    @GetMapping("/members/{teamId}")
    public ResponseEntity<CommonResponseEntity> getGroupMembersInfo(@PathVariable("teamId") int teamId){
        List<TeamUsersResponse> membersInfo = teamService.getGroupMembersInfo(teamId);
        log.info("그룹원 정보 조회 getGroupMembersInfo - teamId:{}", teamId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,membersInfo);
    }

    @Operation(summary = "그룹 나가기", description = "특정 teamId에 해당하는 그룹에서 나가기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 나가기 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 나가기 실패")
    @PostMapping("/exit")
    public ResponseEntity<CommonResponseEntity> exitGroup(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody ExitGroupRequest exitGroupRequest){
        Integer userId = userDetail.getUsers().getUserId();
        teamService.exitGroup(exitGroupRequest, userId);
        log.info("그룹 나가기 exitGroup - exitGroupRequest:{}, userId:{}", exitGroupRequest,userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/teamnamecheck")
    @Operation(summary = "그룹 이름 중복 체크", description = "입력한 그룹 이름이 기존 그룹 이름들과 중복되지 않는지 확인하기 ")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 이름 중복 체크 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 그룹 이름 중복 체크 실패")
    public ResponseEntity<CommonResponseEntity> checkAvailableTeamname(@RequestParam("teamName") String teamName) {
        log.info("그룹 이름 중복 체크 checkAvailableTeamname - teamName: {}", teamName);
        boolean isAvailable = teamService.checkAvailableNickname(teamName);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, isAvailable);
    }
}
