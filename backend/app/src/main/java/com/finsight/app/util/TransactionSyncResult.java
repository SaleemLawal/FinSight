package com.finsight.app.util;

import com.plaid.client.model.RemovedTransaction;
import com.plaid.client.model.Transaction;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionSyncResult {
  private List<Transaction> added;
  private List<Transaction> modified;
  private List<RemovedTransaction> removed;
  //  private PlaidAccessToken plaidAccessToken;
}
