package com.walkerholic.walkingpet.domain.battle.function;

import com.walkerholic.walkingpet.domain.battle.dto.response.RewardItem;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;

@Component
public class RewardFunction {
    static final int BOX_PROBABILITY = 20;
    static final int LUXURY_BOX_PROBABILITY = 30;
    static final int NORMAL_BOX_PROBABILITY = 70;
    static final int WIN_EXP_ITEM_QUANTITY = 2;
    static final int LOSE_EXP_ITEM_QUANTITY = 1;

    public RewardItem getRewardItem(boolean batlleResult){
        Random random = new Random();
        int boxProbability = random.nextInt(100);

        HashMap<String, Integer> battleReward = new HashMap<>();
        battleReward.put("Luxury Box", 0);
        battleReward.put("Normal Box", 0);
        battleReward.put("Exp Item", 0);

        if(batlleResult){
            battleReward.replace("Exp Item", WIN_EXP_ITEM_QUANTITY);

            if(boxProbability <= BOX_PROBABILITY){
                int randomBoxCheck = random.nextInt(100);
                if(randomBoxCheck <= LUXURY_BOX_PROBABILITY){
                    battleReward.replace("Luxury Box", 1);
                }
                else if(randomBoxCheck <= NORMAL_BOX_PROBABILITY){
                    battleReward.replace("Normal Box", 1);
                }
            }
            return RewardItem.builder()
                    .reward(battleReward)
                    .build();
        }
        else{
            battleReward.replace("Exp Item", LOSE_EXP_ITEM_QUANTITY);
            return RewardItem.builder()
                    .reward(battleReward)
                    .build();
        }
    }
}
