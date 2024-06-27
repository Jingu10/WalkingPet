package com.walkerholic.walkingpet.domain.record.repository;

import com.walkerholic.walkingpet.domain.record.entity.RecordCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RecordCheckRepository extends JpaRepository<RecordCheck, Integer> {
    @Query("SELECT RC FROM RecordCheck RC WHERE RC.user.userId = :userId AND RC.record.recordId = :recordId")
    Optional<RecordCheck> getRecordCheckByUserIdAndRecordId(int userId, int recordId);
}
