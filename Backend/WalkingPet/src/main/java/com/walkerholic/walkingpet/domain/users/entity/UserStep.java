package com.walkerholic.walkingpet.domain.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_step_id")
    private Integer userStepId;

    @Column(name = "battle_rating")
    private Integer battleRating;

    @Column(name = "yesterday_step")
    private Integer yesterdayStep;

    @Column(name = "daily_step")
    private Integer dailyStep;

    @Column(name = "accumulation_step")
    private Integer accumulationStep;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

}
