package com.finsight.app.service;

import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.PlaidItem;
import com.finsight.app.repository.PlaidItemRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlaidItemService {
    private final PlaidItemRepository plaidItemRepository;
    private final UserService userService;


    public PlaidItemService(PlaidItemRepository plaidItemRepository, UserService userService) {
        this.plaidItemRepository = plaidItemRepository;
        this.userService = userService;
    }

    public void createPlaidItem(String userId, String accessToken, String itemId){
        if (userId == null || accessToken == null || itemId == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }
        com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);

        if (!Objects.equals(loggedUser.getId(), userId)) {
            throw new UnauthorizedAccessException("Unauthorized user");
        }

        plaidItemRepository.save(new PlaidItem(itemId, accessToken, loggedUser));
    }
}
