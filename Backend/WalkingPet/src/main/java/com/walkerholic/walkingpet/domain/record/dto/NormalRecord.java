package com.walkerholic.walkingpet.domain.record.dto;

import com.walkerholic.walkingpet.domain.record.entity.Record;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Getter
@Slf4j
@Builder
public class NormalRecord {
    private String imageUrl;
    private int recordId;
    private int characterId;
    private double latitude;
    private double longitude;
    private LocalDateTime regDate;

    public static NormalRecord from(Record record){
        String imageUrl = record.getImageUrl();
        String httpImageUrl = imageUrl.replace("https://", "http://");

        return NormalRecord.builder()
                .imageUrl(httpImageUrl)
                .recordId(record.getRecordId())
                .characterId(record.getCharacterId())
                .latitude(record.getLatitude().doubleValue())
                .longitude(record.getLongitude().doubleValue())
                .regDate(record.getRecordRegDate())
                .build();
    }
}
