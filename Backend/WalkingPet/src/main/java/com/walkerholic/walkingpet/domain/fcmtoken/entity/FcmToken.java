package com.walkerholic.walkingpet.domain.fcmtoken.entity;

import com.walkerholic.walkingpet.domain.gacha.dto.response.GachaCountResponse;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fcmtoken")
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcmtoken_id")
    private Integer fcmTokenId;

    @Column(name = "token")
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Builder
    public FcmToken(Users user,String token){
        this.token = token;
        this.user = user;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
