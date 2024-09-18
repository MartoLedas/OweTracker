package com.example.owetracker.service;


import com.example.owetracker.model.Group;
import com.example.owetracker.model.GroupMembership;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.GroupMembershipRepository;
import com.example.owetracker.repository.GroupRepository;
import com.example.owetracker.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

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

    public void updateGroupTitle(Integer groupId, String newTitle) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        group.setTitle(newTitle);

        groupRepository.save(group);
    }

    public void removeUsersFromGroup(Integer groupId, List<Integer> userIds) {
        for (Integer userId : userIds) {
            removeUserFromGroup(groupId, userId);
        }
    }

    public void removeUserFromGroup(Integer groupId, Integer userId) {
        GroupMembership membership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId);

        if (membership != null) {
            groupMembershipRepository.delete(membership);

            long memberCount = groupMembershipRepository.countByGroupId(groupId);

            if (memberCount == 0) {
                deleteGroup(groupId);
            }
        } else {
            throw new EntityNotFoundException("User is not part of this group.");
        }
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        groupMembershipRepository.deleteByGroupId(groupId);

        groupRepository.deleteById(groupId);
    }

//    public void addUsersToGroup(Integer groupId, List<Integer> userIds) {
//        Group group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
//
//        for (Integer userId : userIds) {
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//            if (groupMembershipRepository.findByGroupIdAndUserId(groupId, userId) == null) {
//                GroupMembership membership = new GroupMembership(group, user);
//                groupMembershipRepository.save(membership);
//            }
//        }
//    }

    public void addUsersToGroup(Integer groupId, List<Integer> userIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        for (Integer userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            GroupMembership existingMembership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId);

            if (existingMembership == null) {
                GroupMembership membership = new GroupMembership(group, user);
                groupMembershipRepository.save(membership);
                System.out.println("User with ID " + userId + " added to group.");
            } else {
                System.out.println("User with ID " + userId + " is already part of the group.");
            }
        }
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<User> getGroupMembers(Integer groupId) {
        List<Integer> userIds = groupMembershipRepository.findUserIdsByGroupId(groupId);

        return userRepository.findAllById(userIds);
    }

}
