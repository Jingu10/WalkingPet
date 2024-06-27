package com.walkerholic.walkingpet.domain.users.repository;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserStepRepository extends JpaRepository<UserStep, Integer> {
    Optional<UserStep> findUserStepByUser(Users user);

    Optional<UserStep> findUserStepByUserUserId(Integer userId);

    List<UserStep> findTop10ByOrderByAccumulationStepDesc();

    List<UserStep> findTop10ByOrderByYesterdayStepDesc();

    @Query("SELECT us FROM UserStep us ORDER BY us.accumulationStep DESC LIMIT 3")
    List<UserStep> findByTop3OrderByAccumulationStepDesc();

    @Query("SELECT us FROM UserStep us ORDER BY us.yesterdayStep DESC LIMIT 3")
    List<UserStep> findByTop3OrderByYesterdayStepDesc();

    @Query("SELECT us FROM UserStep us WHERE us.user.status = 1 ORDER BY us.accumulationStep DESC")
    List<UserStep> findUserStepList();

    @Query("SELECT US.dailyStep FROM UserStep US WHERE US.user.userId = :userId")
    Integer findUserDailyStep(int userId);

    @Query("""
        SELECT COUNT(*) + 1
        FROM UserStep us1
        WHERE us1.accumulationStep >
        (
            SELECT us2.accumulationStep
            FROM UserStep us2
            WHERE us2.user.userId = :userId
        )
        """)
    int findUserPersonalRankingByAccStep(Integer userId);

    @Query("SELECT us.yesterdayStep FROM UserStep us WHERE us.user.userId = :userId")
    Integer findUserYesterdayStep(int userId);
}
