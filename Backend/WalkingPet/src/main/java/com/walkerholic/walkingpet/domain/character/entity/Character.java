package com.walkerholic.walkingpet.domain.character.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "characters")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Integer characterId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "fix_health", nullable = false)
    private Integer fixHealth;

    @Column(name = "fix_power", nullable = false)
    private Integer fixPower;

    @Column(name = "fix_defense", nullable = false)
    private Integer fixDefense;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<UserCharacter> userCharacters;

}