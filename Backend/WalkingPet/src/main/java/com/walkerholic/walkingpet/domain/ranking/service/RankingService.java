package com.walkerholic.walkingpet.domain.ranking.service;

import com.walkerholic.walkingpet.domain.ranking.dto.*;
import com.walkerholic.walkingpet.domain.ranking.dto.response.*;
import com.walkerholic.walkingpet.domain.team.entity.Team;
import com.walkerholic.walkingpet.domain.team.repository.TeamRepository;
import com.walkerholic.walkingpet.domain.team.repository.TeamUserRepository;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {
    private final UserStepRepository userStepRepository;
    private final UserDetailRepository userDetailRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    /*
     * 누적 걸음수 랭킹 가져오기
     */
    @Transactional(readOnly = true)
    public PersonalStepRankingAllInfoResponse getAccStepRanking(int userId) {
        //TODO: 동점 순위 일경우 가입 시간순으로
        List<UserStep> topUsers = userStepRepository.findTop10ByOrderByAccumulationStepDesc();

        List<AccStepRankingAndUserInfo> accStepRankingList = new ArrayList<>();
        for (UserStep userStepInfo : topUsers) {
            UserDetail userDetailInfo = userDetailRepository.findUserDetailByUser(userStepInfo.getUser())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

            accStepRankingList.add(AccStepRankingAndUserInfo.entityFrom(userDetailInfo, userStepInfo));
        }

        UserPersonalStepRankingResponse userAccStepRanking = getUserAccStepRanking(userId);
        return PersonalStepRankingAllInfoResponse.from(userAccStepRanking, accStepRankingList);
    }

    /*
     * 누적 걸음수 기준으로 top 10 랭킹 가져오기 - redis 용
     */
    @Transactional(readOnly = true)
    @Cacheable(value="accStepRankingTop10", key = "'accStepTop10'")
    public StepRankingResponse getAccStepRankingTop10() {
        log.info("RankingService getAccStepRankingTop10 누적 걸음수 top10(캐싱 적용 x)");
        List<UserStep> topUsers = userStepRepository.findTop10ByOrderByAccumulationStepDesc();

        return StepRankingResponse.from(calculateAccRanking(topUsers));
    }

    /*
     * 누적 걸음수 기준으로 top 3 랭킹 가져오기 - redis 용
     */
    @Transactional(readOnly = true)
    @Cacheable(value="accStepRankingTop3", key = "'accStepTop3'")
    public StepRankingResponse getAccStepRankingTop3() {
        log.info("RankingService getAccStepRankingTop3 누적 걸음수 top3(캐싱 적용 x)");
        List<UserStep> topUsers = userStepRepository.findByTop3OrderByAccumulationStepDesc();

        return StepRankingResponse.from(calculateAccRanking(topUsers));
    }

    /*
     * 어제 걸음수 기준으로 top 10 랭킹 가져오기 - redis 용
     */
    @Transactional(readOnly = true)
    @Cacheable(value="yesterdayStepRankingTop10", key = "'ydStepTop10'")
    public StepRankingResponse getYesterdayStepRankingTop10() {
        log.info("RankingService getYesterdayStepRanking0Top10 어제 걸음수 top10(캐싱 적용 x)");
        List<UserStep> topUsers = userStepRepository.findTop10ByOrderByYesterdayStepDesc();

        return StepRankingResponse.from(calculateYesterdayRanking(topUsers));
    }

    /*
     * 어제 걸음수 기준으로 top 3 랭킹 가져오기 - redis 용
     */
    @Transactional(readOnly = true)
    @Cacheable(value="yesterdayStepRankingTop3", key = "'ydStepTop3'")
    public StepRankingResponse getYesterdayStepRankingTop3() {
        log.info("RankingService getYesterdayStepRankingTop3 어제 걸음수 top3(캐싱 적용 x)");
        List<UserStep> topUsers = userStepRepository.findByTop3OrderByYesterdayStepDesc();

        return StepRankingResponse.from(calculateYesterdayRanking(topUsers));
    }


    /*
     * 누적 걸음수 기준으로 개인 랭킹에서 해당 유저의 랭킹 가져오기
     */
    @Transactional(readOnly = true)
    public UserPersonalStepRankingResponse getUserAccStepRanking(int userId) {
        //TODO: Redis 데이터로 변경
        int userRanking = userStepRepository.findUserPersonalRankingByAccStep(userId);
        UserStep userStep = userStepRepository.findUserStepByUserUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));

        return UserPersonalStepRankingResponse.from(userRanking, userStep.getAccumulationStep());
    }

    /*
    사용자들의 누적 걸음수 데이터 및 사용자 데이터 가져오기
    userId, nickname, step
     */
    @Transactional(readOnly = true)
    public List<AccStepRankingAndUserInfo> getUserAccStepAndInfoList() {
        List<UserDetail> allByUser = userDetailRepository.findAllByUserStatus(1);

        List<AccStepRankingAndUserInfo> accStepRankingList = new ArrayList<>();
        for (UserDetail userDetailInfo : allByUser) {
            UserStep userStepInfo = userStepRepository.findUserStepByUser(userDetailInfo.getUser())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));
            accStepRankingList.add(AccStepRankingAndUserInfo.entityFrom(userDetailInfo, userStepInfo));
        }

        return accStepRankingList;
    }


    /*
        사용자들의 어제 걸음수 데이터 가져오기
        yesterdayStep
     */
    @Transactional(readOnly = true)
    public List<YesterdayStepRankingInfo> getUserYseterdayStepList() {
        List<UserDetail> allByUser = userDetailRepository.findAllByUserStatus(1);

        List<YesterdayStepRankingInfo> accStepRankingList = new ArrayList<>();
        for (UserDetail userDetailInfo : allByUser) {
            UserStep userStepInfo = userStepRepository.findUserStepByUser(userDetailInfo.getUser())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));
            accStepRankingList.add(YesterdayStepRankingInfo.entityFrom(userStepInfo));
        }

        return accStepRankingList;
    }

    /*
    사용자들의 실시간 걸음수 데이터 가져오기
    yesterdayStep
    */
    @Transactional(readOnly = true)
    public List<ReailtimeStepRankingInfo> getUserRealtimeStepList() {
        List<UserDetail> allByUser = userDetailRepository.findAllByUserStatus(1);

        List<ReailtimeStepRankingInfo> reailtimeStepRankingList = new ArrayList<>();
        for (UserDetail userDetailInfo : allByUser) {
            UserStep userStepInfo = userStepRepository.findUserStepByUser(userDetailInfo.getUser())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));

            reailtimeStepRankingList.add(ReailtimeStepRankingInfo.entityFrom(userStepInfo));
        }

        return reailtimeStepRankingList;
    }

    // 그룹 랭킹 상위 10개 가져오기
    @Transactional(readOnly = true)
    @Cacheable(value="teamRankingTop10", key = "'teamTop10'")
    public TeamRankingResponse getTeamRankingTop10() {
        log.info("그룹 랭킹 상위 10개 Mysql 데이터(캐싱 적용 x)");
        List<Team> teamTop10 = teamRepository.findTop10ByOrderByPointDesc();

        List<TeamRanking> teamRankingTop10List = new ArrayList<>();

        int rank = 0;
        int previousPoints = -1;
        int sameRankCount = 0;
        for (Team team: teamTop10) {
            // 동점 계산
            if (team.getPoint() != previousPoints) {
                rank += sameRankCount + 1;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            teamRankingTop10List.add(TeamRanking.from(team, rank));
            previousPoints = team.getPoint();
        }

        return TeamRankingResponse.from(teamRankingTop10List);
    }

    // 나의 그룹 랭킹 가져오기
    @Transactional(readOnly = true)
    public TeamRankingResponse getMyTeamRanking(int userId) {
        // TODO: redis에 그룹 포인트 저장 방식으로 변경
        List<Team> myTeamRanking = teamRepository.findTeamsByUserIdOrderByPointDesc(userId);

        List<TeamRanking> myTeamRankingList = new ArrayList<>();

        for (Team team: myTeamRanking) {
            Integer userTeamRanking = teamRepository.findUserTeamRanking(team.getTeamId());
            myTeamRankingList.add(TeamRanking.from(team, userTeamRanking));
        }

        return TeamRankingResponse.from(myTeamRankingList);
    }

    /**
     * 나의 그룹 수 가져오기
     * @param userId 가입 그룹 수 확인을 위한 유저 아이디
     * @return int(가입한 그룹 수)
     */
    @Transactional(readOnly = true)
    public GroupCountResponse getMyGroupCount(int userId) {
        int groupCount = teamUserRepository.countByUserId(userId);
        return GroupCountResponse.from(groupCount);
    }

    // 배틀 랭킹 top 10
    @Cacheable(value="battleRankingTop10", key = "'battleTop10'")
    public BattleRankingResponse getBattleRankingTop10() {
        log.info("배틀 랭킹 상위 10개 Mysql 데이터(캐싱 적용 x)");
        List<UserDetail> top10 = userDetailRepository.findTop10ByOrderByBattleRatingDesc();

        return BattleRankingResponse.from(calculateBattleRanking(top10));
    }

    // 배틀 랭킹 top 3
    @Cacheable(value="battleRankingTop3", key = "'battleTop3'")
    public BattleRankingResponse getBattleRankingTop3() {
        log.info("배틀 랭킹 상위 3개 Mysql 데이터(캐싱 적용 x)");
        List<UserDetail> top3 = userDetailRepository.findByTop3OrderByBattleRatingDesc();

        return BattleRankingResponse.from(calculateBattleRanking(top3));
    }

    // 배틀 랭킹 나의 순위 조회
    @Cacheable(value = "battleMyRank", key = "#userId")
    public BattleRankingList getBattleRankingMyRank(int userId) {
        log.info("배틀 랭킹 나의 순위 Mysql 데이터(캐싱 적용 x)");
        UserDetail userDetail = userDetailRepository.findUserAndUserCharacterByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        int myRank = userDetailRepository.findBattleRankingMyRankByBattleRating(userId);

        return BattleRankingList.mysqlFrom(userDetail, myRank);
    }

    // 누적 걸음수 랭킹 동점 계산
    public List<PersonalStepRankingInfo> calculateAccRanking(List<UserStep> topUsers) {
        int rank = 0;
        int previousStep = -1;
        int sameRankCount = 0;
        List<PersonalStepRankingInfo> StepRankingList = new ArrayList<>();

        for (UserStep userStepInfo: topUsers) {
            UserDetail userDetailInfo = userDetailRepository.findUserAndSelectUserCharacterByUserId(userStepInfo.getUser().getUserId())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

            if (userStepInfo.getAccumulationStep() != previousStep) {
                rank += sameRankCount + 1;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            StepRankingList.add(PersonalStepRankingInfo.entityFrom(userDetailInfo, userStepInfo, userStepInfo.getAccumulationStep(),rank));
            previousStep = userStepInfo.getAccumulationStep();
        }

        return StepRankingList;
    }

    // 어제 걸음수 랭킹 동점 계산
    public List<PersonalStepRankingInfo> calculateYesterdayRanking(List<UserStep> topUsers) {
        int rank = 0;
        int previousStep = -1;
        int sameRankCount = 0;
        List<PersonalStepRankingInfo> StepRankingList = new ArrayList<>();

        for (UserStep userStepInfo: topUsers) {
            UserDetail userDetailInfo = userDetailRepository.findUserAndSelectUserCharacterByUserId(userStepInfo.getUser().getUserId())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

            if (userStepInfo.getYesterdayStep() != previousStep) {
                rank += sameRankCount + 1;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            StepRankingList.add(PersonalStepRankingInfo.entityFrom(userDetailInfo, userStepInfo, userStepInfo.getYesterdayStep(), rank));
            previousStep = userStepInfo.getYesterdayStep();
        }

        return StepRankingList;
    }

    // 배틀 랭킹 동점 계산
    public List<BattleRankingList> calculateBattleRanking(List<UserDetail> topUsers) {
        int rank = 0;
        int previousRating = -1;
        int sameRankCount = 0;
        List<BattleRankingList> battleRankingList = new ArrayList<>();

        for (UserDetail userInfo: topUsers) {

            if (userInfo.getBattleRating() != previousRating) {
                rank += sameRankCount + 1;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            battleRankingList.add(BattleRankingList.mysqlFrom(userInfo, rank));
            previousRating = userInfo.getBattleRating();
        }

        return battleRankingList;
    }

}
