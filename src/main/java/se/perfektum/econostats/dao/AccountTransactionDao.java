package se.perfektum.econostats.dao;

import com.google.api.services.drive.model.File;
import se.perfektum.econostats.domain.AccountTransaction;
import se.perfektum.econostats.domain.PayeeFilter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Dao that handles loads and writes to data storage
 */
public interface AccountTransactionDao {
    String storeAccountTransactions() throws IOException, GeneralSecurityException;

    String storeAccountTransactions(String fileId, File file) throws IOException, GeneralSecurityException;

    List<AccountTransaction> getAccountTransactions() throws IOException, GeneralSecurityException;

//    getFolderStructure
//    createFolder

    List<PayeeFilter> getPayeeFilter();
}
