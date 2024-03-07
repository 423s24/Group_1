package sssp.Helper;

public class DatabaseEventListener implements HttpStreamingManager.IServerEventListener {
    private String eventName;
    private Runnable callback;

    public DatabaseEventListener(String eventName, Runnable callback){
        this.eventName = eventName;
        this.callback = callback;
    }


    @Override
    public void ServerEventCalled(String EventName) {
        if(EventName.equals(this.eventName))
        {
            callback.run();
        }
    }
}
