// package sssp.Helper;
// import java.util.Scanner;


// public class demo {
//     public static void main(String[] args) {

//         // Instantiate our secrets
//         String client = "HRDC";
//         String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

//         // Initialize a new DBConnector using our secrets
//         DBConnector demoConnection = new DBConnector(client, secret);

//         // Pull our client database
//         demoConnection.getClientDatabase();
        
//         System.out.println("OUR DATA: \n\n");

//         // Print out a represenation of our database
//         demoConnection.database.printDatabase();

//         // Get user input 
//         System.out.println("\n\nEnter the new first name for testEntry1:");
//         Scanner scanner = new Scanner(System.in);
//         String newFirstName = scanner.nextLine();

//         // Update our local database
//         demoConnection.database.enrollmentForm.get("testEntry1").put("FirstName", newFirstName);

//         // Push our local database to global
//         demoConnection.pushClientDatabase();

//     }
// }



// // In total accessing the database can be done with 4 lines:

// // String client = "HRDC";

// // String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

// // DBConnector demoConnection = new DBConnector(client, secret);

// // demoConnection.getClientDatabase();



// // The database can be shown for manual data checking in one line 

// // demoConnection.database.printDatabase();




// // Updating the database can be done in 2 lines 

// // demoConnection.database.enrollmentForm.get("testEntry1").put("FirstName", newFirstName);

// // demoConnection.pushClientDatabase();



// // In total, all database related communication can be handled with these 7 lines and slight variations to change fields we're accessing. 