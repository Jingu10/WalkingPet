package com.walkerholic.walkingpet.domain.fcmtoken.repository;

import com.walkerholic.walkingpet.domain.fcmtoken.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    boolean existsByToken(String token);
}
