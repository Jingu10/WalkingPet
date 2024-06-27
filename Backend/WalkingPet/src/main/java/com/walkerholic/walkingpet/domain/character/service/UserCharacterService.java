package com.walkerholic.walkingpet.domain.character.service;

import com.walkerholic.walkingpet.domain.character.dto.UserCharacterListInfo;
import com.walkerholic.walkingpet.domain.character.dto.request.ChangeUserCharacterIdRequest;
import com.walkerholic.walkingpet.domain.character.dto.response.*;
import com.walkerholic.walkingpet.domain.character.entity.Character;
import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.repository.CharacterRepository;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.global.auth.dto.response.MainResponse;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import com.walkerholic.walkingpet.global.redis.service.RealtimeStepRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.UserInfoRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.walkerholic.walkingpet.domain.util.UpgradeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserCharacterService {
    private static final int REDUCE_STAT_POINT = 1; // 스탯 사용시 줄어 드는 스탯 포인트
    private static final int ADD_STAT = 1; // 스탯 사용시 줄어 드는 스탯 포인트

    private final UserCharacterRepository userCharacterRepository;
    private final UserItemRepository userItemRepository;
    private final UserDetailRepository userDetailRepository;
    private final CharacterRepository characterRepository;
    private final UserStepRepository userStepRepository;
    private final UserInfoRedisService userInfoRedisService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;

    /**
     * 사용자의 캐릭터 정보 가져오기(api)
     */
    //TODO 업그레이드 수치 적용 리팩토링
    @Transactional(readOnly = true)
    public UserCharacterInfoResponse getUserCharacterInfo(int userId) {
        UserDetail userDetail = userDetailRepository.findByJoinFetchByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        int upgrade = userDetail.getSelectUserCharacter().getUpgrade();
        int grade = userDetail.getSelectUserCharacter().getCharacter().getGrade();

        HashMap<String, Integer> upgradeValue = getUpgradeStatus(grade, upgrade);

        return UserCharacterInfoResponse.from(userDetail, upgradeValue.get("health"), upgradeValue.get("power"), upgradeValue.get("defense"));
    }

    /**
     * 사용자의 캐릭터 경험치 정보 가져오기(api)
     */
    @Transactional(readOnly = true)
    public UserCharacterExpInfoResponse getUserCharacterExpInfo(Integer userId) {
        UserDetail userDetail = userDetailRepository.findByJoinFetchByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        UserItem userItem = userItemRepository.findByUserItemWithUserAndItemFetch(userId,"Exp Item")
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_ITEM_NOT_FOUND_EXP));

        return UserCharacterExpInfoResponse.from(userDetail,userItem);
    }

    /**
     * 사용자가 가지고 있는 스탯 포인트로 능력치 올리기
     */
    @Transactional(readOnly = false)
    public UserCharacterStatResponse addStatPoint(int userId, String value) {
        UserDetail userDetail = userDetailRepository.findUserCharacterByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        UserCharacter userCharacterInfo = userDetail.getSelectUserCharacter();

        if (userCharacterInfo.getStatPoint() < REDUCE_STAT_POINT) {
            throw new GlobalBaseException(GlobalErrorCode.INSUFFICIENT_STAT_POINT);
        }

        userCharacterInfo.useStatPoint(REDUCE_STAT_POINT);
        if (value.equals("health")) {
            userCharacterInfo.raiseHealth(ADD_STAT);
        } else if (value.equals("defense")) {
            userCharacterInfo.raiseDefense(ADD_STAT);
        } else if (value.equals("power")) {
            userCharacterInfo.raisePower(ADD_STAT);
        }

        HashMap<String, Integer> upgradeValue = getUpgradeStatus(userCharacterInfo.getCharacter().getGrade(),userCharacterInfo.getUpgrade());
        int upgradeHealth = upgradeValue.get("health");
        int upgradePower = upgradeValue.get("power");
        int upgradeDefense = upgradeValue.get("defense");

        return UserCharacterStatResponse.from(userCharacterInfo, upgradeHealth, upgradePower, upgradeDefense);
    }

    /**
     * 사용자의 캐릭터 변경 메서드 + redis 도 변경
     */
    @Transactional(readOnly = false)
    public ChangeCharacterIdResponse changeUserCharacter(int userId, ChangeUserCharacterIdRequest changeUserCharacterIdRequest) {
        UserDetail userDetail = userDetailRepository.findByUserUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));

        UserCharacter userCharacter = userCharacterRepository.findByUserUserIdAndAndCharacterCharacterId(userId, changeUserCharacterIdRequest.getSelectCharacterId())
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_CHARACTER_NOT_FOUND));

        userDetail.changeUserCharacter(userCharacter);

        userInfoRedisService.updateCharacterId(userId, changeUserCharacterIdRequest.getSelectCharacterId());
//        return ChangeCharacterIdResponse.from(userDetail);
        return ChangeCharacterIdResponse.from(userDetail.getSelectUserCharacter());
    }

    /**
     * 스탯 초기화 버튼 클릭
     */
    //TODO line 161~174 리팩토링 필요(추가되는 스탯만 빼면 되는데 시간상 지저분하게 함.)
    @Transactional(readOnly = false)
    public ResetStatResponse resetInitStatus(int userId) {
        UserDetail userDetail = userDetailRepository.findUserAndUserCharacterByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));

        if (userDetail.getInitStatus() == 1) {
            throw new GlobalBaseException(GlobalErrorCode.STAT_INIT_LIMIT_EXCEEDED);
        }

        UserCharacter userCharacterInfo = userDetail.getSelectUserCharacter();
        System.out.println("-------------------------------------------------");
        Character character = userCharacterInfo.getCharacter();

        int resetStatPoint = userCharacterInfo.getStatPoint();
        //캐릭터의 기본값, 업그레이드 된 수치 빼야함.

        HashMap<String, Integer> upgradeValue = getUpgradeStatus(character.getGrade(),userCharacterInfo.getUpgrade());

        System.out.println("userCharacterInfo.getHealth() : " + userCharacterInfo.getHealth());
        System.out.println("userCharacterInfo.getPower() : " + userCharacterInfo.getPower() );
        System.out.println("userCharacterInfo.getDefense() : " + userCharacterInfo.getDefense());

        int upgradeHealth = upgradeValue.get("health");
        int upgradePower = upgradeValue.get("power");
        int upgradeDefense = upgradeValue.get("defense");

        resetStatPoint += userCharacterInfo.getHealth() - character.getFixHealth() - upgradeHealth;
        resetStatPoint += userCharacterInfo.getPower() - character.getFixPower() - upgradePower;
        resetStatPoint += userCharacterInfo.getDefense() - character.getFixDefense() - upgradeDefense;

        userCharacterInfo.resetStat(resetStatPoint, character.getFixHealth() + upgradeHealth, character.getFixPower() + upgradePower, character.getFixDefense() + upgradeDefense);
        userDetail.changeInitStatus();
        userDetailRepository.save(userDetail);

        return ResetStatResponse.from(userCharacterInfo, upgradeHealth, upgradePower, upgradeDefense);
    }

    /**
     * 유저의 일일 걸음수 가져오기
     */
    public UserStepResponse checkUserStep(int userId, int frontStep) {
//        UserStep userStep = userStepRepository.findUserStepByUserUserId(userId)
//                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));

        int redisUserDailyStep = realtimeStepRankingRedisService.getUserDailyStep(userId);

        // 휴대폰이 재부팅 될 때를 가정
//        if (frontStep < userStep.getDailyStep()) {
//            return UserStepResponse.from(userStep.getDailyStep(), true);
//        } else {
//            return UserStepResponse.from(frontStep, false);
//        }
        if (frontStep < redisUserDailyStep) {
            return UserStepResponse.from(redisUserDailyStep, true);
        } else {
            return UserStepResponse.from(frontStep, false);
        }
    }

    /**
     * 유저의 걸음수 저장
     */
    @Transactional(readOnly = false)
    public void saveUserStep(int userId, int frontStep) {
        UserStep userStep = userStepRepository.findUserStepByUserUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));

        userStep.updateDailyStep(frontStep);
    }

    /*
        사용자의 캐릭터 정보 가져오기
     */
    @Transactional(readOnly = true)
    public UserCharacterListInfoResponse getUserCharacterInfoList(int userId) {
        List<UserCharacterListInfo> userCharacterListInfos = new ArrayList<>();

        // 해당 사용자가 장착하고 있는 캐릭터 찾기
        UserDetail userDetail = userDetailRepository.findByUserUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        // 해당 사용자가 가지고 있는 캐릭터 정보 가져오기
        List<UserCharacter> haveCharacterList = userCharacterRepository.findByUserUserId(userId);
        for (UserCharacter userCharacter : haveCharacterList) {
            userCharacterListInfos.add(UserCharacterListInfo.userCharacterFrom(userCharacter));
        }

        // 해당 사용자가 가지고 있지 않은 캐릭터 정보 가져오기
        List<Character> notHaveUserCharacterList = characterRepository.findNotHaveUserCharacterList(userId);
        for (Character userCharacter : notHaveUserCharacterList) {
            userCharacterListInfos.add(UserCharacterListInfo.characterFrom(userCharacter));
        }

        return UserCharacterListInfoResponse.from(userCharacterListInfos, userDetail.getSelectUserCharacter().getCharacter().getCharacterId());
    }

    /**
     * 사용자의 캐릭터 정보 가져오기(내부 메서드)
     */
    public UserCharacter getUserCharacter(int userId) {
        UserDetail userDetail = userDetailRepository.findByJoinFetchByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        return userDetail.getSelectUserCharacter();
    }

    /**
     * 업그레이드된 수치를 반환해주는 메소드
     * @param gradeValue 등급 수치
     * @param upgradeValue 업그레이드 수치
     * @return HashMap 형태로 체력/공격력/방어력이 얼마나 올랐는지
     */
    private HashMap<String, Integer> getUpgradeStatus(int gradeValue, int upgradeValue){
        HashMap<String, Integer> response = new HashMap<>();
        response.put("health", 0);
        response.put("power", 0);
        response.put("defense", 0);

        switch (gradeValue){
            case 1:
                response.replace("health", UpgradeValue.GRADE_1_UPGRADE_HEALTH_STAT*upgradeValue);
                response.replace("power", UpgradeValue.GRADE_1_UPGRADE_POWER_STAT*upgradeValue);
                response.replace("defense", UpgradeValue.GRADE_1_UPGRADE_DEFENSE_STAT*upgradeValue);
                break;
            case 2:
                response.replace("health", UpgradeValue.GRADE_2_UPGRADE_HEALTH_STAT*upgradeValue);
                response.replace("power", UpgradeValue.GRADE_2_UPGRADE_POWER_STAT*upgradeValue);
                response.replace("defense", UpgradeValue.GRADE_2_UPGRADE_DEFENSE_STAT*upgradeValue);
                break;
            case 3:
                response.replace("health", UpgradeValue.GRADE_3_UPGRADE_HEALTH_STAT*upgradeValue);
                response.replace("power", UpgradeValue.GRADE_3_UPGRADE_POWER_STAT*upgradeValue);
                response.replace("defense", UpgradeValue.GRADE_3_UPGRADE_DEFENSE_STAT*upgradeValue);
                break;

        }
        return response;
    }

    public MainResponse getUserCharacterId(int userId) {
        UserDetail userDetail = userDetailRepository.findUserAndUserCharacterByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));

        return MainResponse.from(userDetail.getSelectUserCharacter().getCharacter().getCharacterId(), userDetail.getUser().getNickname());
    }
}
