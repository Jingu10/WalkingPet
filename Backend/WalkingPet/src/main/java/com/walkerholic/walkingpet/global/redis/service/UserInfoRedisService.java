package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoRedisService {
    private static final String USER_KEY = "USER_INFO";

    @Resource(name = "redisTemplateForStepCount")
    private final RedisTemplate<String, Integer> rankigRedisTemplate;

    // mysql 모든 유저 정보 저장
    @Transactional(readOnly = false)
    public void saveAllUser(UserRedisDto UserRedisDto) {
        HashOperations<String, Integer, UserRedisDto> hashOperation = rankigRedisTemplate.opsForHash();
        hashOperation.put(USER_KEY, UserRedisDto.getUserId(), UserRedisDto);
    }

    // 특정 유저 정보 저장
    @Transactional(readOnly = false)
    public void saveUser(UserRedisDto userRedisDto) {
        HashOperations<String, Integer, UserRedisDto> hashOperation = rankigRedisTemplate.opsForHash();
        hashOperation.put(USER_KEY, userRedisDto.getUserId(), userRedisDto);
    }


    // 사용자가 캐릭터 변경시 redis에도 값 변경
    @Transactional(readOnly = false)
    public void updateCharacterId(int userId, int characterId) {
        UserRedisDto user = getUser(userId);
        rankigRedisTemplate.opsForHash().put(USER_KEY, userId, user.changeCharacterId(characterId));
    }

    // 사용자가 닉네임 변경시 redis에도 값 변경
    @Transactional(readOnly = false)
    public void updateNickname(int userId, String nickname) {
        UserRedisDto user = getUser(userId);
        rankigRedisTemplate.opsForHash().put(USER_KEY, userId, user.changeNickname(nickname));
    }

    @Transactional(readOnly = true)
    public UserRedisDto getUser(int userId) {
        HashOperations<String, Integer, UserRedisDto> hashOperations = rankigRedisTemplate.opsForHash();

        return hashOperations.get(USER_KEY, userId);
    }
}
