package com.walkerholic.walkingpet.domain.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_step")
public class UserStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_step_id")
    private Integer userStepId;

    @Column(name = "yesterday_step", nullable = false)
    private Integer yesterdayStep;

    @Column(name = "daily_step", nullable = false)
    private Integer dailyStep;

    @Column(name = "accumulation_step", nullable = false)
    private Integer accumulationStep;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Builder
    public UserStep(Users user){
        this.yesterdayStep = 0;
        this.dailyStep = 0;
        this.accumulationStep = 0;
        this.user = user;
    }

    public void updateDailyStep(int step) { this.dailyStep = step;}

    public void updateUserStepInfo(int dailyStep) {
        this.yesterdayStep = dailyStep;
        this.accumulationStep += dailyStep;
        this.dailyStep = 0;
    }

    public void updateUserDailyStep(int dailyStep) {
        this.dailyStep = dailyStep;
    }
}