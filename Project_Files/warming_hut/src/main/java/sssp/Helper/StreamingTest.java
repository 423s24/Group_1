package sssp.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

import static java.net.http.HttpClient.newHttpClient;

public class StreamingTest {

    private final String client = "HRDC";
    private final String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";
    private final String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";
    private final HttpClient httpClient = newHttpClient();

    public Future<?> ListenForEvents() throws IOException {
        try {

            String urlString = endpoint +"/"+ client + ".json?auth=" + secret;
            var request = HttpRequest.newBuilder(URI.create(urlString)).GET().setHeader("Accept","text/event-stream").build();
            var bodyHandler = HttpResponse.BodyHandlers.fromLineSubscriber(new EventstreamSubscriber());
            return httpClient.sendAsync(request, bodyHandler);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    class EventstreamSubscriber implements Flow.Subscriber<String> {

        private Flow.Subscription subscription;
        private final List<String> bufferedLines = new ArrayList<>();
        //private final Consumer<ServerSentEvent> eventConsumer;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            System.out.println(item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }
}
