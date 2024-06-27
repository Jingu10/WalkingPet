package com.walkerholic.walkingpet.domain.character.function;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UserCharacterFunction {
    static final int INCREASE_MAX_EXP = 5;
    public static int getMaxExperience(int level){
        if(level == 1){
            return 10;
        }
        else{
            return level*(level-1)*INCREASE_MAX_EXP/2 + 10;
        }
    }
}
