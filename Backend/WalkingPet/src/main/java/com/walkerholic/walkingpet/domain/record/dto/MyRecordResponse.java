package com.walkerholic.walkingpet.domain.record.dto;

import com.walkerholic.walkingpet.domain.record.entity.Record;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Builder
@Slf4j
@Getter
public class MyRecordResponse {
    private String imageUrl;
    private String content;
    private int characterId;
    private LocalDateTime regDate;

    public static MyRecordResponse from(Record record){
        String imageUrl = record.getImageUrl();
        String httpImageUrl = imageUrl.replace("https://", "http://");

        return MyRecordResponse.builder()
                .imageUrl(httpImageUrl)
                .content(record.getContent())
                .characterId(record.getCharacterId())
                .regDate(record.getRecordRegDate())
                .build();
    }
}
