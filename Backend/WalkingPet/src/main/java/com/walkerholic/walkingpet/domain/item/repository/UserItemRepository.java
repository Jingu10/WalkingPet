package com.walkerholic.walkingpet.domain.item.repository;

import com.walkerholic.walkingpet.domain.item.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    @Query("SELECT ui FROM UserItem ui JOIN FETCH ui.user WHERE ui.user.userId = :userId")
    Optional<List<UserItem>> findByUserIdWithUserFetch(Integer userId);

    @Query("select ui from UserItem ui join fetch ui.item where ui.user.userId = :userId and ui.item.name = :itemName")
    Optional<UserItem> findByUserItemWithUserAndItemFetch(Integer userId, String itemName);

    Optional<UserItem> findByUserAndItem(Users users, Item item);
}
