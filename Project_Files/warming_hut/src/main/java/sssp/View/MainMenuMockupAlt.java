package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;

import sssp.Control.SecretManager;
import sssp.Helper.*;
import sssp.Model.*;
import sssp.View.BunkAssignment.Panels.BunkAssignmentPanel;
import sssp.View.GuestDetails.GuestDetailsPanel;
import sssp.View.Reporting.ExternalDataReportingPanel;
import sssp.View.components.ButtonColumn;

public class MainMenuMockupAlt extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JButton activeButton;
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
    private JTable table;
    private GuestDetailsPanel guestDetailsPanel;
    private ExternalDataReportingPanel dataReportingPanel;
    private JButton guestDetailsPanelButton;

    public MainMenuMockupAlt() {
        setTitle("HRDC Warming Center Manager");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH); // Sets the App Window to Maximized on Startup
        
        // Panel Switch Buttons
        JButton checkinPanelButton = createButton("Check In");
        JButton bunkAssignmentPanelButton = createButton("Bunk Assignment");
        guestDetailsPanelButton = createButton("Guest Details");
        JButton externalDataReportingPanelButton = createButton("HMIS + CW Reporting");

        // Main Panel
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Panels for Tab Switching
        JPanel checkInPanel = createCheckInPanel();
        BunkAssignmentPanel.mainBunkPanel = BunkAssignmentPanel.getBunkAssignmentPanel();
        guestDetailsPanel = new GuestDetailsPanel();
        dataReportingPanel = new ExternalDataReportingPanel();

        // Add panels to the main panel
        mainPanel.add(checkInPanel, "Panel 1");
        mainPanel.add(BunkAssignmentPanel.mainBunkPanel, "Panel 2");
        mainPanel.add(guestDetailsPanel, "Panel 3");
        mainPanel.add(dataReportingPanel.createExternalDataReportingPanel(), "Panel 4");

        // Initialize Active Button
        activeButton = checkinPanelButton;
        activeButton.setEnabled(false); // Darkens active button

        // Panel Switching Action Listeners
        checkinPanelButton.addActionListener(createButtonActionListener(checkinPanelButton, "Panel 1"));
        //bunkAssignmentPanelButton.addActionListener(createButtonActionListener(bunkAssignmentPanelButton, "Panel 2"));
        bunkAssignmentPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.remove(BunkAssignmentPanel.mainBunkPanel);
                BunkAssignmentPanel.mainBunkPanel = BunkAssignmentPanel.getBunkAssignmentPanel();
                mainPanel.add(BunkAssignmentPanel.mainBunkPanel, "Panel 2");
                cardLayout.show(mainPanel, "Panel 2");
                activeButton.setEnabled(true); // Enable previously active button
                bunkAssignmentPanelButton.setEnabled(false); // Disable clicked button
                activeButton = bunkAssignmentPanelButton; // Update active button reference

            }
        });
        final Date[] previousBunkDate = {new Date()};
        BunkAssignmentPanel.dateChooser.setDateFormatString("MM/dd/yyyy");
        BunkAssignmentPanel.dateChooser.addPropertyChangeListener(
                new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        if ("date".equals(e.getPropertyName())) {
                            if(dateChooser.getDate() != previousBunkDate[0]){
                                mainPanel.remove(BunkAssignmentPanel.mainBunkPanel);
                                BunkAssignmentPanel.mainBunkPanel = BunkAssignmentPanel.getBunkAssignmentPanel();
                                mainPanel.add(BunkAssignmentPanel.mainBunkPanel, "Panel 2");
                                cardLayout.show(mainPanel, "Panel 2");
                                previousBunkDate[0] = dateChooser.getDate();
                            }
                        }
                    }
                });
        guestDetailsPanelButton.addActionListener(createButtonActionListener(guestDetailsPanelButton, "Panel 3"));
        externalDataReportingPanelButton.addActionListener(createButtonActionListener(externalDataReportingPanelButton,"Panel 4"));

        // Side Panel for Buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(4, 1));
        sidePanel.add(checkinPanelButton);
        sidePanel.add(bunkAssignmentPanelButton);
        sidePanel.add(guestDetailsPanelButton);
        sidePanel.add(externalDataReportingPanelButton);

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

        // Button to set DB secret Key
        JButton createDBSecretButton = new JButton("Set Database Key");
        createDBSecretButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // DB Secret Panel pops up here
                String secret = SecretManager.voluntarySecretUpdatePopup();
                db.setSecret(secret);
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

        // Start listening only AFTER all subscribers have been subscribed
        HttpStreamingManagerSingleton.startListening();
// endregion

        // Centers Window
        setLocationRelativeTo(null);

        setVisible(true);
    }


    JDateChooser dateChooser;
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
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Set default date to current date
        dateChooser.setDateFormatString("MM/dd/yyyy"); // Format of the selected date (Month/Day/Year)
        inputPanel.add(dateChooser);

        PropertyChangeListener dateChangedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("date"))
                {
                    updateGuestsTable();
                }
            }
        };

        dateChooser.getDateEditor().addPropertyChangeListener(dateChangedListener);

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

        //Removing this for now
        //panel.add(EditGuestPanel.getEditGuestPanel(panel), BorderLayout.EAST);

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

        int deleteColumn = 5;

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            // Disable cell editing
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == deleteColumn;
            }
        };
        table = new JTable(tableModel);

        // Create column of delete buttons.
        // Register the delete button action listener
        ButtonColumn deleteButtons = new ButtonColumn(table, onDeleteRowButtonPressed, deleteColumn);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int col = target.getSelectedColumn();
                    if(col == 5)
                    {
                        return;
                    }
                    String guestID = getGuestTableKey(target.getValueAt(row, 0).toString());
                    if(guestDetailsPanel.setActiveGuestID(guestID))
                    {
                        guestDetailsPanelButton.doClick();
                    }
                }
            }
        });

        // Label Sizing
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(15);
        table.getColumnModel().getColumn(2).setPreferredWidth(15);
        table.getColumnModel().getColumn(3).setPreferredWidth(15);
        table.getColumnModel().getColumn(4).setPreferredWidth(15);
        table.getColumnModel().getColumn(5).setMaxWidth(50);

        // hotkeys the delete key
        deleteButtons.setMnemonic(KeyEvent.VK_D);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        updateGuestsTable();

        return panel;
    }

    private List<Map<String,String>> filterGuestTableByRosterDate(Date d) {
        if (db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()) == null)
            return null;

        d = DateHelper.truncateToDay(d);

        List<Map<String, String>> checkins = db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).values().stream().toList();

        Instant dateInstant = d.toInstant();
            List<Map<String, String>> checkinsOnDate = checkins.stream().filter(
                e -> 
                {
                    try {
                        return instantsOnSameDayOfYear(Instant.parse(e.get(CheckinsDBKeys.DATE.getKey())), dateInstant);
                    } catch (Exception ex) {
                        return false;
                    }
                }
            ).toList();

        List<String> guestIDs = checkinsOnDate.stream().map(e -> e.get(CheckinsDBKeys.GUEST_ID.getKey())).toList();

        List<Map<String,String>> guestsForDate = new ArrayList<>();

        for(String guestID : guestIDs)
        {
            Map<String,String> guest = db.database.guests.get(guestID);
            guestsForDate.add(guest);
        }

        return guestsForDate;
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

        selectedDate = DateHelper.truncateToDay(selectedDate);

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

        // If there hasn't been a guest entry created, make one
        if (!db.database.guests.containsKey(guestTableKey)) {
            Map<String,String> guestTableEntry = createGuestEntry(guestName, formattedDate);
            db.database.guests.put(guestTableKey, guestTableEntry);
            db.asyncPush();
        }

        // If there hasn't been a checkin already, go ahead and create one
        if(!guestCheckedInOnDate(guestTableKey, selectedDate))
        {
            createAndPutCheckin(selectedDate, guestTableKey);
            db.asyncPush();
            updateGuestsTable();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Guest already checked in.", "Duplicate Checkin", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean instantsOnSameDayOfYear(Instant i1, Instant i2) {
        LocalDate d1 = i1.atOffset(ZoneOffset.UTC).toLocalDate();
        LocalDate d2 = i2.atOffset(ZoneOffset.UTC).toLocalDate();

        return d1.getDayOfYear() == d2.getDayOfYear();
    }

    private void createAndPutCheckin(Date date, String guestTableKey) {
        // create single "Check In"
        Map<String, String> checkinEntry = new HashMap<>();
        checkinEntry.put(CheckinsDBKeys.GUEST_ID.getKey(), guestTableKey);
        checkinEntry.put(CheckinsDBKeys.DATE.getKey(), date.toInstant().toString());
        checkinEntry.put(CheckinsDBKeys.EMERGENCY_SHELTER.getKey(), Boolean.toString(true));
        checkinEntry.put(CheckinsDBKeys.SERVICES_ONLY.getKey(), Boolean.toString(false));
        checkinEntry.put(CheckinsDBKeys.LAUNDRY.getKey(), Boolean.toString(false));
        checkinEntry.put(CheckinsDBKeys.CASEWORTHY_ENTERED.getKey(), Boolean.toString(false));
        checkinEntry.put(CheckinsDBKeys.HMIS_ENTERED.getKey(), Boolean.toString(false));
        checkinEntry.put(CheckinsDBKeys.SHOULD_DISPLAY.getKey(), Boolean.toString(true));

        // enter the "Check In" in to the DB
        db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).put(UUIDGenerator.getNewUUID(), checkinEntry);

        // if the checkin is either the first or latest checkin, update the respective fields in the guest entry

        boolean lastVisitExists = db.database.guests.get(guestTableKey).containsKey(GuestDBKeys.LAST_VISIT_DATE.getKey());
        Instant lastVisitDate = null;
        try {
            lastVisitDate = Instant.parse(db.database.guests.get(guestTableKey).get(GuestDBKeys.LAST_VISIT_DATE.getKey()));
        } catch (DateTimeParseException e) {
            lastVisitExists = false;
        } catch (NullPointerException e) {
            lastVisitExists = false;
        } catch (Exception e) {
            e.printStackTrace();
            lastVisitExists = false;
        }

        boolean firstVisitExists = db.database.guests.get(guestTableKey).containsKey(GuestDBKeys.GUEST_SINCE_DATE.getKey());
        Instant firstVisitDate = null;
        try {
            firstVisitDate = Instant.parse(db.database.guests.get(guestTableKey).get(GuestDBKeys.GUEST_SINCE_DATE.getKey()));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            firstVisitExists = false;
        } catch (NullPointerException e) {
            firstVisitExists = false;
        } catch (Exception e) {
            e.printStackTrace();
            firstVisitExists = false;
        }
        Instant dateInstant = date.toInstant();

        boolean checkinMostRecent;
        boolean checkinFirstVisit;

        if (lastVisitExists) {
            checkinMostRecent = dateInstant.isAfter(lastVisitDate);
        } else {
            checkinMostRecent = true;
        }

        if (firstVisitExists) {
            checkinFirstVisit = dateInstant.isBefore(firstVisitDate);
        } else {
            checkinFirstVisit = true;
        }

        if (checkinMostRecent) {
            db.database.guests.get(guestTableKey).put(GuestDBKeys.LAST_VISIT_DATE.getKey(), dateInstant.toString());
        }
        if (checkinFirstVisit) {
            db.database.guests.get(guestTableKey).put(GuestDBKeys.GUEST_SINCE_DATE.getKey(), dateInstant.toString());
        }
    }

    /** 
     * Checks if a guest has checked in on a specific date.
     * 
     * @param guestID the ID of the guest
     * @param date the date to check
    */
    private boolean guestCheckedInOnDate(String guestID, Date date) {
        Instant dateInstant = date.toInstant();

        List<Map<String, String>> checkins = db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).values().stream().toList();
        List<Map<String, String>> checkinsOnDate = checkins.stream().filter(
            e -> {
                try {
                    Instant stored = Instant.parse(e.get(CheckinsDBKeys.DATE.getKey()));
                    Instant target = dateInstant;
                    return instantsOnSameDayOfYear(stored, target);
                } catch (Exception ex) {
                    return false;
                }
            }
        ).toList();

        for (Map<String, String> checkin : checkinsOnDate) {
            if (checkin.get(CheckinsDBKeys.GUEST_ID.getKey()).equals(guestID)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Updates the guest table with the latest data from the database.
     */
    private void updateGuestsTable() {
        Date currentDate = dateChooser.getDate();

        List<Map<String,String>> guestsForDate = filterGuestTableByRosterDate(currentDate);

        // Update the guest table
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        // Clear the table
        tableModel.setRowCount(0);

        // Add the updated data
        for (Map<String,String> guest : guestsForDate) {

            // Check whether they have a small or medium locker
            String locker;
            if (guest.get("SmallLockerNumber") != null && !guest.get("SmallLockerNumber").isEmpty()) {
                locker = guest.get("SmallLockerNumber");
            } else if (guest.get("MediumLockerNumber") != null && !guest.get("MediumLockerNumber").isEmpty()) {
                locker = guest.get("MediumLockerNumber");
            } else {
                locker = "Unassigned";
            }

            // Check what kind of storage the guest is using (if any)
            String storage;
            if (guest.get("DayStorageShelf") != null && !guest.get("DayStorageShelf").isEmpty()) {
                storage = "shelf:" + guest.get("DayStorageShelf") + " slot:" + guest.get("DayStorageSlot");
            //} else if (guest.get("MediumLockerNumber") != null && !guest.get("MediumLockerNumber").isEmpty()) {
                //storage = guest.get("MediumLockerNumber"); TODO CHECK HOW CUBE STORAGE WORKS AND INCORPORATE IT (MAYBE ADD A NEW COLUMN)
            } else {
                storage = "Unassigned";
            }

            // Check for an assigned bunk (if any)
            String bunk = BunkAssignmentPanel.getLastAssignedBunk("Guest_"+guest.get("GuestId"), true, currentDate);
            /*
            if (guest.get("ReservedBunk") != null && !guest.get("ReservedBunk").isEmpty()) {
                bunk = guest.get("ReservedBunkSlot") + ":" + guest.get("ReservedBunk");
            } else {
                bunk = "Unassigned";
            } */

            // Check for any issues in order of seriousness
            String issue;
            String trespass = "";
            String suspension = "";
            String warning = "";

            LocalDate today = LocalDate.now(); //Date is of the form YYYY-MM-DD
            // TODO need to work out the GuestID + joinOnValue() - joinValue field, currenlty stumped on what/how i pass it what it needs
            // getGuestTableKey(String guestName)
            List<Map<String,String>> trespassData;
            List<Map<String,String>> suspensionData;
            List<Map<String,String>> warningData;

            trespassData = DBConnectorV2.filterByKeyValuePair(db.database.conflicts.get("NoTrespass"),
                    "GuestId", getGuestTableKey(guest.get("FirstName") + " " + guest.get("LastName")));

            suspensionData = DBConnectorV2.filterByKeyValuePair(db.database.conflicts.get("Suspensions"),
                    "GuestId", getGuestTableKey(guest.get("FirstName") + " " + guest.get("LastName")));

            warningData = DBConnectorV2.filterByKeyValuePair(db.database.conflicts.get("Warnings"),
                    "GuestId", getGuestTableKey(guest.get("FirstName") + " " + guest.get("LastName")));

            // check each entry in trespassData list
            for (Map<String,String> oneTrespass : trespassData) {
                if (oneTrespass != null && !oneTrespass.containsKey(NoTrespassDBKeys.STAFF_INITIALS.getKey())) {
                    trespass = "NO TRESPASS";
                }
                else {
                    trespass = "None";
                }
            }
            // check each entry in the suspensionData list
            for (Map<String,String> oneSuspension : suspensionData) {
                if (oneSuspension != null && !oneSuspension.get(SuspensionDBKeys.GUEST_ID.getKey()).isEmpty()) {
                    suspension = "SUSPENSION";
                }
                else {
                    suspension = "None";
                }
            }
            // check each entry in the warningData list
            for (Map<String,String> oneWarning : warningData) {
                if (oneWarning != null && !oneWarning.get(WarningDBKeys.GUEST_ID.getKey()).isEmpty()) {
                    warning = "WARNING";
                }
                else {
                    warning = "None";
                }
            }
            // checks if there are No Trespass, Suspensions, or Warnings - displays the most serious one in checkin table
            if (!trespass.equals("")){
                issue = trespass;
            } else if (!suspension.equals("")) {
                issue = suspension;
            } else if (!warning.equals("")) {
                issue = warning;
            } else {
                issue = "No Issues";
            }

            String[] rowData = {guest.get("FirstName") + " " + guest.get("LastName"),
                    locker, storage, bunk, issue};
            tableModel.addRow(rowData);
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
     * Updates the guest table when DB updates.
     */
    private void onDatabasePut()
    {
        updateGuestsTable();
    }

    // Source: https://tips4java.wordpress.com/2009/07/12/table-button-column/
    /**
     * Event listener that listens for when the delete button in a row of the guest list is pressed.
     * When the delete button is pressed, the row is removed from the table and the entry is removed from the database.
     */
    Action onDeleteRowButtonPressed = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            JTable table = (JTable)evt.getSource();
            int modelRow = Integer.valueOf( evt.getActionCommand() );
            Object delete = table.getModel().getValueAt(modelRow, 5);
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

                String associatedGuest = getGuestTableKey(guestName);
                Date currentSelected = dateChooser.getDate();
                currentSelected = DateHelper.truncateToDay(currentSelected);

                Set<Map.Entry<String, Map<String,String>>> checkins = db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).entrySet();

                // filter where checkin.GuestId == associatedGuest && checkin.Date == currentSelected
                for (Map.Entry<String, Map<String,String>> entry : checkins) 
                {
                    if (entry.getValue().get(CheckinsDBKeys.GUEST_ID.getKey()).equals(associatedGuest) &&
                            instantsOnSameDayOfYear(Instant.parse(entry.getValue().get(CheckinsDBKeys.DATE.getKey())), currentSelected.toInstant()))
                    {
                        // remove entry from checkins
                        db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).put(entry.getKey(), null);
                        break;
                    }
                }
                db.asyncPush();
            }
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
        runMenu();
    }

    public static void runMenu() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put( "Button.arc", 5 );
        UIManager.put( "Component.arc", 5 );
        UIManager.put( "ProgressBar.arc", 5 );
        UIManager.put( "TextComponent.arc", 5 );
        UIManager.put( "ScrollBar.trackInsets", new Insets( 2, 4, 2, 4 ) );
        UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
        UIManager.put( "ScrollBar.track", new Color( 0xe0e0e0 ) );
        UIManager.put( "TabbedPane.showTabSeparators", true );

        UIManager.put( "TabbedPane.selectedBackground", Color.white );
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenuMockupAlt();
            }
        });
    }

}