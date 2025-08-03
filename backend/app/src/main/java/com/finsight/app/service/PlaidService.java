package com.finsight.app.service;

import com.finsight.app.model.Account;
import com.finsight.app.model.AccountCursor;
import com.finsight.app.model.PlaidAccessToken;
import com.finsight.app.repository.AccountCursorRepository;
import com.finsight.app.repository.PlaidAccessTokenRepository;
import com.finsight.app.util.AccountType;
import com.finsight.app.util.TransactionSyncResult;
import com.plaid.client.model.*;
import com.plaid.client.request.PlaidApi;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

@Service
public class PlaidService {

  private static final Logger logger = LoggerFactory.getLogger(PlaidService.class);

  private final PlaidApi plaidApi;
  private final PlaidAccessTokenRepository plaidAccessTokenRepository;
  private final TransactionService transactionService;
  private final AccountCursorRepository accountCursorRepository;

  @Autowired
  public PlaidService(
      PlaidApi plaidApi,
      PlaidAccessTokenRepository plaidAccessTokenRepository,
      TransactionService transactionService, AccountCursorRepository accountCursorRepository) {
    this.plaidApi = plaidApi;
    this.plaidAccessTokenRepository = plaidAccessTokenRepository;
    this.transactionService = transactionService;
    this.accountCursorRepository = accountCursorRepository;
  }

  public Response<LinkTokenCreateResponse> createLinkToken(String userId) throws IOException {

    LinkTokenCreateRequest request = new LinkTokenCreateRequest()
        .user(new LinkTokenCreateRequestUser().clientUserId(userId))
        .clientName("FinSight App")
        .countryCodes(List.of(CountryCode.US))
        .language("en")
        .products(List.of(Products.TRANSACTIONS));

    return plaidApi.linkTokenCreate(request).execute();
  }

  // update-existing flow
  public Response<LinkTokenCreateResponse> createUpdateLinkToken(String userId, String itemId) throws IOException {
    PlaidAccessToken tok = plaidAccessTokenRepository.findById(itemId)
        .orElseThrow(() -> new RuntimeException("Item not found"));

    LinkTokenCreateRequest req = new LinkTokenCreateRequest()
        .user(new LinkTokenCreateRequestUser().clientUserId(userId))
        .clientName("FinSight App")
        .countryCodes(List.of(CountryCode.US))
        .language("en")
        .accessToken(tok.getAccessToken())
        .update(new LinkTokenCreateRequestUpdate()
            .accountSelectionEnabled(true)
            .reauthorizationEnabled(true));

    return plaidApi.linkTokenCreate(req).execute();
  }

  // public LinkTokenGetResponse getPublicToken(String linkToken) throws
  // IOException {
  // try {
  // logger.info("Getting details for public token");
  //
  // LinkTokenGetRequest request = new LinkTokenGetRequest().linkToken(linkToken);
  // Response<LinkTokenGetResponse> response =
  // plaidApi.linkTokenGet(request).execute();
  //
  // if (response.isSuccessful()) {
  // assert response.body() != null;
  // logger.info("Successfully retrieved link token details");
  // return response.body();
  // } else {
  // String errorMsg = "Failed to get link token details. HTTP Code: " +
  // response.code();
  // if (response.errorBody() != null) {
  // errorMsg += ", Error: " + response.errorBody().string();
  // }
  // logger.error(errorMsg);
  // throw new RuntimeException(errorMsg);
  // }
  // } catch (IOException e) {
  // logger.error("IO error getting link token details: ", e);
  // throw e;
  // } catch (Exception e) {
  // logger.error("Unexpected error getting link token details: ", e);
  // throw new RuntimeException("Unexpected error: " + e.getMessage());
  // }
  // }

  public String getInstitutionName(String accessToken) throws IOException {
    ItemGetRequest request = new ItemGetRequest().accessToken(accessToken);
    Response<ItemGetResponse> response = plaidApi.itemGet(request).execute();
    ItemGetResponse body = response.body();

    if (body == null || body.getItem() == null) {
      throw new RuntimeException("Invalid response from Plaid API");
    }

    return body.getItem().getInstitutionName();
  }

  public Map<String, String> exchangePublicTokenForAccessToken(String publicToken)
      throws IOException {
    try {
      ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest().publicToken(publicToken);
      Response<ItemPublicTokenExchangeResponse> response = plaidApi.itemPublicTokenExchange(request).execute();
      ItemPublicTokenExchangeResponse body = response.body();

      if (body == null || body.getAccessToken() == null || body.getItemId() == null) {
        throw new RuntimeException("Invalid response from Plaid API");
      }

      Map<String, String> res = new HashMap<>();
      res.put("accessToken", body.getAccessToken());
      res.put("itemId", body.getItemId());
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

      AccountsGetResponse body = response.body();

      if (body == null || body.getAccounts() == null || body.getItem() == null) {
        throw new RuntimeException("Invalid response from Plaid API");
      }

      List<AccountBase> plaidAccounts = body.getAccounts();
      String institutionId = body.getItem().getInstitutionId();
      String institutionName = body.getItem().getInstitutionName();

      return plaidAccounts.stream()
          .map(account -> transformToAccount(institutionId, institutionName, account, userId))
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void getTransactionsFromAccessToken(String accessToken, String userId, String accountId,
      AccountCursor accountCursor)
      throws IOException {
    PlaidAccessToken plaidAccessToken = plaidAccessTokenRepository.findByAccessToken(accessToken);
    String cursor = accountCursor.getCursor();
    List<Transaction> added = new ArrayList<>();
    List<Transaction> modified = new ArrayList<>();
    List<RemovedTransaction> removed = new ArrayList<>();
    boolean hasMore = true;

    TransactionsSyncRequestOptions options = new TransactionsSyncRequestOptions().includePersonalFinanceCategory(true);

    if (accountId != null) {
      options.accountId(accountId);
    }

    while (hasMore) {
      TransactionsSyncRequest request = new TransactionsSyncRequest().accessToken(accessToken).cursor(cursor)
          .options(options);

      TransactionsSyncResponse response = plaidApi.transactionsSync(request).execute().body();

      if (response == null) {
        throw new RuntimeException("Invalid response from Plaid API");
      }

      added.addAll(response.getAdded());
      modified.addAll(response.getModified());
      removed.addAll(response.getRemoved());

      hasMore = response.getHasMore();
      cursor = response.getNextCursor();
    }

    accountCursor.setCursor(cursor);
    accountCursorRepository.save(accountCursor);
    plaidAccessTokenRepository.save(plaidAccessToken);

    transactionService.SyncTransactionsToDB(
        new TransactionSyncResult(added, modified, removed), userId);
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

  private AccountType mapPlaidAccountType(
      com.plaid.client.model.AccountType plaidType) {
    return switch (plaidType) {
      case CREDIT -> AccountType.CREDIT;
      case LOAN -> AccountType.LOAN;
      case INVESTMENT -> AccountType.INVESTMENT;
      default -> AccountType.DEPOSITORY;
    };
  }
}
