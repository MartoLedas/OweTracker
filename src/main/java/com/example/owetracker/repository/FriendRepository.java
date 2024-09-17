package com.example.owetracker.repository;

import com.example.owetracker.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    boolean existsByUserIdAndFriendId(Integer userId, Integer friendId);
    void deleteByUserIdAndFriendId(Integer userId, Integer friendId);

    @Query("SELECT f.friendId FROM Friend f WHERE f.userId = :userId")
    List<Integer> findFriendIdsByUserId(@Param("userId") Integer userId);

    // Find all friends for a given user where the user is either the userId or the friendId
    List<Friend> findByUserIdOrFriendId(Integer userId, Integer friendId);
    List<Friend> findByUserIdOrFriendIdAndStatus(Integer userId, Integer friendId, String status);

    @Query("SELECT SUM(e.amountOwed) FROM ExpenseUser e WHERE e.user.id = :userId")
    BigDecimal getTotalOwedByUser(@Param("userId") Integer userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.id = :userId")
    BigDecimal getTotalOwedToUser(@Param("userId") Integer userId);


    Optional<Friend> findByUserIdAndFriendId(Integer userId, Integer friendId);

    List<Friend> findByFriendIdAndStatus(Integer friendId, String status);

}
