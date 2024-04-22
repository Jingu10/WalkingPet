package com.walkerholic.walkingpet.domain.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_detail_id")
    private Integer userDetailId;

    @Column(name = "battle_rating")
    private Integer battleRating;

    @Column(name = "normal_box_count")
    private Integer normalBoxCount;

    @Column(name = "luxury_box_count")
    private Integer luxuryBoxCount;

    //0: 초기화 안함, 1: 초기화 함
    @Column(name = "init_status", columnDefinition = "TINYINT(1)")
    private boolean initStatus;

    @Column(name = "battle_count")
    private byte battleCount;
}
