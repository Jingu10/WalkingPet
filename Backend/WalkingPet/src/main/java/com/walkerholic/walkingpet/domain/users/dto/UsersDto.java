package com.walkerholic.walkingpet.domain.users.dto;

import com.walkerholic.walkingpet.domain.users.entity.Users;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UsersDto {
    private Integer userId;
    private String email;
    private String nickname;

    public static UsersDto from(Users users) {
        return UsersDto.builder()
                .email(users.getEmail())
                .userId(users.getUserId())
                .nickname(users.getNickname())
                .build();
    }
}
