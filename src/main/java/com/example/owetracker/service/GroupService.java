package com.example.owetracker.service;


import com.example.owetracker.model.Group;
import com.example.owetracker.model.GroupMembership;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.GroupMembershipRepository;
import com.example.owetracker.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public void addUsersToGroup(Group group, List<User> users) {
        for (User user : users) {
            GroupMembership membership = new GroupMembership(group, user);
            groupMembershipRepository.save(membership);
        }
    }

    public List<Group> findGroupsByUserId(Integer userId) {
        return groupRepository.findGroupsByUserId(userId);
    }

    public List<Group> searchGroupByTitle(String partialTitle){
        return groupRepository.findByTitleContaining(partialTitle);
    }

    public Group findById(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public List<User> findMembersByGroupId(Integer groupId) {
        return groupMembershipRepository.findUsersByGroupId(groupId);
    }

    public void updateGroupTitle(Integer groupId, String title) {
        Group group = findById(groupId);
        group.setTitle(title);
        groupRepository.save(group);
    }

    public void removeUsersFromGroup(Integer groupId, List<Integer> userIds) {
        groupMembershipRepository.deleteByGroupIdAndUserIdIn(groupId, userIds);
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        groupMembershipRepository.deleteByGroupId(groupId);

        groupRepository.deleteById(groupId);
    }

//    public void removeUserFromGroup(Integer groupId, Integer userId) {
//        if (!groupRepository.existsById(groupId)) {
//            throw new EntityNotFoundException("Group not found with id: " + groupId);
//        }
//
//        groupMembershipRepository.deleteByGroupIdAndUserId(groupId, userId);
//
//    }

    public void removeUserFromGroup(Integer groupId, Integer userId) {
        GroupMembership membership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId);

        if (membership != null) {
            groupMembershipRepository.delete(membership);
        } else {
            throw new EntityNotFoundException("User is not part of this group.");
        }
    }




}
