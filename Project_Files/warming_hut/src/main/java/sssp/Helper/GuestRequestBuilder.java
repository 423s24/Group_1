package sssp.Helper;

import java.util.HashMap;
import java.util.Random;

public class GuestRequestBuilder extends HttpRequestBuilder {

    private final String ENDPOINT = "clients/HRDC/tables/Guests";

    public boolean postNewGuest(String firstName, String lastName, String password){
        String path = String.format("%s/%s",ENDPOINT,String.format(String.format("%s-%s", lastName, firstName)));

        HashMap<String, String> newGuestData = new HashMap<>();
        newGuestData.put("First Name", firstName);
        newGuestData.put("Last Name", lastName);
        newGuestData.put("Password", password);

        return postRequest(path, newGuestData);
    }
}
