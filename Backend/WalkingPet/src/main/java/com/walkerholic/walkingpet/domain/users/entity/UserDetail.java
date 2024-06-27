package com.walkerholic.walkingpet.domain.users.entity;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_detail")
public class UserDetail {
    private static final int INITIAL_BATTLE_RATING = 1000;
    private static final int LIMIT_DAILY_BATTLE_COUNT = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_detail_id")
    private Integer userDetailId;

    @Column(name = "battle_rating", nullable = false, columnDefinition = "INT DEFAULT 1000")
    private Integer battleRating;

    //0: 초기화 안함, 1: 초기화 함
    @Column(name = "init_status", columnDefinition = "TINYINT(1)", nullable = false)
    private byte initStatus;

    @Column(name = "battle_count", nullable = false)
    private byte battleCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_character_id", nullable = false)
    private UserCharacter selectUserCharacter;

    public void changeUserCharacter(UserCharacter character) { this.selectUserCharacter = character;}

    public void changeInitStatus() {this.initStatus = 1; };

    public void battleCountDeduction(){
        this.battleCount--;
    }

    public void updateBattleRating(int ratingScore){this.battleRating += ratingScore;}

    public void setBattleRating(int battleRating){this.battleRating = battleRating;}

    public void resetBattleCount(){
        this.battleCount = 10;
    }

    public void resetBattleCountAndInitStatus() {
        this.battleCount = 10;
        this.initStatus = 0;
    }

    @Builder
    public UserDetail(Users user, UserCharacter selectUserCharacter){
        this.user = user;
        this.selectUserCharacter = selectUserCharacter;
        this.battleRating = INITIAL_BATTLE_RATING;   //초기 레이팅은 1000고정
        this.initStatus = 0;
        this.battleCount = LIMIT_DAILY_BATTLE_COUNT;
    }
}
