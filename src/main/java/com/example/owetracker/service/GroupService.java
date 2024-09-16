package com.example.owetracker.service;


import com.example.owetracker.model.Group;
import com.example.owetracker.model.GroupMembership;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.GroupMembershipRepository;
import com.example.owetracker.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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




}
