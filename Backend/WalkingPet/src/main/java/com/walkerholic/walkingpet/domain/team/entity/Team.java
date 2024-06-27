package com.walkerholic.walkingpet.domain.team.entity;

import com.walkerholic.walkingpet.domain.team.dto.response.TeamResponse;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Integer teamId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "explanation", nullable = false)
    private String explanation;

    @Column(name = "status", nullable = false)
    private byte status;

    @Column(name = "password")
    private String password;

    @Column(name = "point", nullable = false)
    private Integer point;

    @CreationTimestamp
    @Column(name = "create_date",nullable = false)
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Builder
    public Team(String name, String explanation, String password, byte status, Users user) {
        this.name = name;
        this.explanation = explanation;
        this.password = password;
        this.user = user;
        this.status = status;
        this.point = 0;
    }

    public void addPoint(int point) {
        this.point += point;
    }

}
