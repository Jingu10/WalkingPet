package com.walkerholic.walkingpet.domain.record.dto;

import com.walkerholic.walkingpet.domain.record.entity.Record;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class SelectUserRecord {
    //기록확인하려는 유저의 닉네임
    private String nickname;
    //기록 확인하려는 유저의 캐릭터
    private int characterId;
    //기록에 담긴 설명
    private String content;
    //기록 사진 url
    private String imageUrl;
    //기록 등록 날짜
    LocalDateTime regDate;

    public static SelectUserRecord from(Record record){
        String imageUrl = record.getImageUrl();
        String httpImageUrl = imageUrl.replace("https://", "http://");

        return SelectUserRecord.builder()
                .nickname(record.getUser().getNickname())
                .characterId(record.getCharacterId())
                .content(record.getContent())
                .imageUrl(httpImageUrl)
                .regDate(record.getRecordRegDate())
                .build();
    }
}
