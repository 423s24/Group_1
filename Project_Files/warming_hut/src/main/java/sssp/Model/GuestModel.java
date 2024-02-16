package sssp.Model;

import sssp.Helper.GuestRequestBuilder;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class GuestModel {

    public enum Events {
        GuestAdded("GuestAdded");
        private final String eventString;
        Events(String eventString) {
            this.eventString = eventString;
        }

        @Override
        public String toString(){
            return eventString;
        }
    }

    private GuestRequestBuilder guestRequest;
    private SwingPropertyChangeSupport eventHandler;

    public GuestModel(){
        eventHandler = new SwingPropertyChangeSupport(this);
        guestRequest = new GuestRequestBuilder();
    }

    public void addEventListener(PropertyChangeListener listener){
        eventHandler.addPropertyChangeListener(listener);
    }

    public void addNewGuest(String firstName, String lastName, String password){
        boolean result = guestRequest.postNewGuest(firstName, lastName, password);
        onGuestAdded(result);
    }

    private void onGuestAdded(boolean result){
        eventHandler.firePropertyChange(Events.GuestAdded.toString(), false, result);
    }
}
