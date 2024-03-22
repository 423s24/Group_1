package sssp.Helper;

import java.io.IOException;
import java.util.concurrent.Future;

public class HttpStreamingManagerSingleton {
    private static String client = "HRDC";
    private static String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";
    private static String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";

    private static HttpStreamingManager httpStreamingManager = null;

    private HttpStreamingManagerSingleton() {
    }

    public static HttpStreamingManager getInstance() {
        if (httpStreamingManager == null) {
            httpStreamingManager = new HttpStreamingManager(client, secret, endpoint);
        }
        return httpStreamingManager;
    }

    public static void subscribeRunnable(String eventName, Runnable listener)
    {
        getInstance().subscribeRunnable(eventName, listener);
    }

    public static void startListening()
    {
        try {
            getInstance().ListenForEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
