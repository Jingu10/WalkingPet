package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StepService {
    private static final String USERS_KEY = "USER_REALTIME_STEP_RANKING_INFO";

    @Resource(name = "redisTemplateForStepCount")
    private final RedisTemplate<String, Integer> redisTemplate;

    private final UserStepRepository userStepRepository;

    public void updateUserStepInfo() {
        Map<Object, Object> dailySteps = redisTemplate.opsForHash().entries(USERS_KEY);

        for (Map.Entry<Object, Object> entry : dailySteps.entrySet()) {
            String userId = (String) entry.getKey();
            int dailyStep = (int) entry.getValue();

            UserStep userStep = userStepRepository.findById(Integer.valueOf(userId)).orElse(null);
            if (userStep != null) {
                userStep.updateUserStepInfo(dailyStep);
                userStepRepository.save(userStep);
            }
        }
    }
}