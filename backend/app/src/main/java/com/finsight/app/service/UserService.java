package com.finsight.app.service;

import com.finsight.app.exception.UserNotAuthenticatedException;
import com.finsight.app.model.User;
import com.finsight.app.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public com.finsight.app.dto.User createUser(User user){
        user.setCreatedAt(LocalDateTime.now());
        User createdUser = userRepository.save(user);
        return transformToDto(createdUser);
    }

    public User getLoggedInUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotAuthenticatedException("User not found with id: " + id));
    }

    public com.finsight.app.dto.User getUserDto(Long id){
        User user = getLoggedInUser(id);
        return transformToDto(user);
    }

    public List<com.finsight.app.dto.User> getAllUsersDto(){
        return userRepository.findAll().stream()
            .map(this::transformToDto)
            .collect(Collectors.toList());
    }

    private com.finsight.app.dto.User transformToDto(User user){
        return new com.finsight.app.dto.User(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}
