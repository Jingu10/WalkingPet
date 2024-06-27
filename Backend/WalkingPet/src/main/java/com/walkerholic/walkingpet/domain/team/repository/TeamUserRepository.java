package com.walkerholic.walkingpet.domain.team.repository;

import com.walkerholic.walkingpet.domain.team.entity.Team;
import com.walkerholic.walkingpet.domain.team.entity.TeamUser;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Integer> {
    @Query("SELECT COUNT(tu) FROM TeamUser tu WHERE tu.team.teamId = :teamId")
    Integer countByTeamId(@Param("teamId") Integer teamId);

    @Query("SELECT COUNT(tu) FROM TeamUser tu WHERE tu.user = :user")
    Integer countByUser(@Param("user") Users user);

    @Query("SELECT COUNT(tu) FROM TeamUser tu WHERE tu.user.userId = :userId")
    Integer countByUserId(@Param("userId") int userId);

    @Query("SELECT tu.team FROM TeamUser tu WHERE tu.user.userId = :userId")
    List<Team> findTeamsByUserId(@Param("userId") Integer userId);

    @Query("SELECT tu FROM TeamUser tu WHERE tu.team.teamId = :teamId")
    List<TeamUser> findByTeamId(@Param("teamId") Integer teamId);

    boolean existsByTeamAndUser(Team team, Users user);

    Optional<TeamUser> findByUserAndTeam(Users user, Team team);

    void deleteByTeam(Team team);
}
