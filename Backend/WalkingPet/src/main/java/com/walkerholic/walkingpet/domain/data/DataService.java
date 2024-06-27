package com.walkerholic.walkingpet.domain.data;

import com.walkerholic.walkingpet.domain.character.entity.Character;
import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.repository.CharacterRepository;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {
    private final UserCharacterRepository userCharacterRepository;
    private final UserDetailRepository userDetailRepository;
    private final CharacterRepository characterRepository;
    private final UsersRepository usersRepository;

    public void setUserCharacter(){
        int[] userIdList = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
                34, 35, 36, 37, 38};

        int[] characterIdList = {1,2,4,11,12,13,14,17,18,22};   //1성의 갯수는 10개

        int selectCharacterIndex = 0;

        for(int i = 0; i < userIdList.length ; i++){
            selectCharacterIndex = selectCharacterIndex % 10;
            int userId = userIdList[i];

            int characterId = characterIdList[selectCharacterIndex];
            Character character = characterRepository.findById(characterId).get();
            Users user = usersRepository.findById(userId).get();

            UserCharacter userCharacter = UserCharacter.builder()
                    .character(character)
                    .user(user)
                    .build();

            userCharacterRepository.save(userCharacter);
            UserDetail userDetail = getUserDetail(userId);
            userDetail.changeUserCharacter(userCharacter);
            userDetailRepository.save(userDetail);

            selectCharacterIndex++;
        }
    }

    public void resetBattleCount(){
        List<UserDetail> userDetailList = userDetailRepository.findAll();

        for(UserDetail userDetail : userDetailList){
            userDetail.resetBattleCount();
            userDetailRepository.save(userDetail);
        }
    }

    public UserDetail getUserDetail(int userId){
        return userDetailRepository.findByUserUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));
    }
}
