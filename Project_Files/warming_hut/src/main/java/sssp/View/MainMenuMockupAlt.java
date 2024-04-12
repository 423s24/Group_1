package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.HttpStreamingManagerSingleton;

public class MainMenuMockupAlt extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton activeButton;
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
    private JTable table;

    public MainMenuMockupAlt() {
        setTitle("HRDC Warming Center Manager");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Panel Switch Buttons
        JButton panel1Button = createButton("Check In");
        JButton panel2Button = createButton("Bunk Assignment");
        JButton panel3Button = createButton("Guest Details");
        JButton panel4Button = createButton("HMIS + CW Reporting");

        // Main Panel
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Panels for Tab Switching
        JPanel panel1 = createCheckInPanel();
        JPanel panel2 = BunkAssignmentPanel.getBunkAssignmentPanel();
        GuestDetailsPanel panel3 = new GuestDetailsPanel();
        ExternalDataReportingPanel panel4 = new ExternalDataReportingPanel();

        // Add panels to the main panel
        mainPanel.add(panel1, "Panel 1");
        mainPanel.add(panel2, "Panel 2");
        mainPanel.add(panel3, "Panel 3");
        mainPanel.add(panel4.createExternalDataReportingPanel(), "Panel 4");

        // Initialize Active Button
        activeButton = panel1Button;
        activeButton.setEnabled(false); // Darkens active button

        // Panel Switching Action Listeners
        panel1Button.addActionListener(createButtonActionListener(panel1Button, "Panel 1"));
        panel2Button.addActionListener(createButtonActionListener(panel2Button, "Panel 2"));
        panel3Button.addActionListener(createButtonActionListener(panel3Button, "Panel 3"));
        panel4Button.addActionListener(createButtonActionListener(panel4Button,"Panel 4"));

        // Side Panel for Buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(4, 1));
        sidePanel.add(panel1Button);
        sidePanel.add(panel2Button);
        sidePanel.add(panel3Button);
        sidePanel.add(panel4Button);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        Border border = BorderFactory.createEtchedBorder();

        // Documentation button
        JButton docsButton = new JButton("Help!");
        docsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://docs.google.com/document/d/1NFg2_1X0lsAy_qRDhNyk6lcxtxBhPvKlntXFOtWWhFw/edit"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        topPanel.add(docsButton);

        // Github Issue button
        JButton createIssueButton = new JButton("Report a Bug");
        createIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/423s24/Group_1/issues"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        topPanel.add(createIssueButton);

        //Global Settings Button
        // TODO create button to set DB secret
        JButton createDBSecretButton = new JButton("Set Database Key");
        createDBSecretButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    // DB Secret Panel pops up here
                SetDBSecretPopup.displaySetDBSecretPopup();
            }
        });
        topPanel.add(createDBSecretButton);
        topPanel.setBorder(border);

        // Main + Side Panels
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(sidePanel, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

// region DATABASE INIT - panels should be initialized before this is done
        // Subscribe to database events
        db.subscribeRunnableToDBUpdate(this::onDatabasePut);

        // Start listening only AFTER db has been instantiated
        HttpStreamingManagerSingleton.startListening();
// endregion

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
        guestNameField.addActionListener(submitAction);

        ((AbstractDocument) guestNameField.getDocument()).setDocumentFilter(new NameAutocompleteDocumentFilter(guestNameField));

        inputPanel.add(submitButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(EditGuestPanel.getEditGuestPanel(panel), BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridLayout(2, 2));

        // Table Labels
        JLabel nameLabel = new JLabel("Name");
        JLabel lockerLabel = new JLabel("Locker");
        JLabel dayStorageLabel = new JLabel("D. Storage");
        JLabel bunkAssignLabel = new JLabel("Bunk Assign.");
        JLabel behaviorFlagsLabel = new JLabel("Behavior Flags");
        //JLabel dateLabel = new JLabel("Date");
        JLabel deleteLabel = new JLabel("Delete");

        formPanel.add(nameLabel);
        formPanel.add(lockerLabel);
        formPanel.add(dayStorageLabel);
        formPanel.add(bunkAssignLabel);
        formPanel.add(behaviorFlagsLabel);
        //formPanel.add(dateLabel);
        formPanel.add(deleteLabel);

        inputPanel.add(guestNameLabel, BorderLayout.WEST);
        inputPanel.add(guestNameField, BorderLayout.CENTER);
        inputPanel.add(dateChooser, BorderLayout.EAST);
        inputPanel.add(submitButton, BorderLayout.SOUTH);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table Init
        String[] columnNames = {"Name", "Locker", "Storage", "Bunk Assign.", "Issues", "Delete"};
        Object[][] data = {
            // Right now this just creates an empty object. You're gonna have to figure
                // out some way to put stuff in from the backend/input field. Check the submission
                // method below for some more info.
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        // Label Sizing
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(15);
        table.getColumnModel().getColumn(2).setPreferredWidth(15);
        table.getColumnModel().getColumn(3).setPreferredWidth(15);
        table.getColumnModel().getColumn(4).setPreferredWidth(15);
        table.getColumnModel().getColumn(5).setMaxWidth(50);

        // register onTableCellUpdated, which must be done after table creation
        new TableCellListener(table, onTableCellUpdated);

        // Create column of delete buttons.
        // Register the delete button action listener
        ButtonColumn deleteButtons = new ButtonColumn(table, onDeleteRowButtonPressed, 5);

        // hotkeys the delete key
        deleteButtons.setMnemonic(KeyEvent.VK_D);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        updateGuestsTable();

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

        // Looks like "Guest_<integer>"
        String guestTableKey = getGuestTableKey(guestName); // TODO USE THIS IN THE CENTER TABLE IMPLEMENTATION

        if (db.database.guests.containsKey(guestTableKey)) {
            JOptionPane.showMessageDialog(this, "Guest already checked in.", "Duplicate Guest", JOptionPane.WARNING_MESSAGE);
            return;
        }
        else
        {
            Map<String,String> guestTableEntry = createGuestEntry(guestName, formattedDate);
            db.database.guests.put(guestTableKey, guestTableEntry);
            db.push();
        }
    }

    /**
     * Updates the guest table with the latest data from the database.
     */
    private void updateGuestsTable()
    {
        // Update the guest table
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        // Clear the table
        tableModel.setRowCount(0);

        // Add the updated data
        for (Map.Entry<String, Map<String, String>> entry : db.database.guests.entrySet()) {
            Map<String, String> guest = entry.getValue();
            String[] rowData = {guest.get("FirstName") + " " + guest.get("LastName")}; //TODO add updated guest info data here
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Updates the guest table based on the provided filter.
     * 
     * @param filter the filter to apply on the guest names
     */
    private void filterTable(String filter, JTable table)
    {
        // Update the guest table
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        // Clear the table
        tableModel.setRowCount(0);

        // Add the updated data
        for (Map.Entry<String, Map<String, String>> entry : db.database.guests.entrySet()) {
            Map<String, String> guest = entry.getValue();
            String guestName = guest.get("FirstName") + " " + guest.get("LastName");
            if(guestName.toLowerCase().contains(filter.toLowerCase()))
            {
                String[] rowData = {guestName, guest.get("Date")};
                tableModel.addRow(rowData);
            }
        }
    
    }

    /**
     * Creates a guest entry suitable for the guest table.
     *
     * @param guestName     the full name of the guest
     * @param formattedDate the formatted date of the guest entry
     * @return a map containing the guest table entry with the following key-value pairs:
     *         - "FirstName": the first name of the guest
     *         - "LastName": the last name of the guest
     *         - "GuestId": the hash code of the guest name
     *         - "Date": the formatted date of the guest entry
     */
    public Map<String, String> createGuestEntry(String guestName, String formattedDate) {
        String[] nameParts = guestName.split(" ");
        String firstName = nameParts[0];
        String lastName = (nameParts.length == 1) ? "" : nameParts[1];

        int nameHash = guestName.hashCode();
        Map<String, String> guestTableEntry = new HashMap<>();

        guestTableEntry = new HashMap<>();
        guestTableEntry.put("FirstName", firstName);
        guestTableEntry.put("LastName", lastName);
        guestTableEntry.put("GuestId", Integer.toString(nameHash));
        guestTableEntry.put("Date", formattedDate);

        return guestTableEntry;
    }

    /**
     * Returns the key for the guest table based on the guest name.
     *
     * @param guestName the name of the guest
     * @return the key for the guest table
     */
    public String getGuestTableKey(String guestName) {
        return "Guest_" + guestName.hashCode();
    }
    
    /**
     * Pulls data from the database and updates the guests table.
     */
    private void onDatabasePut()
    {
        db.pull();

        updateGuestsTable();
    }

    // Source: https://tips4java.wordpress.com/2009/07/12/table-button-column/
    /**
     * Event listener that listens for when the delete button in a row of the guest list is pressed.
     * When the delete button is pressed, the row is removed from the table and the entry is removed from the database.
     */
    Action onDeleteRowButtonPressed = new AbstractAction() {
        public void actionPerformed(ActionEvent e)
        {
            JTable table = (JTable)e.getSource();
            int modelRow = Integer.valueOf( e.getActionCommand() );
            Object delete = table.getModel().getValueAt(modelRow, 2);
            Window window = SwingUtilities.windowForComponent(table);

            String guestName = (String)table.getModel().getValueAt(modelRow, 0);

            int result = JOptionPane.showConfirmDialog(
                window,
                "Are you sure you want to delete " + guestName,
                "Delete Row Confirmation",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION)
            {
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);

                String toDeleteKey = getGuestTableKey(guestName);
                db.database.guests.put(toDeleteKey, null);
                db.push();
            }
        }
    };

    // Source: https://tips4java.wordpress.com/2009/06/07/table-cell-listener/
    /**
     * Event listener that listens for when a cell in the guest table is updated.
     * When a cell is updated, the entry in the database is updated with the new value.
     */
    Action onTableCellUpdated = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            TableCellListener tcl = (TableCellListener)e.getSource();
            int row = tcl.getRow();
            int col = tcl.getColumn();
            String oldValue = (String) tcl.getOldValue();
            String newValue = (String) tcl.getNewValue();

            String name = (String)table.getValueAt(row, 0);
            String date = (String)table.getValueAt(row, 1);
    
            String newGuestTableKey = getGuestTableKey(name);

            Map<String,String> guestTableEntry = createGuestEntry(name, date);
    
            // Key is based on name, so if the name changes, we must rekey the entry
            if(col == 0 && oldValue != null && !oldValue.equals(newValue)) {

                // If the name changed to an already existing name, reject the change
                if(db.database.guests.containsKey(newGuestTableKey))
                {
                    JOptionPane.showMessageDialog(null, "There's already a guest with that name.", "Duplicate Guest", JOptionPane.WARNING_MESSAGE);
                    table.setValueAt(oldValue, row, 0);
                    return;
                }
            
                String oldGuestTableKey = getGuestTableKey(oldValue);
                db.database.guests.put(oldGuestTableKey, null);
                db.push();
                db.database.guests.put(newGuestTableKey, guestTableEntry);
            }
            else
            {
                db.database.guests.put(newGuestTableKey, guestTableEntry);
            }

            db.push();
        }
    };

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