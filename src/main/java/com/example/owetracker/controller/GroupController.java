package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.Group;
import com.example.owetracker.model.User;
import com.example.owetracker.service.ExpenseService;
import com.example.owetracker.service.GroupService;
import com.example.owetracker.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExpenseService expenseService;

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

        return "redirect:/groups";
    }

    @GetMapping("/groups")
    public String listUserGroups(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Group> groups = groupService.findGroupsByUserId(userId);
        //model.addAttribute("groups", groups);
        Map<Group, Boolean> groupsWithCreatorFlag = new HashMap<>();
        for (Group group : groups) {
            boolean isCreator = group.getCreatedBy().equals(userId);
            groupsWithCreatorFlag.put(group, isCreator);
        }

        model.addAttribute("groupsWithCreatorFlag", groupsWithCreatorFlag);

        return "groups/list";
    }

    @GetMapping("/groups/details/{groupId}")
    public String getGroupDetails(@PathVariable Integer groupId, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        Group group = groupService.findById(groupId);
        List<User> members = groupService.findMembersByGroupId(groupId);
        List<Expense> expenses = expenseService.getExpensesByGroupId(groupId); // Correct method name here

        model.addAttribute("group", group);
        model.addAttribute("members", members);
        model.addAttribute("expenses", expenses);
        model.addAttribute("isCreator", group.getCreatedBy().equals(userId));

        return "groups/details";
    }

    @GetMapping("/groups/{groupId}/edit")
    public String editGroup(@PathVariable Integer groupId, Model model) {
        Group group = groupService.findById(groupId);
        List<User> members = groupService.findMembersByGroupId(groupId);
        Integer creatorId = group.getCreatedBy();

        model.addAttribute("group", group);
        model.addAttribute("members", members);
        model.addAttribute("creatorId", creatorId);
        model.addAttribute("allUsers", userService.getAllUsers());

        return "groups/edit";
    }

    @PostMapping("/groups/edit")
    public String updateGroup(
            @RequestParam Integer groupId,
            @RequestParam(value = "title", required = false, defaultValue = "defaultTitle") String title,
            @RequestParam(required = false) List<Integer> usersToKick,
            @RequestParam(required = false) List<Integer> usersToAdd,
            @RequestParam(required = false) String action,
            RedirectAttributes redirectAttributes) {


        try {
            switch (action) {
                case "rename":
                    groupService.updateGroupTitle(groupId, title);
                    redirectAttributes.addFlashAttribute("message", "Group renamed successfully.");
                    break;
                case "kick":
                    if (usersToKick != null && !usersToKick.isEmpty()) {
                        groupService.removeUsersFromGroup(groupId, usersToKick);
                        redirectAttributes.addFlashAttribute("message", "User(s) kicked successfully.");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "No users selected to kick.");
                    }
                    break;
                case "add":
                    if (usersToAdd != null && !usersToAdd.isEmpty()) {
                        groupService.addUsersToGroup(groupId, usersToAdd);
                        redirectAttributes.addFlashAttribute("message", "User(s) added successfully.");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "No users selected to add.");
                    }
                    break;
                case "delete":
                    groupService.deleteGroup(groupId);
                    redirectAttributes.addFlashAttribute("message", "Group deleted successfully.");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid action.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while updating the group.");
        }

        return "redirect:/groups";
    }



    @PostMapping("/groups/{groupId}/leave")
    public String leaveGroup(
            @PathVariable("groupId") Integer groupId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            groupService.removeUserFromGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("message", "You have left the group.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while leaving the group.");
        }

        return "redirect:/groups";
    }

    @PostMapping("/groups/{groupId}/delete")
    public String deleteGroup(
            @PathVariable Integer groupId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Group group = groupService.findById(groupId);
            if (group == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Group not found.");
                return "redirect:/groups";
            }

            if (!group.getCreatedBy().equals((Integer) session.getAttribute("userId"))) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this group.");
            return "redirect:/groups";
            }

            groupService.deleteGroup(groupId);
            redirectAttributes.addFlashAttribute("successMessage", "Group deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the group.");
            e.printStackTrace();
        }

        return "redirect:/groups";
    }


    @PostMapping("/{groupId}/removeUser")
    public String removeUserFromGroup(
            @PathVariable Integer groupId,
            @RequestParam Integer userId,
            RedirectAttributes redirectAttributes) {

        try {
            groupService.removeUserFromGroup(groupId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "User removed successfully.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/groups/" + groupId + "/details";
    }

    @GetMapping("/api/groups/{groupId}/members")
    @ResponseBody
    public List<User> getGroupMembers(@PathVariable Integer groupId) {
        return groupService.findMembersByGroupId(groupId);
    }


}
