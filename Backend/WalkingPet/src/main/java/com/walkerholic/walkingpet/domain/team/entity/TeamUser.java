package com.walkerholic.walkingpet.domain.team.entity;

import com.walkerholic.walkingpet.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_user")
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Integer groupUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Builder
    public TeamUser(Team team, Users user) {
        this.team = team;
        this.user = user;
    }

}
