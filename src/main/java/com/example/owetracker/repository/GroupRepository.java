package com.example.owetracker.repository;

import com.example.owetracker.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group>findByTitle(String title);
    List<Group> findByTitleContaining(String title);

    @Query("SELECT g FROM Group g JOIN GroupMembership gm ON g = gm.group WHERE gm.user.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Integer userId);


}
