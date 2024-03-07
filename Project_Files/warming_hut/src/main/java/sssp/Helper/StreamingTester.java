package sssp.Helper;

import com.google.gson.Gson;

public class StreamingTester {

    /*
    private static String client = "HRDC";
    private static String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";
    private static String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";
    */
    public static void main(String[] args) {
        /*
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
        */
        String testJSON = "{\n" +
                "  \"Roster_1\": {\n" +
                "    \"Date\": \"1/27/2024\",\n" +
                "    \"Guests\": {\n" +
                "      \"Guest_100\": {\n" +
                "        \"BunkAssigned\": \"<Bunk number>\",\n" +
                "        \"Laundry\": true,\n" +
                "        \"Services\": \"<Service numbers>\"\n" +
                "      },\n" +
                "      \"Guest_200\": {\n" +
                "        \"BunkAssigned\": \"<Bunk number>\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"StaffNotes\": \"Today was a good day!\"\n" +
                "  }\n" +
                "}";
        System.out.println(testJSON);
        Gson gson = new Gson();

        RosterData data = gson.fromJson(testJSON, RosterData.class);
        System.out.println(data.getStaffNotes());
    }
}
