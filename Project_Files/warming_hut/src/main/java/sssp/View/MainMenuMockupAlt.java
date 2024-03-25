package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

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
import java.util.Optional;
import java.util.HashMap;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;


import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.HttpStreamingManager;
import sssp.Helper.HttpStreamingManagerSingleton;
import sssp.Helper.DatabaseEventListener;

public class MainMenuMockupAlt extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton activeButton;
    private DBConnectorV2 db;
    private JTable table;

    public MainMenuMockupAlt() {
        setTitle("HRDC Warming Center Manager");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

// region DATABASE INIT

        // Database Connection
        db = DBConnectorV2Singleton.getInstance();

        // Subscribe to database events
        HttpStreamingManagerSingleton.subscribeRunnable("put", this::onDatabasePut);

        // Start listening only AFTER events are subscribed
        HttpStreamingManagerSingleton.startListening();

// endregion
        
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
        GuestDetailsPanel panel3 = new GuestDetailsPanel();

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
        inputPanel.add(createIssueButton);

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
        inputPanel.add(docsButton);

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

        guestNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onGuestNameFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onGuestNameFieldChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onGuestNameFieldChanged(e);
            }
        });

        inputPanel.add(submitButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(EditGuestPanel.getEditGuestPanel(panel), BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridLayout(2, 2));

        // Table Labels
        JLabel nameLabel = new JLabel("Name");
        JLabel dateLabel = new JLabel("Date");
        JLabel deleteLabel = new JLabel("Delete");

        formPanel.add(nameLabel);
        formPanel.add(dateLabel);
        formPanel.add(deleteLabel);

        inputPanel.add(guestNameLabel, BorderLayout.WEST);
        inputPanel.add(guestNameField, BorderLayout.CENTER);
        inputPanel.add(dateChooser, BorderLayout.EAST);
        inputPanel.add(submitButton, BorderLayout.SOUTH);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table Init
        String[] columnNames = {"Name", "Date", "Delete"};
        Object[][] data = {
            // Right now this just creates an empty object. You're gonna have to figure
                // out some way to put stuff in from the backend/input field. Check the submission
                // method below for some more info.
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        // Label Sizing
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setMaxWidth(50);

        // register onTableCellUpdated, which must be done after table creation
        new TableCellListener(table, onTableCellUpdated);

        ButtonColumn deleteButtons = new ButtonColumn(table, onDeleteRowButtonPressed, 2);
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
        String guestTableKey = getGuestTableKey(guestName);

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

    public void onGuestNameFieldChanged(DocumentEvent e)
    {
        Document field = e.getDocument();

        try
        {
            this.updateGuestsTable(field.getText(0, field.getLength()));
        }
        catch(BadLocationException ex)
        {
            // uh oh!
            throw new RuntimeException(ex.getMessage());
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
            String[] rowData = {guest.get("FirstName") + " " + guest.get("LastName"), guest.get("Date")};
            tableModel.addRow(rowData);
        }
    }

    
    /**
     * Updates the guest table based on the provided filter.
     * 
     * @param filter the filter to apply on the guest names
     */
    private void updateGuestsTable(String filter)
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