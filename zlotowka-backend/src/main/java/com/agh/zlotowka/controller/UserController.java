package com.agh.zlotowka.controller;

import com.agh.zlotowka.model.User;
import com.agh.zlotowka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser() {
        return userService.createUser();
    }
}
