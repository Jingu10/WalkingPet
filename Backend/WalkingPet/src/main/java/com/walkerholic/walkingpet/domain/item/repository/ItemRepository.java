package com.walkerholic.walkingpet.domain.item.repository;

import com.walkerholic.walkingpet.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select i from Item i where i.name = :itemName")
    Optional<Item> findItemByItemName(String itemName);
}
