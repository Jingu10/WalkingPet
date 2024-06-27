package com.walkerholic.walkingpet.domain.fcmtoken.repository;

import com.walkerholic.walkingpet.domain.fcmtoken.entity.FcmToken;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    boolean existsByToken(String token);

    FcmToken findByUser(Users user);
}
