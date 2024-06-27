package com.walkerholic.walkingpet.domain.team.repository;

import com.walkerholic.walkingpet.domain.team.entity.Team;
import com.walkerholic.walkingpet.domain.team.entity.TeamUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByNameContaining(String name);

    Optional<Team> findByTeamId(Integer teamId);

    List<Team> findTop10ByOrderByPointDesc();

    @Query("SELECT tu.team FROM TeamUser tu JOIN tu.team t WHERE tu.user.userId = :userId ORDER BY t.point DESC")
    List<Team> findTeamsByUserIdOrderByPointDesc(@Param("userId") Integer userId);

    @Query("""
        SELECT COUNT(*) + 1
        FROM Team t1
        WHERE t1.point >
        (
            SELECT t2.point
            FROM Team t2
            WHERE t2.teamId = :teamId
        )
        """)
    Integer findUserTeamRanking(Integer teamId);

    boolean existsByName(String name);

    @Query("SELECT t FROM Team t WHERE t.teamId NOT IN (SELECT tu.team.teamId FROM TeamUser tu WHERE tu.user.userId = :userId)")
    List<Team> findNotJoinedTeams(@Param("userId") int userId);

}
