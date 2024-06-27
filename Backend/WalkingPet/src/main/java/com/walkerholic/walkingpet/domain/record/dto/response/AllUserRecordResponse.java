package com.walkerholic.walkingpet.domain.record.dto.response;

import com.walkerholic.walkingpet.domain.record.dto.MyRecordResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Builder
@Slf4j
@Getter
public class AllUserRecordResponse {
    @Builder.Default
    private List<MyRecordResponse> myRecordResponsesList = new ArrayList<>();
}