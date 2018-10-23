package se.perfektum.econostats.dao;

import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.util.List;

public class AccountTransactionDao implements IAccountTransactionDao {
    @Override
    public List<AccountTransaction> loadAccountTransactions() {
        // Not yet implemented
        return null;
    }

    @Override
    public List<PayeeFilter> loadPayeeConfig() {
        // Not yet implemented
        return null;
    }
}
