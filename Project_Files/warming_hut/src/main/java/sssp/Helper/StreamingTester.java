package sssp.Helper;

import java.io.IOException;
import java.util.concurrent.Future;

public class StreamingTester {

    private static String client = "HRDC";
    private static String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";
    private static String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";
    public static void main(String[] args) {

        HttpStreamingManager streamingTest = new HttpStreamingManager(client, secret, endpoint);
        try {
            Future<?> myFuture = streamingTest.ListenForEvents();
            while(!myFuture.isDone()){
                Thread.sleep(100);
                System.out.println("Waiting for Future to complete");
            }
        } catch (IOException ignored){

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
