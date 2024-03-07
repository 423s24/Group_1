package sssp.Helper;

public class DBConnectorV2Singleton {
    private static String client = "HRDC";
    private static String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

    private static DBConnectorV2 db = null;

    private DBConnectorV2Singleton() {
    }

    public static DBConnectorV2 getInstance() {
        if (db == null) {
            db = new DBConnectorV2(client, secret);
        }
        return db;
    }
}
