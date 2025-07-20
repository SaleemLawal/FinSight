package com.finsight.app.service;

import com.finsight.app.exception.UserNotAuthenticatedException;
import com.finsight.app.model.User;
import com.finsight.app.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public com.finsight.app.dto.User register(User user) {
    user.setCreatedAt(LocalDateTime.now());
    User createdUser = userRepository.save(user);
    return transformToDto(createdUser);
  }

  public com.finsight.app.model.User getCurrentUser(@NotNull String id) throws Exception {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return user.get();
    }
    throw new Exception("User not found");
  }

  public com.finsight.app.dto.User getCurrentUserDto(@NotNull String id) throws Exception {
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      return transformToDto(user.get());
    }
    throw new Exception("User not found");
  }

  public String authenticate(String email, String password) throws Exception {

    Optional<User> userOpt = userRepository.findByEmail(email);

    if (userOpt.isPresent()) {
      User user = userOpt.get();

      // Todo: Hash password
      if (!user.getPassword().equals(password)) {
        throw new UserNotAuthenticatedException("Invalid password");
      }

      return user.getId();
    } else {
      throw new UserNotAuthenticatedException("User not found");
    }
  }

  private com.finsight.app.dto.User transformToDto(@NotNull User user) {
    return new com.finsight.app.dto.User(
        user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
  }
}
