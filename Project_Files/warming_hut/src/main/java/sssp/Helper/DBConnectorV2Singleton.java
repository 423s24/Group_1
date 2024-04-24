package sssp.Helper;

import sssp.Control.SecretManager;

public class DBConnectorV2Singleton {
    private static String client = "HRDC";

    private static DBConnectorV2 db = null;

    private DBConnectorV2Singleton() {
    }

    public static DBConnectorV2 getInstance() {
        if (db == null) {
            String secret = SecretManager.getDBSecret();
            db = new DBConnectorV2(client, secret);
        }
        return db;
    }
}
