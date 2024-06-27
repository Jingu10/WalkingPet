package com.walkerholic.walkingpet.domain.goal.repository;

import com.walkerholic.walkingpet.domain.goal.entity.Goal;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
    Optional<Goal> findGoalByUser(Users user);

    Optional<Goal> findGoalByUserUserId(int userId);
}
