package com.example.owetracker.controller;

import com.example.owetracker.model.Group;
import com.example.owetracker.model.User;
import com.example.owetracker.service.GroupService;
import com.example.owetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping("/groups/create")
    public String showCreateGroupForm(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "create-group"; // thymeleaf
    }

    @PostMapping("/groups/save")
    public String createGroup(
            HttpSession session,
            @RequestParam String title,
            @RequestParam(required = false) List<Integer> users,
            RedirectAttributes redirectAttributes
    ) {
        Integer createdBy = (Integer) session.getAttribute("userId");

        if (users == null || users.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A group must have at least one member besides you.");
            return "redirect:/groups/create";
        }

        users.remove(createdBy); //if only creator is in the group it should count as empty group

        if (users.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A group must have at least one member besides you.");
            return "redirect:/groups/create";
        }

        List<User> selectedUsers = users.stream()
                .map(userId -> userService.findById(userId))
                .collect(Collectors.toList());

        Group group = new Group(title, createdBy);
        Group savedGroup = groupService.createGroup(group);

        User creator = userService.findById(createdBy); //also add creator as member
        selectedUsers.add(creator);

        groupService.addUsersToGroup(savedGroup, selectedUsers);

        return "redirect:/groups/my-groups";
    }

    @GetMapping("/groups/my-groups")
    public String listUserGroups(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Group> userGroups = groupService.findGroupsByUserId(userId);
        model.addAttribute("userGroups", userGroups);

        return "my-groups";
    }



}
