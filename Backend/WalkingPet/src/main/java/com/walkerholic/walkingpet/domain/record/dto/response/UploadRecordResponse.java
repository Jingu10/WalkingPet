package com.walkerholic.walkingpet.domain.record.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Slf4j
@Getter
public class UploadRecordResponse {
    private String imageName;
    private String imageUrl;
    private int characterId;
    private LocalDateTime regDate;
}
