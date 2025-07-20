package com.finsight.app.controller;

import com.finsight.app.dto.LoginRequest;
import com.finsight.app.model.User;
import com.finsight.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

  private final UserService userService;

  @Autowired
  UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<com.finsight.app.dto.User> registerUser(@RequestBody User user) {
    com.finsight.app.dto.User createdUser = userService.register(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

    @GetMapping("/me")
    public ResponseEntity<com.finsight.app.dto.User> getCurrentUser(HttpServletRequest request) throws Exception {
        String userId = (String) request.getSession().getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.finsight.app.dto.User user = userService.getCurrentUserDto(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            String userId = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            request.getSession().setAttribute("userId", userId);
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }
}
