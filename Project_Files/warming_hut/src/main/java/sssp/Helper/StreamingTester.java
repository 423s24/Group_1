package sssp.Helper;

import java.io.IOException;
import java.util.concurrent.Future;

public class StreamingTester {
    public static void main(String[] args) {
        StreamingTest streamingTest = new StreamingTest();
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
