package com.example.owetracker.repository;

import com.example.owetracker.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Integer> {
}
