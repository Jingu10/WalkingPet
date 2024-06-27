package com.walkerholic.walkingpet.domain.goal.service;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.repository.UserCharacterRepository;
import com.walkerholic.walkingpet.domain.goal.dto.response.UserGoalInfoDTO;
import com.walkerholic.walkingpet.domain.goal.entity.Goal;
import com.walkerholic.walkingpet.domain.goal.repository.GoalRepository;
import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.levelup.function.LevelUpFunction;
import com.walkerholic.walkingpet.domain.levelup.service.LevelUpService;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.walkerholic.walkingpet.domain.goal.dto.response.UserGoalInfoDTO.DAILY_GOAL_COUNT;
import static com.walkerholic.walkingpet.domain.goal.dto.response.UserGoalInfoDTO.WEEKLY_GOAL_COUNT;
import static com.walkerholic.walkingpet.global.error.GlobalErrorCode.*;

/**
 * 보상으로 경험치 아이템을 지급할때 사용하는 GoalService
 * 이 경우에 UserGoalInfo에는 LevelUpResponse가 없어야함
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalItemService {
    private final GoalRepository goalRepository;
    private final UsersRepository usersRepository;
    private final UserStepRepository userStepRepository;
    private final UserItemRepository userItemRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserCharacterRepository userCharacterRepository;
    private final LevelUpFunction levelUpFunction;
    private final LevelUpService levelUpService;

    /**
     * Controller - 유저의 목표 정보를 보여주는 service
     * @param userId 유저의 아이디
     * @return 유저의 목표 정보
     */
    public UserGoalInfoDTO getUserGoalInfo(int userId){
        Users user = getUserByUserId(userId);
        Goal goal = getUserGoalByUser(user);
        int userStep = getUserStepByUser(user);

        return UserGoalInfoDTO.builder()
                .step(userStep)
                .dailyGoal(getDailyGoalStatus(goal.getDailyGoal()))
                .weeklyGoal(getWeeklyGoalStatus(goal.getWeeklyGoal()))
                .build();
    }

    /**
     * 일정 걸음수를 달성했을 때 레벨업 여부를 판별해주는 함수.
     * @param userId 유저의 아이디
     * @param goalStep 달성한 걸음수
     * @return 유저의 목표 달성 여부가 갱신된 정보
     */
    public UserGoalInfoDTO getGoalReward(int userId, int goalStep){
        Users user = getUserByUserId(userId);
        Goal goal = getUserGoalByUser(user);
        int userStep = getUserStepByUser(user);

        return UserGoalInfoDTO.builder()
                .step(userStep)
                .dailyGoal(getDailyGoalStatus(goal.getDailyGoal()))
                .weeklyGoal(getWeeklyGoalStatus(goal.getWeeklyGoal()))
                .build();
    }

    /**
     * int형으로 저장되어 있는 일일 목표값을 달성여부를 판별해주는 boolean형 배열로 변환
     * @param dailyGoal DB에 저장되어있는 일일 목표 값
     * @return 달성여부를 판별해주는 boolean형 배열
     */
    public boolean[] getDailyGoalStatus(int dailyGoal){
        boolean[] dailyGoalArray = new boolean[DAILY_GOAL_COUNT];

        String dailyGoalString = String.format("%05d", Integer.parseInt(Integer.toBinaryString(dailyGoal)));
        System.out.println("dailyGoalString == " + dailyGoalString);

        for(int i = DAILY_GOAL_COUNT-1 ; i >= 0 ; i--){
            try{
                if(dailyGoalString.charAt(i) == '1'){
                    dailyGoalArray[DAILY_GOAL_COUNT-1-i] = true;
                }
                else if(dailyGoalString.charAt(i) == '0'){
                    dailyGoalArray[DAILY_GOAL_COUNT-1-i]= false;
                }
                else{
                    throw new Exception("이진수로 변환하는 중 문제가 발생했습니다.");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        System.out.println(Arrays.toString(dailyGoalArray));

        return dailyGoalArray;
    }

    /**
     * int형으로 저장되어 있는 주간 목표값을 달성여부를 판별해주는 boolean형 배열로 변환
     * @param weeklyGoal DB에 저장되어있는 일일 목표 값
     * @return 달성여부를 판별해주는 boolean형 배열
     */
    public boolean[] getWeeklyGoalStatus(int weeklyGoal){
        boolean[] weeklyGoalArray = new boolean[WEEKLY_GOAL_COUNT];

        String weeklyGoalString = String.format("%07d", Integer.parseInt(Integer.toBinaryString(weeklyGoal)));

        for(int i = WEEKLY_GOAL_COUNT-1; i >=0 ; i--){
            try{
                if(weeklyGoalString.charAt(i) == '1'){
                    weeklyGoalArray[WEEKLY_GOAL_COUNT-1-i] = true;
                }
                else if(weeklyGoalString.charAt(i) == '0'){
                    weeklyGoalArray[WEEKLY_GOAL_COUNT-1-i]= false;
                }
                else{
                    throw new Exception("이진수로 변환하는 중 문제가 발생했습니다.");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return weeklyGoalArray;
    }

    /**
     * 일일목표 상태 업데이트(0/3000/5000/7000/10000보 걷기에 따라~)
     * @param userId 유저 아이디
     * @param goal 목표
     * @param step 걸음 수
     */
    public void updateDailyGoalStatus(int userId, Goal goal, int step){
        UserDetail userDetail = getUserDetailByUserId(userId);
        UserCharacter userCharacter = getUserCharacterByUserDetail(userDetail);

        //int로 저장되어있는 일일 목표 값을 달성여부 boolean형 배열로 변환
        boolean[] dailyGoalStatus = getDailyGoalStatus(goal.getDailyGoal());
        System.out.println("원본 일일목표" +dailyGoalStatus.length);
        HashMap<String, Integer> reward = new HashMap<>();
        try {
            if(step == 0){
                System.out.println("0보");
                dailyGoalStatus[0] = true;
                reward.put("ExpItem", 1);
            }
            else if(step == 3000){
                System.out.println("3000보");
                dailyGoalStatus[1] = true;
                reward.put("ExpItem", 2);
            }
            else if(step == 5000){
                System.out.println("5000보");
                dailyGoalStatus[2] = true;
                //주간목표 달성 기준
                updateWeeklyGoalStatus(goal);
                reward.put("ExpItem", 2);
            }
            else if(step == 7000){
                System.out.println("7000보");
                dailyGoalStatus[3] = true;
                reward.put("ExpItem", 2);
                reward.put("Normal Box", 1);
            }
            else if(step == 10000){
                System.out.println("10000보");
                dailyGoalStatus[4] = true;
                reward.put("ExpItem", 2);
                reward.put("Luxury Box", 1);
            }
            else{
                throw new Exception("보상에 해당하지 않는 걸음수가 입력값으로 들어왔습니다.");
            }

            System.out.println("일일목표 적용 결과 : " + Arrays.toString(dailyGoalStatus));
            
            //일일 목표 업데이트
            int resultDailyGoalValue = booleanArrayStatusToInt(dailyGoalStatus);
            goal.setDailyGoal(resultDailyGoalValue);
            goalRepository.save(goal);

            //보상으로 받은 정보 저장하기
            getGoalRewardBox(userId, reward);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    //주간목표 업데이트
    public void updateWeeklyGoalStatus(Goal goal){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        //그러면 일단 weekly 값을 가져오고 이 친구를 boolean형 배열로 바꾸고 그리고 해당 인덱스-1에 해당하는 부분을 true로 바꿔주고
        //바꾼애를 다시 정수로 바꾸고, 그 친구를 다시 save해야겠네.

        int weeklyGoalValue = goal.getWeeklyGoal();
        boolean[] weeklyGoalStatus = getWeeklyGoalStatus(weeklyGoalValue);
        weeklyGoalStatus[dayOfWeek.getValue()-1] = true;

        int resultWeeklyGoalValue = booleanArrayStatusToInt(weeklyGoalStatus);

        //어짜피 데일리 목표를 업데이트 하면서 주간 목표를 업데이트 함.
        //골에는 데일리와 주간 동시에 가지고 있으므로 어짜피 데일리 마지막에서 save해줄거라면 굳이 여기서 안해줘도 됨
        goal.setWeeklyGoal(resultWeeklyGoalValue);
    }

    /**
     * 보상으로 받은 박스 아이템 저장
     * @param userId 유저 아이디
     * @param reward 보상으로 받은 아이템 정보
     */
    public void getGoalRewardBox(int userId, HashMap<String, Integer> reward){
        for(String itemName : reward.keySet()){
            UserItem userItem = getUserItemByUserIdAndItemName(userId,itemName);
            userItem.addItemQuantity(reward.get(itemName));
            userItemRepository.save(userItem);
        }
    }

    /**
     * true false로 이루어진 배열을 2진수로 바꿔서 정수로 표현해주는 함수
     * @param status true false로 표현한 일일/주간 목표 현황
     * @return 정수값
     */
    public int booleanArrayStatusToInt(boolean[] status){
        int result = 0;
        System.out.println(Arrays.toString(status));
        for(int i = 0; i < status.length ; i++){
            if(status[i]){
                result += (int)Math.pow(2,i);
            }
        }

        return result;
    }

    public Users getUserByUserId(int userId){
        return usersRepository.findById(userId)
                .orElseThrow(()-> new GlobalBaseException(USER_NOT_FOUND));
    }

    public Goal getUserGoalByUser(Users user){
        return goalRepository.findGoalByUser(user)
                .orElseThrow(()->new GlobalBaseException(USER_GOAL_NOT_FOUND));
    }

    public UserItem getUserItemByUserIdAndItemName(int userId, String itemName){
        return userItemRepository.findByUserItemWithUserAndItemFetch(userId, itemName)
                .orElseThrow(()-> new GlobalBaseException(USER_ITEM_NOT_EXIST));
    }

    public UserDetail getUserDetailByUserId(int userId){
        return userDetailRepository.findByUserUserId(userId)
                .orElseThrow(()-> new GlobalBaseException(USER_DETAIL_NOT_FOUND));
    }

    public UserCharacter getUserCharacterByUserDetail(UserDetail userDetail){
        return userDetail.getSelectUserCharacter();
    }

    public int getUserStepByUser(Users user){

        return userStepRepository.findUserStepByUser(user)
                .orElseThrow(()->new GlobalBaseException(USER_STEP_NOT_FOUND)).getDailyStep();
    }
}
