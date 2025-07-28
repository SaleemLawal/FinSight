package com.finsight.app.service;

import com.finsight.app.exception.UnauthorizedAccessException;
import com.finsight.app.model.Account;
import com.finsight.app.model.PlaidItem;
import com.finsight.app.repository.PlaidItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaidItemService {
    private static final Logger logger = LoggerFactory.getLogger(PlaidItemService.class);

    private final PlaidItemRepository plaidItemRepository;
    private final UserService userService;
    private final PlaidService plaidService;
    private final AccountService accountService;

    public PlaidItemService(PlaidItemRepository plaidItemRepository, UserService userService, PlaidService plaidService, AccountService accountService) {
        this.plaidItemRepository = plaidItemRepository;
        this.userService = userService;
        this.plaidService = plaidService;
        this.accountService = accountService;
    }

    @Transactional
    public void createPlaidItem(String userId, String accessToken, String itemId){
        if (userId == null || accessToken == null || itemId == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }
        com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
        PlaidItem plaidItem = new PlaidItem(itemId, accessToken, loggedUser);

        plaidItemRepository.save(plaidItem);

        try {
            List<Account> accounts = plaidService.getAccountFromAccessToken(accessToken, loggedUser);

            for (com.finsight.app.model.Account account : accounts) {
                accountService.createAccount(account, userId);
            }
        } catch (Exception e) {
            // Log error but don't fail the whole operation
            logger.error("Failed to fetch accounts for itemId: " + itemId, e);
            // You might want to throw here depending on your error handling strategy
        }
    }
}
