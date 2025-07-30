package com.finsight.app.service;

import com.finsight.app.model.Account;
import com.finsight.app.model.PlaidAccessToken;
import com.finsight.app.repository.PlaidAccessTokenRepository;
import com.finsight.app.util.TransactionSyncResult;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
public class PlaidService {

  private static final Logger logger = LoggerFactory.getLogger(PlaidService.class);

  private final PlaidApi plaidApi;
  private final PlaidAccessTokenRepository plaidAccessTokenRepository;

  public PlaidService(PlaidApi plaidApi, PlaidAccessTokenRepository plaidAccessTokenRepository) {
    this.plaidApi = plaidApi;
    this.plaidAccessTokenRepository = plaidAccessTokenRepository;
  }

  public Response<LinkTokenCreateResponse> createLinkToken(String userId) throws IOException {
    LinkTokenCreateRequestUser user = new LinkTokenCreateRequestUser();
    user.setClientUserId(userId);

    LinkTokenCreateRequest request = new LinkTokenCreateRequest();
    request.setUser(user);
    request.setClientName("Finsight App");
    request.setProducts(List.of(Products.TRANSACTIONS));
    request.setLanguage("en");
    request.setCountryCodes(List.of(CountryCode.US));

    return plaidApi.linkTokenCreate(request).execute();
  }

  public LinkTokenGetResponse getPublicToken(String linkToken) throws IOException {
    try {
      logger.info("Getting details for public token");

      LinkTokenGetRequest request = new LinkTokenGetRequest().linkToken(linkToken);
      Response<LinkTokenGetResponse> response = plaidApi.linkTokenGet(request).execute();

      if (response.isSuccessful()) {
        assert response.body() != null;
        logger.info("Successfully retrieved link token details");
        return response.body();
      } else {
        String errorMsg = "Failed to get link token details. HTTP Code: " + response.code();
        if (response.errorBody() != null) {
          errorMsg += ", Error: " + response.errorBody().string();
        }
        logger.error(errorMsg);
        throw new RuntimeException(errorMsg);
      }
    } catch (IOException e) {
      logger.error("IO error getting link token details: ", e);
      throw e;
    } catch (Exception e) {
      logger.error("Unexpected error getting link token details: ", e);
      throw new RuntimeException("Unexpected error: " + e.getMessage());
    }
  }

  public Map<String, String> exchangePublicTokenForAccessToken(String publicToken)
      throws IOException {
    try {
      ItemPublicTokenExchangeRequest request =
          new ItemPublicTokenExchangeRequest().publicToken(publicToken);
      Response<ItemPublicTokenExchangeResponse> response =
          plaidApi.itemPublicTokenExchange(request).execute();
      assert response.body() != null;

      Map<String, String> res = new HashMap<>();
      res.put("accessToken", response.body().getAccessToken());
      res.put("itemId", response.body().getItemId());
      return res;
    } catch (IOException e) {
      logger.error("IO error getting link token details: ", e);
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<Account> getAccountsFromAccessToken(String accessToken, String userId) {
    try {
      AccountsBalanceGetRequest request = new AccountsBalanceGetRequest().accessToken(accessToken);
      Response<AccountsGetResponse> response = plaidApi.accountsBalanceGet(request).execute();

      if (!response.isSuccessful()) {
        throw new RuntimeException("Failed to get accounts from Plaid");
      }

      assert response.body() != null;
      List<AccountBase> plaidAccounts = response.body().getAccounts();
      String institutionId = response.body().getItem().getInstitutionId();
      String institutionName = response.body().getItem().getInstitutionName();

      return plaidAccounts.stream()
          .map(account -> transformToAccount(institutionId, institutionName, account, userId))
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public TransactionSyncResult getTransactionsFromAccessToken(String accessToken, String userId)
      throws IOException {
    PlaidAccessToken plaidAccessToken = plaidAccessTokenRepository.findByAccessToken(accessToken);
    String cursor = plaidAccessToken.getCursor();
    List<Transaction> added = new ArrayList<Transaction>();
    List<Transaction> modified = new ArrayList<Transaction>();
    List<RemovedTransaction> removed = new ArrayList<RemovedTransaction>();
    boolean hasMore = true;

    TransactionsSyncRequestOptions options =
        new TransactionsSyncRequestOptions().includePersonalFinanceCategory(true);

    while (hasMore) {
      TransactionsSyncRequest request =
          new TransactionsSyncRequest().accessToken(accessToken).cursor(cursor).options(options);

      TransactionsSyncResponse response = plaidApi.transactionsSync(request).execute().body();

      assert response != null;
      added.addAll(response.getAdded());
      modified.addAll(response.getModified());
      removed.addAll(response.getRemoved());

      hasMore = response.getHasMore();
      cursor = response.getNextCursor();
    }

    return new TransactionSyncResult(added, modified, removed, plaidAccessToken);
  }

  private Account transformToAccount(
      String institutionId, String institutionName, AccountBase plaidAccount, String user) {
    return new Account(
        plaidAccount.getAccountId(),
        plaidAccount.getName(),
        mapPlaidAccountType(plaidAccount.getType()),
        institutionName,
        institutionId,
        plaidAccount.getMask(),
        plaidAccount.getBalances().getAvailable() == null
            ? plaidAccount.getBalances().getCurrent()
            : plaidAccount.getBalances().getAvailable(),
        user);
  }

  private com.finsight.app.util.AccountType mapPlaidAccountType(
      com.plaid.client.model.AccountType plaidType) {
    return switch (plaidType) {
      case CREDIT -> com.finsight.app.util.AccountType.CREDIT;
      case LOAN -> com.finsight.app.util.AccountType.LOAN;
      case INVESTMENT -> com.finsight.app.util.AccountType.INVESTMENT;
      default -> com.finsight.app.util.AccountType.DEPOSITORY;
    };
  }
}
