package com.finsight.app.controller;

import com.finsight.app.model.UserModel;
import com.finsight.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel user){
        UserModel createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserModel>> getUser() {
        List<UserModel> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }
}
