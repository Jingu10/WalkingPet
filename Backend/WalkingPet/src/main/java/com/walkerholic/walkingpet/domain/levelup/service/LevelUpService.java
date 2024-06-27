package com.walkerholic.walkingpet.domain.levelup.service;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.levelup.dto.CharacterLevelExperienceInfo;
import com.walkerholic.walkingpet.domain.levelup.dto.LevelUpReward;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpInfo;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import com.walkerholic.walkingpet.domain.levelup.function.LevelUpFunction;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LevelUpService {
    private final UserCharacterRepository userCharacterRepository;
    private final LevelUpFunction levelUpFunction;
    private final UserItemRepository userItemRepository;
    private final UserDetailRepository userDetailRepository;
    private UserCharacterFunction userCharacterFunction;

    /**
     * 레벨업 여부 & 레벨업 정보 반환 서비스(현재 레벨, 다음 레벨, 보상 정보), 레벨업 된 정보를 저장한다
     * @param userDetail 유저 디테일
     * @param getExperience 획득한 경험치 양
     * @return 레벨업 여부 & 레벨업 정보 => 현재 레벨, 다음 레벨, 보상 정보
     */
    public LevelUpResponse getLevelUpResponse(UserDetail userDetail, int getExperience){
        //일단 얻은 경험치 양만큼 캐릭터에 경험치 증가
        UserCharacter selectUserCharacter = userDetail.getSelectUserCharacter();
        selectUserCharacter.addExperience(getExperience);
        userCharacterRepository.save(selectUserCharacter);

        int userId= userDetail.getUser().getUserId();
        
        if(levelUpFunction.checkLevelUp(selectUserCharacter.getLevel(), selectUserCharacter.getExperience(), getExperience)){
            return LevelUpResponse.builder()
                    .isLevelUp(true)
                    .levelUpInfo(getLevelUpInfo(userId,selectUserCharacter, getExperience))
                    .build();
        }
        else{
            return LevelUpResponse.builder()
                    .isLevelUp(false)
                    .levelUpInfo(null)
                    .build();
        }
    }

    /**
     * 레벨업 여부 & 레벨업 정보 반환 서비스(현재 레벨, 다음 레벨, 보상 정보), 레벨업 된 정보를 저장한다
     * @param userCharacter 유저 캐릭터
     * @param getExperience 획득한 경험치 양
     * @return 레벨업 여부 & 레벨업 정보 => 현재 레벨, 다음 레벨, 보상 정보
     */
    public LevelUpResponse getLevelUpResponseByObject(int userId, UserCharacter userCharacter, int getExperience){

        if(levelUpFunction.checkLevelUp(userCharacter.getLevel(), userCharacter.getExperience(), getExperience)){
            return LevelUpResponse.builder()
                .isLevelUp(true)
                .levelUpInfo(getLevelUpInfo(userId,userCharacter, getExperience))
                .build();
        }
        else{
            return LevelUpResponse.builder()
                .isLevelUp(false)
                .levelUpInfo(getLevelUpInfo(userId,userCharacter, getExperience))
                .build();
        }
    }

    /**
     * 레벨업 결과 정보 가져오기, 정보를 가져오면서 캐릭터/아이템 정보를 업데이트한다.
     * @param selectUserCharacter 유저 캐릭터
     * @param getExperience 획득한 경험치
     * @return 레벨업 정보
     */
    public LevelUpInfo getLevelUpInfo(int userId, UserCharacter selectUserCharacter, int getExperience){

        int nowLevel = selectUserCharacter.getLevel();
        CharacterLevelExperienceInfo updateCharacterInfo = levelUpFunction.getNextLevel(selectUserCharacter.getLevel(), selectUserCharacter.getExperience(), getExperience);
        LevelUpReward levelUpReward = levelUpFunction.getLevelUpReward(selectUserCharacter.getLevel(), updateCharacterInfo.getNowLevel());

        //1. 캐릭터 정보 업데이트하기
        selectUserCharacter.updateLevelUp(updateCharacterInfo.getNowLevel(), updateCharacterInfo.getNowExperience(), levelUpReward.getStatPoint());
        userCharacterRepository.save(selectUserCharacter);

        //2. 레벨업 보상 업데이트하기
        for(String itemName : levelUpReward.getItemReward().keySet()){
            UserItem userItem = getUserItem(userId,itemName);
            userItem.addItemQuantity(levelUpReward.getItemReward().get(itemName));
            userItemRepository.save(userItem);
        }
        return LevelUpInfo.builder()
                .nowLevel(nowLevel)
                .nextLevel(updateCharacterInfo.getNowLevel())
                .levelUpReward(levelUpReward)
                .build();
    }

    /**
     * 현재 캐릭터가 레벨업이 될 상태라면 레벨업 시키고 정보 반환하기. 근데 어짜피 레벨 +1만 될 거임.
     * @param userId 유저 아읻
     * @return 레벨업 정보 반환
     */
    public LevelUpInfo updateLevelUpStatus(int userId){
        UserDetail userDetail = getUserDetail(userId);
        UserCharacter userCharacter = getUserCharacter(userId, userDetail.getSelectUserCharacter().getUserCharacterId());

        int nowLevel = userCharacter.getLevel();
        int nextLevel = nowLevel + 1;
        int remainExperience = userCharacter.getExperience() - userCharacterFunction.getMaxExperience(nowLevel);

        //1. 레벨업 했으면 유저에 대한 정보 업데이트
        userCharacter.updateLevelUp(nextLevel,remainExperience, (nextLevel-nowLevel)*5);
        userCharacterRepository.save(userCharacter);

        //레벨업 했으면 해당 보상 저장
        LevelUpReward levelUpReward = levelUpFunction.getLevelUpReward(nowLevel, nextLevel);
        for(String itemName : levelUpReward.getItemReward().keySet()){
            UserItem userItem = getUserItem(userId,itemName);
            userItem.addItemQuantity(levelUpReward.getItemReward().get(itemName));
            userItemRepository.save(userItem);
        }

        return LevelUpInfo.builder()
                .nowLevel(nowLevel)
                .nextLevel(nextLevel)
                .levelUpReward(levelUpReward)
                .build();
    }

    /**
     * userId와 selectUserCharacterId로 user가 장착한 UserCharacter 찾기
     * @param userId 유저 아이디
     * @param selectUserCharacterId 유저 캐릭터 아이디
     * @return UserCharacter
     */
    //[1] UserId로 UserCharacter 찾기
    private UserCharacter getUserCharacter(int userId, int selectUserCharacterId){
        return userCharacterRepository.findUserCharacterByUserIdAndUserCharacterId(userId, selectUserCharacterId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_CHARACTER_NOT_FOUND));
    }

    /**
     * 유저 아이디와 아이템 이름으로 유저아이템 찾기
     * @param userId 유저 아이디
     * @param itemName 아이템 이름
     * @return UserItem
     */
    private UserItem getUserItem(int userId, String itemName){
        return userItemRepository.findByUserItemWithUserAndItemFetch(userId, itemName)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_ITEM_NOT_FOUND_BOX));
    }

    /**
     * userId로 UserDetail 가져오기
     * @param userId 유저 아이디
     * @return UserDetail
     */
    private UserDetail getUserDetail(int userId){
        return userDetailRepository.findByUserUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));
    }
}
