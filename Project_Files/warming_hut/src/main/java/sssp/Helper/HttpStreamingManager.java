package sssp.Helper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

import static java.net.http.HttpClient.newHttpClient;

public class HttpStreamingManager {

    public interface IServerEventListener {
        public void ServerEventCalled(String EventName);
    }

    List<IServerEventListener> eventListeners = new ArrayList<>();
    private String urlString;
    private final HttpClient httpClient = newHttpClient();

    public void addServerEventListener(IServerEventListener eventListener){
        eventListeners.add(eventListener);
    }

    public HttpStreamingManager(String client, String secret, String endpoint){
        urlString = endpoint +"/"+ client + ".json?auth=" + secret;
    }
    public Future<?> ListenForEvents() throws IOException {
        try {


            var request = HttpRequest.newBuilder(URI.create(urlString)).GET().setHeader("Accept","text/event-stream").build();
            var bodyHandler = HttpResponse.BodyHandlers.fromLineSubscriber(new EventstreamSubscriber(eventListeners));
            return httpClient.sendAsync(request, bodyHandler);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    static class EventstreamSubscriber implements Flow.Subscriber<String>  {

        private Flow.Subscription subscription;
        private final List<IServerEventListener> eventListeners;

        public EventstreamSubscriber(List<IServerEventListener> eventListeners){
            this.eventListeners = eventListeners;
        }


        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            if("event: put".equals(item)){
                raiseEvent("put");
            }
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.println("Error?");
        }

        @Override
        public void onComplete() {
            System.out.println("Completed?");
        }

        private void raiseEvent(String name){
            for(IServerEventListener listener : eventListeners){
                listener.ServerEventCalled(name);
            }
        }
    }
}
