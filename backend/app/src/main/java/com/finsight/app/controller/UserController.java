package com.finsight.app.controller;

import com.finsight.app.model.User;
import com.finsight.app.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;

  @Autowired
  UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/")
  public ResponseEntity<com.finsight.app.dto.User> createUser(@RequestBody User user) {
    com.finsight.app.dto.User createdUser = userService.createUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping("/")
  public ResponseEntity<List<com.finsight.app.dto.User>> getUser() {
    List<com.finsight.app.dto.User> users = userService.getAllUsersDto();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }
}
