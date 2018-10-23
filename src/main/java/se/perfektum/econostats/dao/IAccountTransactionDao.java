package se.perfektum.econostats.dao;

import se.perfektum.econostats.domain.AccountTransaction;

import java.util.List;

/**
 * Created by stenman on 2017-01-08.
 */
public interface IAccountTransactionDao {
    public List<AccountTransaction> loadAccountTransactions();
}
