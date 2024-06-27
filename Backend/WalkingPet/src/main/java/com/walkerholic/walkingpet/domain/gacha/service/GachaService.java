package com.walkerholic.walkingpet.domain.gacha.service;

import com.walkerholic.walkingpet.domain.character.entity.Character;
import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.repository.CharacterRepository;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.gacha.dto.response.GachaCountResponse;
import com.walkerholic.walkingpet.domain.gacha.dto.response.GachaResultResponse;
import com.walkerholic.walkingpet.domain.item.entity.Item;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.ItemRepository;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.domain.util.UpgradeValue;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.walkerholic.walkingpet.domain.character.service.UserCharacterService.*;
import static com.walkerholic.walkingpet.global.error.GlobalErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GachaService {
    private static final int NORMAL_BOX_ID = 1;
    private static final int LUXURY_BOX_ID = 2;
    private static final int UPGRADE_INCREMENT = 1;
    private static final int GRADE2_APPEAR_PERCENTAGE = 90;
    private static final int GRADE1_APPEAR_PERCENTAGE = 70;
    private static final int GRADE1 = 1;
    private static final int GRADE2 = 2;
    private static final int GRADE3 = 3;

    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;
    private final CharacterRepository characterRepository;
    private final UsersRepository usersRepository;
    private final UserCharacterRepository userCharacterRepository;

    @Transactional(readOnly = false)
    public GachaResultResponse getGachaResult(String boxType, int userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));

        int grade = decideGrade(user,boxType);
        boolean duplication = false;

        Character character = characterRepository.findRandomByGrade(grade)
                .orElseThrow(() -> new GlobalBaseException(CHARACTER_NOT_FOUND));

        Optional<UserCharacter> userCharacter = userCharacterRepository.findByUserAndCharacter(user,character);

        if(userCharacter.isPresent()){
            userCharacter.get().setUpgrade(userCharacter.get().getUpgrade() + UPGRADE_INCREMENT);
            //업그레이드 된 만큼 능력치 증가
            HashMap<String, Integer> upgradeStat = getUpgradeStatus(grade);
            userCharacter.get().raiseHealth(upgradeStat.get("health"));
            userCharacter.get().raisePower(upgradeStat.get("power"));
            userCharacter.get().raiseDefense(upgradeStat.get("defense"));
            userCharacterRepository.save(userCharacter.get());
            duplication = true;
        }
        else{
            userCharacterRepository.save(new UserCharacter(character,user));
        }

        return  GachaResultResponse.from(character,duplication);
    }

    @Transactional(readOnly = true)
    public GachaCountResponse getGachaCount(int userId){

        List<UserItem> userItems = userItemRepository.findByUserIdWithUserFetch(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_ITEM_NOT_FOUND_BOX));

        int normalBoxCount = countItems(userItems, NORMAL_BOX_ID);
        int luxuryBoxCount = countItems(userItems, LUXURY_BOX_ID);

        return GachaCountResponse.from(normalBoxCount,luxuryBoxCount);
    }

    // 상자 타입에 따라 확률 계산을 해준 뒤, 몇 성의 캐릭터를 뽑아야할지 Return 하는 함수
    public int decideGrade(Users user,String boxType){

        int itemId = boxType.equals("normal") ? 1 : 2;
        decreaseItemQuantity(user, itemId, 1);
        int randomNumber = new Random(System.currentTimeMillis()).nextInt(100) + 1;

        return boxType.equals("normal") ?
                (randomNumber <= GRADE1_APPEAR_PERCENTAGE ? GRADE1 : GRADE2) :
                (randomNumber <= GRADE2_APPEAR_PERCENTAGE ? GRADE2 : GRADE3);
    }

    // 주어진 사용자 항목 목록에서 특정 아이템 ID에 해당하는 아이템의 수량을 계산합니다.
    public int countItems(List<UserItem> userItems, int itemId) {
        return userItems.stream()
                .filter(item -> item.getItem().getItemId() == itemId)
                .mapToInt(UserItem::getQuantity)
                .sum();
    }

    // 사용자의 아이템 수량을 감소시킵니다.
    public void decreaseItemQuantity(Users user, int itemId, int decreaseAmount) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalBaseException(ITEM_NOT_FOUND));
        UserItem userItem = userItemRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new GlobalBaseException(USER_ITEM_NOT_FOUND_BOX));

        if (userItem.getQuantity() >= decreaseAmount) {
            userItem.setQuantity(userItem.getQuantity() - decreaseAmount);
            userItemRepository.save(userItem);
        } else {
            throw new GlobalBaseException(USER_ITEM_NOT_EXIST);
        }
    }

    /**
     * 등급별 각 능력치에 업그레이드가 얼마나 되는지 반환해주는 함수
     * @param grade 등급
     * @return HashMap 형태로 체력/공격력/방어력이 얼마나 증가하는지
     */
    private HashMap<String, Integer> getUpgradeStatus(int grade){
        HashMap<String, Integer> response = new HashMap<>();
        response.put("health", 0);
        response.put("power", 0);
        response.put("defense", 0);

        switch (grade){
            case 1:
                response.replace("health", UpgradeValue.GRADE_1_UPGRADE_HEALTH_STAT);
                response.replace("power", UpgradeValue.GRADE_1_UPGRADE_POWER_STAT);
                response.replace("defense", UpgradeValue.GRADE_1_UPGRADE_DEFENSE_STAT);
                break;
            case 2:
                response.replace("health", UpgradeValue.GRADE_2_UPGRADE_HEALTH_STAT);
                response.replace("power", UpgradeValue.GRADE_2_UPGRADE_POWER_STAT);
                response.replace("defense", UpgradeValue.GRADE_2_UPGRADE_DEFENSE_STAT);
                break;
            case 3:
                response.replace("health", UpgradeValue.GRADE_3_UPGRADE_HEALTH_STAT);
                response.replace("power", UpgradeValue.GRADE_3_UPGRADE_POWER_STAT);
                response.replace("defense", UpgradeValue.GRADE_3_UPGRADE_DEFENSE_STAT);
                break;

        }//end of swith/case

        return response;
    }
}
