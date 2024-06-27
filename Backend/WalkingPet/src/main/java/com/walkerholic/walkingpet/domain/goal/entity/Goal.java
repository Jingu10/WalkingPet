package com.walkerholic.walkingpet.domain.goal.entity;

import com.walkerholic.walkingpet.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Integer goalId;

    @Column(name = "daily_goal", nullable = false)
    private Integer dailyGoal;

    @Column(name = "weekly_goal", nullable = false)
    private Integer weeklyGoal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    public void setWeeklyGoal(int weeklyGoal){
        this.weeklyGoal = weeklyGoal;
    }

    public void setDailyGoal(int dailyGoal){
        this.dailyGoal = dailyGoal;
    }

    @Builder
    public Goal(Users user){
        this.dailyGoal = 0;
        this.weeklyGoal = 0;
        this.user = user;
    }
}
