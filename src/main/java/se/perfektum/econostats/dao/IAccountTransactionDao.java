package se.perfektum.econostats.dao;

import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeConfig;

import java.util.List;

/**
 * Dao that handles loads and writes to DB
 */
public interface IAccountTransactionDao {
    public List<AccountTransaction> loadAccountTransactions();

    public List<PayeeConfig> loadPayeeConfig();
}
