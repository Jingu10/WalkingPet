package com.walkerholic.walkingpet.domain.item.service;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpInfo;
import com.walkerholic.walkingpet.domain.levelup.dto.response.LevelUpResponse;
import com.walkerholic.walkingpet.domain.levelup.service.LevelUpService;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {
    private final int EXP_ITEM_EXP_QUANTITY = 5;

    private final UserItemRepository userItemRepository;
    private final UserDetailRepository userDetailRepository;

    private final LevelUpService levelUpService;

    //리팩토링 필요함! -> 나중에 다양한 경험치 아이템이 나왔을 때를 생각해야함.
    public LevelUpResponse usingExpItem(int userId, int expItemQuantity){
        //1. 일단 사용한 경험치 아이템 갯수 만큼 db 값 변경
        UserItem userItem = getUserItem(userId, "Exp Item");
        try {
            if(userItem.getQuantity() > 0){
                userItem.addItemQuantity(-expItemQuantity);
                userItemRepository.save(userItem);

                UserCharacter userCharacter = getUserDetail(userId).getSelectUserCharacter();
                return levelUpService.getLevelUpResponseByObject(userId, userCharacter, expItemQuantity*EXP_ITEM_EXP_QUANTITY);
            }
            else
                throw new Exception("경험치 아이템이 없습니다.");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * userId로 userDetail과 UserCharacter 가져오는 메소드
     * @param userId 유저아이디
     * @return UserCharacter의 정보를 담고있는 UserDetail
     */
    private UserDetail getUserDetail(int userId){
        return userDetailRepository.findUserDetailByJoinFetchByUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));
    }

    private UserItem getUserItem(int userId, String itemName){
        return userItemRepository.findByUserItemWithUserAndItemFetch(userId, itemName)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.ITEM_NOT_FOUND));
    }

}
