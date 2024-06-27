package com.walkerholic.walkingpet.domain.levelup.function;

import com.walkerholic.walkingpet.domain.character.entity.UserCharacter;
import com.walkerholic.walkingpet.domain.character.function.UserCharacterFunction;
import com.walkerholic.walkingpet.domain.levelup.dto.CharacterLevelExperienceInfo;
import com.walkerholic.walkingpet.domain.levelup.dto.LevelUpReward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class LevelUpFunction {
    static final int LEVEL_UP_GET_STAT_POINT = 5;
    private final UserCharacterFunction userCharacterFunction;

    /**
     * 레벨업을 할 수 있는지 체크하는 함수
     * @param nowLevel 현재레벨
     * @param nowExperience 현재 경험치
     * @param getExperience 여러 요소를 통해 얻은 경험치 양
     * @return 레벨업을 할 수 있다면 true, 할 수 없다면 false
     */
    public boolean checkLevelUp(int nowLevel, int nowExperience, int getExperience){
        int requiredExperience = userCharacterFunction.getMaxExperience(nowLevel);

        //지금 경험치 + 얻은 경험치 양이 레벨업에 필요한 경험치보다 크다면 레벨업 가능
        if(nowExperience + getExperience >= requiredExperience)
            return true;
        else return false;
    }

    /**
     * 레벨업을 했을 때 레벨 몇까지 업하고 남는 경험치는 몇인지 알려주는 함수
     * @param nowLevel 현재레벨
     * @param nowExperience 현재 경험치
     * @param getExperience 여러 요소를 통해 얻은 경험치 양
     * @return 레벨업 이후의 레벨과 경험치
     */
    public CharacterLevelExperienceInfo getNextLevel(int nowLevel, int nowExperience, int getExperience){
        //경험치를 얻었을 때 가지고 있는 경험치 양
        int resultExperience = nowExperience + getExperience;
        //레벨업이 되기 위해 필요한 경험치 양
        int requiredLevelUpExperience = userCharacterFunction.getMaxExperience(nowLevel);

        while(resultExperience >= requiredLevelUpExperience){
            //1. 레벨업
            nowLevel++;
            //2. 레벨업에 필요한 경험치 양 만큼 현재 경험치에서 깎기
            resultExperience -= requiredLevelUpExperience;
            //3. 다음 레벨업에 필요한 경험치 양 갱신하기
            requiredLevelUpExperience = userCharacterFunction.getMaxExperience(nowLevel);
        }

        return CharacterLevelExperienceInfo.builder()
                .nowLevel(nowLevel)
                .nowExperience(resultExperience)
                .build();
    }


    /**
     * 레벨업이 되었을 때 얻게되는 보상의 정보
     * @param nowLevel 현재레벨
     * @param nextLevel 다음레벨
     * @return 레벨업으로 받게 되는 스탯포인트, 얻게되는 상자와 수량
     */
    public LevelUpReward getLevelUpReward(int nowLevel, int nextLevel){
        HashMap<String, Integer> itemReward = new HashMap<>();
        itemReward.put("Luxury Box", 0);
        itemReward.put("Normal Box", 0);

        for(int i = nowLevel+1; i <= nextLevel ; i++){
            //5레벨 배수마다 고급상자 1개 지급
            if(i % 5 == 0){
                Integer nowRewardQuantity = itemReward.get("Luxury Box");
                itemReward.replace("Luxury Box", nowRewardQuantity+1);
            }
            //아니면 일반상자 1개 지급
            else{
                Integer nowRewardQuantity = itemReward.get("Normal Box");
                itemReward.replace("Normal Box", nowRewardQuantity+1);
            }
        }

        return LevelUpReward.builder()
                .statPoint((nextLevel - nowLevel)*LEVEL_UP_GET_STAT_POINT)
                .itemReward(itemReward)
                .build();
    }

    /**
     * 현재 유저캐릭터의 상태가 레벨업을 할 수 있는 상태인지 체크해주는 함수
     * @param nowLevel 현재 레벨
     * @param nowExperience 현재 경험치
     * @return 레벨업이 가능하다면 true, 못하면 false
     */
    public boolean checkLevelUpStatus(int nowLevel, int nowExperience){
        if(nowExperience >= userCharacterFunction.getMaxExperience(nowLevel))
            return true;
        else return false;
    }
}
