package sssp.Helper;

import java.util.HashMap;
import java.util.Random;

public class GuestRequestBuilder extends HttpRequestBuilder {

    private final String ENDPOINT = "guests";

    public void postNewGuest(){
        Random rand = new Random();
        int randomID = Math.abs(rand.nextInt());
        String path = String.format("%s/%s",ENDPOINT,String.format("newGuest%d", randomID));

        HashMap<String, String> newGuestData = new HashMap<>();
        newGuestData.put("UserID", String.valueOf(randomID));
        newGuestData.put("Username", "New User");

        postRequest(path, newGuestData);
    }
}
