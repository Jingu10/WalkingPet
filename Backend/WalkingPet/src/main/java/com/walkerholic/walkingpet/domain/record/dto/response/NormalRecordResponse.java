package com.walkerholic.walkingpet.domain.record.dto.response;

import com.walkerholic.walkingpet.domain.record.dto.EventRecord;
import com.walkerholic.walkingpet.domain.record.dto.NormalRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Builder
@Slf4j
@Getter
public class NormalRecordResponse {
    @Builder.Default
    List<NormalRecord> normalRecordList = new ArrayList<>();
}
