package com.walkerholic.walkingpet.global.error;

import lombok.Getter;

@Getter
public enum GlobalErrorCode {
    /**
     * 에러코드 규칙 :
     * 1. 코드 맨 앞에는 연관된 Entity의 첫글자의 대문자를 적는다 ex)  Member -> M
     * 2. 에러 코드와 이름 , 메시지가 최대한 모호하지 않게 작성합니다.
     * 3. 공통으로 발생하는 에러에 대해서는 Global -> G를 붙여서 작성 합니다.
     */
    SEVER_ERROR(500, "E500", "정의되지 않은 서버 오류 발생하였습니다."),

    // 로그인 관련
//    ACCESS_DENIED(401, "U400", "허용되지 않은 사용자입니다"),
//    ACCESS_TOKEN_NOT_FOUND(401, "U400", "액세스 토큰이 존재하지 않습니다."),
//    TOKEN_EXPIRED(401, "U401", "토큰이 만료되었습니다."),
    USER_NOT_FOUND(404, "U402", "해당 id에 해당하는 사용자가 없습니다."),
    USER_NOT_FOUND_EMAIL(404, "U403", "해당 이메일과 해당하는 사용자가 없습니다."),
//    DUPLICATE_NICKNAME(400, "U404", "이미 존재하는 닉네임입니다."),

    // 캐릭터 관련
    USER_CHARACTER_NOT_FOUND(404, "C401", "유저의 해당 캐릭터를 찾을 수 없습니다."),
    CHARACTER_NOT_FOUND(404, "C402", "존재 하지 않는 캐릭터입니다."),
    INSUFFICIENT_STAT_POINT(403, "C403", "유저의 해당 캐릭터 스탯 포인트가 부족합니다."),
    STAT_INIT_LIMIT_EXCEEDED(403, "C404", "이미 스탯 초기화를 수행했습니다. 하루에 한 번만 초기화할 수 있습니다."),

    // 팀 관련
    TEAM_NOT_FOUND(404, "G400", "해당 그룹을 찾을 수 없습니다."),
    TEAM_USER_NOT_FOUND(404, "G401", "유저의 해당 팀을 찾을 수 없습니다."),
    JOIN_TEAMS_EXCEEDED(403, "G402", "한 유저가 가입 가능한 최대 그룹 수를 초과했습니다."),
    TEAM_MEMBER_EXCEEDED(403, "G403", "한 그룹에 가입 가능한 최대 인원 수를 초과했습니다."),
    ALREADY_JOINED_GROUP(403, "G404", "이미 그룹에 가입한 사용자입니다."),
    TEAM_PASSWORD_INCORRECT(404, "G405", "입력하신 그룹의 비밀번호가 일치하지 않습니다."),

    // 아이템 관련
    ITEM_NOT_FOUND(404, "I400", "해당 아이템을 찾을 수 없습니다."),
    USER_ITEM_NOT_EXIST(403, "I401", "유저의 해당 아이템 보유 정보를 확인할 수 없습니다."),
    USER_ITEM_NOT_FOUND_EXP(404, "I402", "경험치 아이템을 가진 유저를 확인할 수 없습니다."),
    USER_ITEM_NOT_FOUND_BOX(404, "I403", "박스 아이템을 가진 유저를 확인할 수 없습니다."),

    // 유저 상세 정보 관련(user_detail)
    USER_DETAIL_NOT_FOUND(404, "D400", "유저의 상세정보를 찾을 수 없습니다."),

    // 유저 걸음수 관련(user_step)
    USER_STEP_NOT_FOUND(404, "S400", "유저의 걸음수 정보를 찾을 수 없습니다."),

    //목표 관련(goal)
    USER_GOAL_NOT_FOUND(404, "G400", "유저의 목표 정보를 찾을 수 없습니다."),

    //파일 업로드 관련
    UPLOAD_FILE_NOT_FOUND(404, "F400", "업로드할 파일을 찾을 수 없습니다."),

    //기록 관련(record)
    RECORD_NOT_FOUND(404,"R400", "기록을 찾을 수 없습니다."),
    DELETE_RECORD_NOT_FOUND(404,"R401", "삭제할 기록을 찾을 수 없습니다."),
    EVENT_NOT_FOUND(404, "R402", "이벤트 기록을 찾을 수 없습니다."),

    // 정의하지 않은 그 외의 에러
    USER_BATTLE_COUNT_LACK(404, "B400", "일일 배틀 횟수 초과");

    //기타 에러

    private final int status;
    private final String code;
    private final String message;

    GlobalErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}