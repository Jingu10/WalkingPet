package com.walkerholic.walkingpet.domain.battle.function;

import com.walkerholic.walkingpet.domain.battle.dto.function.CharacterInfo;
import com.walkerholic.walkingpet.domain.battle.dto.response.BattleProgressInfo;
import com.walkerholic.walkingpet.domain.battle.dto.response.BattleResultInfo;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BattleFunction {
    //데미지 배율
    static final int DAMAGE_SCALE = 3;
    //데미지 최소/최대 값 조정 상수
    static final int DAMAGE_GAP = 2;
    static final int WIN_RATING = 10;
    static final int LOSE_RATING = -5;
    static private boolean battleResult = false;

    private final RewardFunction rewardfunction;

    //전투과정 정보 얻는 함수 -> 선공 / 후공

    /**
     * 전투과정 정보를 얻는 함수
     * @param userCharacterInfo 유저의 캐릭터 정보(공,방,체)
     * @param enemyCharacterInfo 적의 캐릭터 정보(공,방,체)
     * @return 유저/적 이 공격하는 데미지, 유저/적 의 체력 현황, 유저/적 이 받는 데미지의 비율
     */
    public BattleProgressInfo getBattleProgress(CharacterInfo userCharacterInfo, CharacterInfo enemyCharacterInfo){
        List<Integer> userAttackDamageList = new ArrayList<>();
        List<Integer> enemyAttackDamageList = new ArrayList<>();
        List<Integer> userHealthList = new ArrayList<>();
        List<Integer> enemyHealthList = new ArrayList<>();
        List<Double> userLoseDamage = new ArrayList<>();
        List<Double> enemyLoseDamage = new ArrayList<>();

        int userHealth = userCharacterInfo.getHealth();
        int userPower = userCharacterInfo.getPower();
        int userDefense = userCharacterInfo.getDefense();
        int userAccumulateDamage = 0;

        int enemyHealth = enemyCharacterInfo.getHealth();
        int enemyPower = enemyCharacterInfo.getPower();
        int enemyDefense = enemyCharacterInfo.getDefense();
        int enemyAccumulateDamage = 0;

        boolean strikeFirst = true;

        System.out.printf("UserPower : %d, EnemyPower : %d", userCharacterInfo.getPower(), enemyCharacterInfo.getPower());

        while(userHealth != 0 && enemyHealth != 0){
            if(strikeFirst){
                //나의 공격
                int attackDamage = getAttackDamage(userPower, enemyDefense);
                enemyHealth -= attackDamage;
                enemyAccumulateDamage += attackDamage;
                //받는 데미지가 음수가 되면 체력을 0으로 만들어준다.
                if(enemyHealth <= 0){
                    enemyHealth = 0;
                    enemyAccumulateDamage = enemyCharacterInfo.getHealth();
                    battleResult = true;
                }

                userAttackDamageList.add(attackDamage);
                enemyAttackDamageList.add(-1);

                userHealthList.add(userHealth);
                userLoseDamage.add(getDamagePercentage(userCharacterInfo.getHealth(),userAccumulateDamage));
                enemyHealthList.add(enemyHealth);
                enemyLoseDamage.add(getDamagePercentage(enemyCharacterInfo.getHealth(),enemyAccumulateDamage));
            }
            else{
                //상대방의 공격
                int attackDamage = getAttackDamage(enemyPower, userDefense);
                userHealth -= attackDamage;
                userAccumulateDamage += attackDamage;
                //받는 데미지가 음수가 되면 체력을 0으로 만들어준다.
                if(userHealth <= 0){
                    userHealth = 0;
                    userAccumulateDamage = userCharacterInfo.getHealth();
                    battleResult = false;
                }

                enemyAttackDamageList.add(attackDamage);
                userAttackDamageList.add(-1);

                System.out.println();
                System.out.println("적이 때린 데미지: " + attackDamage);
                System.out.println("유저 누적 데미지: " + userAccumulateDamage);

                userHealthList.add(userHealth);
                userLoseDamage.add(getDamagePercentage(userCharacterInfo.getHealth(),userAccumulateDamage));
                enemyHealthList.add(enemyHealth);
                enemyLoseDamage.add(getDamagePercentage(enemyCharacterInfo.getHealth(),enemyAccumulateDamage));
            }
            strikeFirst = !strikeFirst;
        }

        System.out.println("유저가 공격하는 데미지 : " + userAttackDamageList.toString());
        System.out.println("적이 공격하는 데미지 : " + enemyAttackDamageList.toString());

        return BattleProgressInfo.builder()
                .userAttackDamage(userAttackDamageList)
                .enemyAttackDamage(enemyAttackDamageList)
                .userHealth(userHealthList)
                .enemyHealth(enemyHealthList)
                .userLoseDamage(userLoseDamage)
                .enemyLoseDamage(enemyLoseDamage)
                .build();
    }

    /**
     * 상대에게 최종적으로 가하는 공격 데미지 구하는 함수
     * @param power 공격하는 유저의 power값
     * @param defense 방어하는 유저의 power값
     * @return 최종적으로 방어하는 유저에게 들어가는 공격 데미지 값
     */
    public int getAttackDamage(int power, int defense){
        int minDamage = power*DAMAGE_SCALE - DAMAGE_GAP;
        int maxDamage = power*DAMAGE_SCALE + DAMAGE_GAP;

        int originAttackDamage = (int)(Math.random()*(maxDamage-minDamage+1)) + minDamage;
        System.out.println("originAttackDamage = " + originAttackDamage);

        int resultAttackDamage = 100*originAttackDamage/(200+defense);
        System.out.println("result Attack damage = " + resultAttackDamage);

        return resultAttackDamage;
    }

    /**
     * 배틀 결과에 따라 얻게되는 보상을 얻는 메소드 / 보상으로 아이템을 주지 않는 메소드
     * @return 승/패에 따라 다른 보상
     */
    public BattleResultInfo getBattleResult(UserDetail userDetail){
        if(battleResult){
            return BattleResultInfo.builder()
                    .battleResult(battleResult)
                    .rewardRating(WIN_RATING)
                    .resultRating(userDetail.getBattleRating() + WIN_RATING)
                    .rewardItem(rewardfunction.getRewardItem(battleResult))
                    .build();
        }
        else{
            return BattleResultInfo.builder()
                    .battleResult(battleResult)
                    .rewardRating(LOSE_RATING)
                    .resultRating(userDetail.getBattleRating() + LOSE_RATING)
                    .rewardItem(rewardfunction.getRewardItem(battleResult))
                    .build();
        }
    }

    /**
     * 누적 데미지가 현재 체력의 몇퍼센트인지를 얻는 함수
     * @param maxHealth 최대 체력
     * @param accumulateDamage 누적 데미지
     * @return 누적데미지의 퍼센트 비율
     */
    public double getDamagePercentage(int maxHealth, int accumulateDamage){
        double result = (double)accumulateDamage/maxHealth;

        return Math.round(result * 100.0) / 100.0;
    }
}
