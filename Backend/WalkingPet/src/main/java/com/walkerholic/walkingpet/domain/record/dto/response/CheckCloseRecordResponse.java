package com.walkerholic.walkingpet.domain.record.dto.response;

import com.walkerholic.walkingpet.domain.record.dto.SelectUserRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@Getter
public class CheckCloseRecordResponse {
    private Boolean isClose;
    private Boolean isCheck;
    private double distance;
    SelectUserRecord selectUserRecord;

    @Builder
    CheckCloseRecordResponse(Boolean isClose, Boolean isCheck, double distance, SelectUserRecord selectUserRecord){
        this.isClose = isClose;
        this.isCheck = isCheck;
        this.distance = distance;
        this.selectUserRecord = selectUserRecord;
    }
}
