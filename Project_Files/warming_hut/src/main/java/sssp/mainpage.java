package sssp;
import sssp.Control.GuestController;
import sssp.Helper.DBConnector;
import sssp.Helper.HttpRequestBuilder;
import sssp.Helper.GuestRequestBuilder;
import sssp.Model.GuestModel;
import sssp.View.MockForm;

import javax.swing.*;
import java.awt.*;


public class mainpage {
    public static void main(String[] args) {

        // Create a new JFrame
        //JFrame frame = new JFrame("My Swing Application");
       
        // Set the frame to full screen
        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //frame.setUndecorated(false); // Add window decorations


        // Create a JPanel to hold the content
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
               
                // Set font properties
                Font font = new Font("Arial", Font.BOLD, 50); // Font name, style (bold), size
                g.setFont(font);
                g.setColor(Color.BLACK); // Set text color
               
                // Draw "Hello, World!" in the center of the panel
                FontMetrics metrics = g.getFontMetrics(font);
                int x = (getWidth() - metrics.stringWidth("Hello, World!")) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g.drawString("Hello, World!", x, y);
            }
        };


        // Add the panel to the frame
        //frame.add(new MockForm());

       
        // Make the JFrame visible
        //frame.setVisible(true);
       
        // Set the default close operation
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MockForm view = new MockForm();
        GuestModel model = new GuestModel();
        GuestController controller = new GuestController(view, model);

        JFrame frame = new JFrame("MockForm");
        frame.setContentPane(view.getMockPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        String client = "HRDC";
        String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

        // Create an instance of ModuleTemplate
        DBConnector test = new DBConnector(client, secret);
    }
}
