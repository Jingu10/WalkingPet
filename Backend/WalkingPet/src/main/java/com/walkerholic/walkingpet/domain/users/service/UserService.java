package com.walkerholic.walkingpet.domain.users.service;

import com.walkerholic.walkingpet.domain.ranking.dto.UserInfoAndAllStepInfo;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.domain.users.dto.response.ChangeNicknameResponse;
import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.UserInfoAndAllStepInfo;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import com.walkerholic.walkingpet.global.redis.service.UserInfoRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final LoginService loginService;
    private final UsersRepository usersRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserStepRepository userStepRepository;
    private final UserInfoRedisService userInfoRedisService;

    @Transactional(readOnly = false)
    public ChangeNicknameResponse modifyNickname(int userId, String nickname){
        if(loginService.checkAvailableNickname(nickname)){
            return ChangeNicknameResponse.builder()
                    .nickname("")
                    .status(false)
                    .build();
        }

        // mysql 닉네임 변경
        Users user = getUsers(userId);
        user.modifyNickname(nickname);

        // redis 닉네임 변경
        userInfoRedisService.updateNickname(userId, nickname);

        return ChangeNicknameResponse.builder()
                .nickname(user.getNickname())
                .status(true)
                .build();
    }

    public void setBattleRating(){
        int battleRating = -300;
        List<Users> usersList = usersRepository.findAll();
        for(int i = 1; i < usersList.size()+1; i++){
            if(i != 31 && i != 32 && i != 33 && i != 34 && i != 35 && i != 36 && i != 37){
                UserDetail userDetail = getUserDetail(i);
                //배틀 레이팅 초기화
//                userDetail.setBattleRating(1000);
                userDetail.updateBattleRating(battleRating);
                userDetailRepository.save(userDetail);
                battleRating += 3;
            }
        }
    }

    private Users getUsers(int userId){
        return usersRepository.findById(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_NOT_FOUND));
    }

    private UserDetail getUserDetail(int userId){
        return userDetailRepository.findUserDetailByJoinFetchByUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(GlobalErrorCode.USER_DETAIL_NOT_FOUND));
    }

    // mysql에 있는 가입상태인 모든 유저 정보 가져오기
    public List<UserRedisDto> getAllUserDetail() {
        List<UserRedisDto> userDetailList = new ArrayList<>();

        List<UserDetail> allByUser = userDetailRepository.findAllByUserStatus(1);
        for (UserDetail userDetailInfo : allByUser) {
            userDetailList.add(UserRedisDto.from(userDetailInfo));
        }

        return userDetailList;
    }

    // 모든 사용자들의 걸음수 정보 가져오기
    @Transactional(readOnly = true)
    public List<UserInfoAndAllStepInfo> getUserAccStepAndInfoList() {
        List<UserDetail> allByUser = userDetailRepository.findAllByUserStatus(1);

        List<UserInfoAndAllStepInfo> userInfoAndAllStepInfos = new ArrayList<>();
        for (UserDetail userDetailInfo : allByUser) {
            UserStep userStepInfo = userStepRepository.findUserStepByUser(userDetailInfo.getUser())
                    .orElseThrow(() -> new GlobalBaseException(GlobalErrorCode.USER_STEP_NOT_FOUND));
            userInfoAndAllStepInfos.add(UserInfoAndAllStepInfo.from(userDetailInfo, userStepInfo));
        }

        return userInfoAndAllStepInfos;
    }
}
