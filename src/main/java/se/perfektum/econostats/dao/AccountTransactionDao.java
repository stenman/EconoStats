package se.perfektum.econostats.dao;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Dao that handles loads and writes to data storage
 * NOTE: Using Google Drive as an implementation breaks this interface. Maybe it should be renamed/repurposed?
 */
public interface AccountTransactionDao {
    String createFolder(String name) throws IOException, GeneralSecurityException;

    String createFile(String content, List<String> parents) throws IOException, GeneralSecurityException;

    String createFile(List<String> parents) throws IOException, GeneralSecurityException;

    void updateFile(String fileId) throws IOException, GeneralSecurityException;

    List<String> searchForFile(String name, String mimeType) throws IOException, GeneralSecurityException;

    String getFile(String fileId) throws IOException, GeneralSecurityException;
}
