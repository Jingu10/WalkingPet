package com.walkerholic.walkingpet.domain.character.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChangeUserCharacterIdRequest {
    private int selectCharacterId;
}
