package com.walkerholic.walkingpet.domain.character.service;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * 추후 수정할 최적화할 예정
 */
public class TestLevelUpService {
    private final UserCharacterRepository userCharacterRepository;
    private final UserItemRepository userItemRepository;
    private final UserCharacterFunction userCharacterFunction;

    //경험치가 maxExperience를 초과한 상태에서 넘어올거임
    public String levelUp(UserCharacter userCharacter){
        int nowLevel = userCharacter.getLevel();
        int userId = userCharacter.getUser().getUserId();
        int gapExp = userCharacter.getExperience() - userCharacterFunction.getMaxExperience(nowLevel);

        userCharacter.levelUp(gapExp);
        userCharacterRepository.save(userCharacter);

        if(nowLevel + 1 % 5 == 0){
            System.out.println("일반 상자 획득~");
            System.out.println("고급 상자 추가 획득~");
            List<UserItem> userItemList = userItemRepository.findByUserIdWithUserFetch(userId)
                    .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_ITEM_NOT_FOUND_BOX));

            UserItem userNormalBox = null;
            UserItem userLuxuryBox = null;
            for(UserItem userItem : userItemList){
                if(userItem.getItem().getName().equals("Normal Box"))
                    userNormalBox = userItem;
                else if(userItem.getItem().getName().equals("Luxury Box"))
                    userLuxuryBox = userItem;
            }

            userNormalBox.addItemQuantity(1);
            userItemRepository.save(userNormalBox);
            userLuxuryBox.addItemQuantity(1);
            userItemRepository.save(userLuxuryBox);
            
        }
        else{
            System.out.println("일반 상자 획득~");
            UserItem userItem = userItemRepository.findByUserItemWithUserAndItemFetch(userId, "Normal Box")
                    .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_ITEM_NOT_FOUND_BOX));
            userItem.addItemQuantity(1);
            userItemRepository.save(userItem);
            System.out.println();
        }

        return "레벨업 성공";
    }
}
