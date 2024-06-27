package com.walkerholic.walkingpet.domain.users.repository;

import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserDetailRepository extends JpaRepository<UserDetail, Integer> {
    Optional<UserDetail> findUserDetailByUser(Users user);

    @Query("SELECT ud FROM UserDetail ud WHERE ud.user.userId = :userId")
    Optional<UserDetail> findByUserUserId(Integer userId);

    @Modifying
    @Query("UPDATE UserDetail ud SET ud.selectUserCharacter.userCharacterId = :userCharacterId WHERE ud.user.userId = :userId")
    void updateUserCharacterIdByUserId(Integer userId, Integer userCharacterId);

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.user u LEFT JOIN FETCH ud.selectUserCharacter uc WHERE u.userId = :userId")
//    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.selectUserCharacter uc WHERE ud.user.userId = :userId")
    Optional<UserDetail> findUserAndSelectUserCharacterByUserId(Integer userId);

    /**
     * fetch join 사용해서 userId로 UserDetail 찾고 UserCharacter 정보도 가져오기
     * @param userId 유저 아이디
     * @return UserDetail with UserCharacter
     */
    @Query("SELECT UD " +
            "FROM UserDetail UD " +
            "LEFT JOIN FETCH UD.user U " +
            "LEFT JOIN FETCH UD.selectUserCharacter UC " +
            "LEFT JOIN FETCH UC.character " +
            "WHERE U.userId = :userId")
    Optional<UserDetail> findByJoinFetchByUserId(Integer userId);

    @Query("SELECT UD " +
            "FROM UserDetail UD " +
            "LEFT JOIN FETCH UD.selectUserCharacter UC " +
            "WHERE UD.user.userId = :userId")
    Optional<UserDetail> findUserDetailByJoinFetchByUserId(int userId);

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.user LEFT JOIN FETCH ud.selectUserCharacter WHERE ud.user.status = :status")
    List<UserDetail> findAllByUserStatus(Integer status);

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.user LEFT JOIN FETCH ud.selectUserCharacter WHERE ud.user.userId = :userId")
    Optional<UserDetail> findUserAndUserCharacterByUserId(Integer userId);

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.selectUserCharacter WHERE ud.user.userId = :userId")
    Optional<UserDetail> findUserCharacterByUserId(Integer userId);

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.user LEFT JOIN FETCH ud.selectUserCharacter ORDER BY ud.battleRating DESC LIMIT 10")
    List<UserDetail> findTop10ByOrderByBattleRatingDesc();

    @Query("SELECT ud FROM UserDetail ud LEFT JOIN FETCH ud.user LEFT JOIN FETCH ud.selectUserCharacter ORDER BY ud.battleRating DESC LIMIT 3")
    List<UserDetail> findByTop3OrderByBattleRatingDesc();

    @Query("""
        SELECT COUNT(*) + 1
        FROM UserDetail ud1
        WHERE ud1.battleRating >
        (
            SELECT ud2.battleRating
            FROM UserDetail ud2
            WHERE ud2.user.userId = :userId
        )
        """)
    int findBattleRankingMyRankByBattleRating(Integer userId);
}
