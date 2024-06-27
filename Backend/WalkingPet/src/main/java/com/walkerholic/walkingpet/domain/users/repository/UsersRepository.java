package com.walkerholic.walkingpet.domain.users.repository;

import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Query("SELECT U.nickname FROM Users U WHERE U.userId = :userId")
    String findNicknameByUserId(Integer userId);

    @Query("SELECT U FROM Users U WHERE U.email = :email")
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String userEmail);

    boolean existsByNickname(String nickname);

    Users findByUserId(Integer userId);
}
