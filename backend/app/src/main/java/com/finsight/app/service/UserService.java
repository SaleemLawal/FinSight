package com.finsight.app.service;

import com.finsight.app.model.UserModel;
import com.finsight.app.repository.UserRepo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserService {
    private final UserRepo userRepo;

    UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public UserModel createUser(UserModel user){
        user.setCreatedAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    public List<UserModel> getAllUser() {
        return userRepo.findAll();
    }
}
