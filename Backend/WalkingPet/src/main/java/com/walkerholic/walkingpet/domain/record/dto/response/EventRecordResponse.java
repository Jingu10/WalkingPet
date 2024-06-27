package com.walkerholic.walkingpet.domain.record.dto.response;

import com.walkerholic.walkingpet.domain.record.dto.EventRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Builder
@Slf4j
@Getter
public class EventRecordResponse {
    @Builder.Default
    List<EventRecord> eventRecordList = new ArrayList<>();
}
