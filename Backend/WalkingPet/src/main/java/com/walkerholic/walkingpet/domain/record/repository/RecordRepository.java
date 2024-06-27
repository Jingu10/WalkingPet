package com.walkerholic.walkingpet.domain.record.repository;

import com.walkerholic.walkingpet.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Query("SELECT R FROM Record R WHERE R.user.userId = :userId")
    List<Record> findAllByUserId(int userId);

    @Query("SELECT R FROM Record R WHERE R.user.userId = :userId AND R.imageName = :imageName")
    Optional<Record> findByUserIdAndImageName(int userId, String imageName);

    @Query("SELECT R FROM Record R WHERE R.isEvent = 1")
    Optional<List<Record>> findByIsEvent();

    @Query("SELECT R FROM Record R WHERE R.isEvent = 1 AND R.city = :city")
    Optional<List<Record>> findByIsEventAndCity(String city);
}
