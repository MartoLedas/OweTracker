package com.example.owetracker.repository;

import com.example.owetracker.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    boolean existsByUserIdAndFriendId(Integer userId, Integer friendId);
    void deleteByUserIdAndFriendId(Integer userId, Integer friendId);

    @Query("SELECT f.friendId FROM Friend f WHERE f.userId = :userId")
    List<Integer> findFriendIdsByUserId(@Param("userId") Integer userId);

}
