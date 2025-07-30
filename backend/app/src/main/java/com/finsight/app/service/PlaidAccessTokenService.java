package com.finsight.app.service;

import com.finsight.app.model.Account;
import com.finsight.app.model.PlaidAccessToken;
import com.finsight.app.repository.AccountRepository;
import com.finsight.app.repository.PlaidItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaidAccessTokenService {
    private static final Logger logger = LoggerFactory.getLogger(PlaidAccessTokenService.class);

    private final PlaidItemRepository plaidItemRepository;
    private final UserService userService;
    private final PlaidService plaidService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public PlaidAccessTokenService(PlaidItemRepository plaidItemRepository, UserService userService, PlaidService plaidService, AccountService accountService, AccountRepository accountRepository) {
        this.plaidItemRepository = plaidItemRepository;
        this.userService = userService;
        this.plaidService = plaidService;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void createPlaidItem(String userId, String accessToken, String itemId){
        if (userId == null || accessToken == null || itemId == null) {
            throw new IllegalArgumentException("Required parameters cannot be null");
        }
        com.finsight.app.model.User loggedUser = userService.getCurrentUser(userId);
        PlaidAccessToken plaidAccessToken = new PlaidAccessToken(itemId, accessToken, loggedUser);

        plaidItemRepository.save(plaidAccessToken);

        try {
            List<Account> accounts = plaidService.getAccountFromAccessToken(accessToken, loggedUser);

            for (com.finsight.app.model.Account account : accounts) {
                boolean alreadyExists = accountRepository.existsByUserAndLastFourAndInstitutionId(loggedUser, account.getLastFour(), account.getInstitutionId());
                if (!alreadyExists){
                    accountService.createAccount(account, userId);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch accounts for itemId: " + itemId, e);
        }
    }
}
