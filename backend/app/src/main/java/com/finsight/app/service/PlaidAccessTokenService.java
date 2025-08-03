package com.finsight.app.service;

import com.finsight.app.model.Account;
import com.finsight.app.model.PlaidAccessToken;
import com.finsight.app.repository.AccountCursorRepository;
import com.finsight.app.repository.AccountRepository;
import com.finsight.app.repository.PlaidAccessTokenRepository;
import jakarta.transaction.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaidAccessTokenService {
  private static final Logger logger = LoggerFactory.getLogger(PlaidAccessTokenService.class);

  private final PlaidAccessTokenRepository plaidAccessTokenRepository;
  private final UserService userService;
  private final PlaidService plaidService;
  private final AccountService accountService;
  private final AccountRepository accountRepository;
  private final AccountCursorRepository accountCursorRepository;

  @Autowired
  public PlaidAccessTokenService(
      PlaidAccessTokenRepository plaidAccessTokenRepository,
      UserService userService,
      PlaidService plaidService,
      AccountService accountService,
      AccountRepository accountRepository, AccountCursorRepository accountCursorRepository) {
    this.plaidAccessTokenRepository = plaidAccessTokenRepository;
    this.userService = userService;
    this.plaidService = plaidService;
    this.accountService = accountService;
    this.accountRepository = accountRepository;
      this.accountCursorRepository = accountCursorRepository;
  }

  @Transactional
  public void createPlaidItem(String userId, String accessToken, String itemId, String institutionName) {
    if (userId == null || accessToken == null || itemId == null) {
      throw new IllegalArgumentException("Required parameters cannot be null");
    }
    userService.getCurrentUser(userId);
    PlaidAccessToken plaidAccessToken = new PlaidAccessToken(itemId, accessToken, institutionName, userId);

    plaidAccessTokenRepository.save(plaidAccessToken);

    try {

        List<Account> accounts = plaidService.getAccountsFromAccessToken(accessToken, userId);

      for (com.finsight.app.model.Account account : accounts) {
        boolean alreadyExists =
            accountRepository.existsByUserIdAndLastFourAndInstitutionId(
                userId, account.getLastFour(), account.getInstitutionId());

          if (!alreadyExists) {
              accountService.createAccount(account, userId);
              logger.info("Created new account: {}", account.getAccountId());
          } else {
              logger.info("Account already exists: {}", account.getAccountId());
          }
      }

//        List<Account> allAccounts = accounts.stream().toList();
//
//        for (Account account : allAccounts) {
//            AccountCursor accountCursor = accountCursorRepository
//                .findByUserIdAndInstitutionIdAndLastFour(userId, account.getInstitutionId(),account.getLastFour())
//                .orElse(new AccountCursor(null, userId, itemId, account.getAccountId(), account.getLastFour(), account.getInstitutionId(), null));
//
//            plaidService.getTransactionsFromAccessToken(accessToken, userId, account.getAccountId(), accountCursor);
//        }
    } catch (Exception e) {
      logger.error("Failed to fetch accounts for itemId: {}", itemId, e);
    }
  }
}
