package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

public class MainMenuMockupAlt extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton activeButton;
    private DBConnectorV2 db;

    public MainMenuMockupAlt() {
        setTitle("HRDC Warming Center Manager");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Database Connection
        db = DBConnectorV2Singleton.getInstance();

        // Panel Switch Buttons
        JButton panel1Button = createButton("Check In");
        JButton panel2Button = createButton("Bunk Assignment");
        JButton panel3Button = createButton("Guest Details");

        // Main Panel
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Panels for Tab Switching
        JPanel panel1 = createCheckInPanel();
        JPanel panel2 = createPanel("Bunk Assignment", "Assign Bunk Location");
        JPanel panel3 = createPanel("Guest Details", "Guest Detail Roster");

        // Add panels to the main panel
        mainPanel.add(panel1, "Panel 1");
        mainPanel.add(panel2, "Panel 2");
        mainPanel.add(panel3, "Panel 3");

        // Initialize Active Button
        activeButton = panel1Button;
        activeButton.setEnabled(false); // Darkens active button

        // Panel Switching Action Listeners
        panel1Button.addActionListener(createButtonActionListener(panel1Button, "Panel 1"));
        panel2Button.addActionListener(createButtonActionListener(panel2Button, "Panel 2"));
        panel3Button.addActionListener(createButtonActionListener(panel3Button, "Panel 3"));

        // Side Panel for Buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(3, 1));
        sidePanel.add(panel1Button);
        sidePanel.add(panel2Button);
        sidePanel.add(panel3Button);

        // Main + Side Panels
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidePanel, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Centers Window
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private JPanel createCheckInPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel for input field
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Guest Name Stuff
        JLabel guestNameLabel = new JLabel("Guest Name: ");
        JTextField guestNameField = new JTextField(20); // 20 columns for the text field
        inputPanel.add(guestNameLabel);
        inputPanel.add(guestNameField);

        // Date Picker
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Set default date to current date
        dateChooser.setDateFormatString("MM/dd/yyyy"); // Format of the selected date (Month/Day/Year)
        inputPanel.add(dateChooser);

        // Submit Button
        JButton submitButton = new JButton("Submit");

        // Submission method + Field Clear
        ActionListener submitAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitData(guestNameField, dateChooser);
                guestNameField.setText("");
            }
        };

        // Submit ActionListener
        submitButton.addActionListener(submitAction);

        // Enter Key Functionality
        guestNameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAction.actionPerformed(e);
            }
        });

        inputPanel.add(submitButton);

        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2, 2));

        // Table Labels
        JLabel nameLabel = new JLabel("Name");
        JLabel dateLabel = new JLabel("Date");
        JLabel editLabel = new JLabel("Edit");
        JLabel deleteLabel = new JLabel("Delete");

        formPanel.add(nameLabel);
        formPanel.add(editLabel);
        formPanel.add(dateLabel);
        formPanel.add(deleteLabel);

        inputPanel.add(guestNameLabel, BorderLayout.WEST);
        inputPanel.add(guestNameField, BorderLayout.CENTER);
        inputPanel.add(dateChooser, BorderLayout.EAST);
        inputPanel.add(submitButton, BorderLayout.SOUTH);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table Init
        String[] columnNames = {"Name", "Date", "Edit", "Delete"};
        Object[][] data = {
            // Right now this just creates an empty object. You're gonna have to figure
                // out some way to put stuff in from the backend/input field. Check the submission
                // method below for some more info.
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        // Label Sizing
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Button Init Stuff, mostly to stop border focus
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false); // Disable focus border
        return button;
    }

    //Submission Handling
    private void submitData(JTextField guestNameField, JDateChooser dateChooser) {
        String guestName = guestNameField.getText();
        Date selectedDate = dateChooser.getDate();

        // Prevent empty submission
        if (guestName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a guest name.", "Empty Guest Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Date Formatting
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(selectedDate);

        // Parse into first and last name
        String[] nameParts = guestName.split(" ");
        String firstName = nameParts[0];
        String lastName;

        // If there's no last name, just use the first name
        if (nameParts.length == 1) {
            lastName = "";
        }
        else
        {
            lastName = nameParts[1];
        }


        int nameHash = guestName.hashCode();
        String guestTableEntryTitle = "Guest_"+nameHash;
        Map<String, String> guestTableEntry = db.database.guests.get(guestTableEntryTitle);


        // Just have this printing sample stuff for now, so here's where to start I guess
        System.out.println("Guest Name: " + guestName);
        System.out.println("Selected Date: " + formattedDate);

        if (guestTableEntry != null) {
            JOptionPane.showMessageDialog(this, "Guest already checked in.", "Duplicate Guest", JOptionPane.WARNING_MESSAGE);
            return;
        }
        else
        {
            guestTableEntry = new HashMap<String, String>();

            guestTableEntry.put("FirstName", firstName);
            guestTableEntry.put("LastName", lastName);
            guestTableEntry.put("GuestId", Integer.toString(nameHash));
            guestTableEntry.put("Date", formattedDate);

            db.database.guests.put(guestTableEntryTitle, guestTableEntry);

            db.push();
        }
    }

    // Button stuff for switching panels
    private ActionListener createButtonActionListener(JButton button, String panelName) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, panelName);
                activeButton.setEnabled(true); // Enable previously active button
                button.setEnabled(false); // Disable clicked button
                activeButton = button; // Update active button reference
            }
        };
    }

    // Panel stuff
    private JPanel createPanel(String panelTitle, String label) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(label, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenuMockupAlt();
            }
        });
    }

    public static void runMenu() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenuMockupAlt();
            }
        });
    }

}