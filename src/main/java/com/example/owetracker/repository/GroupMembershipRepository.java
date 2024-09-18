package com.example.owetracker.repository;

import com.example.owetracker.model.GroupMembership;
import com.example.owetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Integer> {

    @Query("SELECT gm.user FROM GroupMembership gm WHERE gm.group.id = :groupId")
    List<User> findUsersByGroupId(@Param("groupId") Integer groupId);

    @Query("DELETE FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    void deleteByGroupIdAndUserId(@Param("groupId") Integer groupId, @Param("userId") Integer userId);

    void deleteByGroupIdAndUserIdIn(Integer group_id, Collection<Integer> user_id);

    void deleteByGroupId(Integer groupId);

    GroupMembership findByGroupIdAndUserId(Integer groupId, Integer userId);

    void delete(GroupMembership groupMembership);

    long countByGroupId(Integer groupId);

}
