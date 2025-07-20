package com.finsight.app.controller;

import com.finsight.app.dto.LoginRequest;
import com.finsight.app.model.User;
import com.finsight.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
  public ResponseEntity<com.finsight.app.dto.User> registerUser(@Valid @RequestBody User user) {
    com.finsight.app.dto.User createdUser = userService.register(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping("/me")
  public ResponseEntity<com.finsight.app.dto.User> getCurrentUser(HttpServletRequest request)
      throws Exception {
    String userId = (String) request.getSession().getAttribute("userId");
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    com.finsight.app.dto.User user = userService.getCurrentUserDto(userId);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
    try {
      String userId = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
      request.getSession().setAttribute("userId", userId);

      Map<String, Object> response = new HashMap<>();
      response.put("timestamp", LocalDateTime.now());
      response.put("status", HttpStatus.OK.value());
      response.put("message", "Login successful");
      response.put("userId", userId);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
      errorResponse.put("error", "Unauthorized");
      errorResponse.put("message", "Login failed: " + e.getMessage());

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
    request.getSession().invalidate();

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.OK.value());
    response.put("message", "Logged out successfully");

    return ResponseEntity.ok(response);
  }
}
