package com.example.github.action.demo.controller;


import com.example.github.action.demo.model.User;
import com.example.github.action.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/phone/{phone}")
    public User getUserByPhone(@PathVariable String phone) {
        return userService.getUserByPhone(phone);
    }
}