package com.walkerholic.walkingpet.domain.users.service;

import com.walkerholic.walkingpet.domain.character.entity.Character;
import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.repository.CharacterRepository;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.goal.entity.Goal;
import com.walkerholic.walkingpet.domain.goal.repository.GoalRepository;
import com.walkerholic.walkingpet.domain.item.entity.Item;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.ItemRepository;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignInService {
    private final UserStepRepository userStepRepository;
    private final GoalRepository goalRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserItemRepository userItemRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final UsersRepository usersRepository;
    private final ItemRepository itemRepository;
    private final CharacterRepository characterRepository;

    @Transactional(readOnly = false)
    public void signInInItTable(String email){
        Users user = getUsersByEmail(email);
        //1. UserStep 초기화
        initUserStep(user);
        //2. Goal 초기화
        initGoal(user);
        //3. UserItem 초기화
        initUserItem(user);
        //4. UserCharacter 초기화
        UserCharacter selectUserCharacter = initUserCharacter(user);
        //5. UserDetail 초기화
        initUserDetail(user,selectUserCharacter);
    }

    /**
     * UserStep 초기화
     * @param user 유저
     */
    public void initUserStep(Users user){
        userStepRepository.save(UserStep.builder()
                .user(user)
                .build());
    }

    /**
     * Goal 초기화
     * @param user 유저
     */
    public void initGoal(Users user){
        goalRepository.save(Goal.builder()
                .user(user)
                .build());
    }

    /**
     * 유저 아이템 초기화
     * @param user
     */
    public void initUserItem(Users user){
        int countItem = (int)itemRepository.count();
        for(int i = 1; i < countItem+1 ; i++){
            Item item = itemRepository.findById(i)
                    .orElseThrow(()->new GlobalBaseException(GlobalErrorCode.ITEM_NOT_FOUND));

            userItemRepository.save(UserItem.builder()
                    .item(item)
                    .user(user)
                    .quantity(5)
                    .build());
        }
    }

    /**
     * UserCharacter 초기화
     * @param user 유저
     * @return UserCharacter
     */
    public UserCharacter initUserCharacter(Users user){
        Character character = getCharacterById(1);
        UserCharacter userCharacter = UserCharacter.builder()
                .user(user)
                .character(character)
                .build();
        userCharacterRepository.save(userCharacter);

        return userCharacter;
    }

    public void initUserDetail(Users user, UserCharacter selectUserCharacter){
        userDetailRepository.save(UserDetail.builder()
                .user(user)
                .selectUserCharacter(selectUserCharacter)
                .build());
    }

    @Transactional(readOnly = true)
    public Users getUsersByEmail(String email){
        return usersRepository.findByEmail(email)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND_EMAIL));
    }

    @Transactional(readOnly = true)
    public Character getCharacterById(int characterId){
        return characterRepository.findById(characterId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.CHARACTER_NOT_FOUND));
    }
}
