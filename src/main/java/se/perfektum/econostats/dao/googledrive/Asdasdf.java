package se.perfektum.econostats.dao.googledrive;

import com.google.api.services.drive.DriveScopes;
import se.perfektum.econostats.dao.AccountTransactionDao;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class Asdasdf {
    public static void main(String[] args) throws IOException, GeneralSecurityException {

        AccountTransactionDao dao = new GoogleDriveDao();

        dao.getFile("666");

    }
}
