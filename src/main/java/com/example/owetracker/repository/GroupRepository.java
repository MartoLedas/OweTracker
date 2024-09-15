package com.example.owetracker.repository;

import com.example.owetracker.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group>findByTitle(String title);
    List<Group> findByTitleContaining(String title);



}
