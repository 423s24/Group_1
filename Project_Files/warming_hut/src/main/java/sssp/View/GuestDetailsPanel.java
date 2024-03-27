package sssp.View;

import com.toedter.calendar.JDateChooser;

import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.HttpStreamingManagerSingleton;
import sssp.Helper.DBConnectorV2;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;


import static sssp.View.DisciplinaryInfoPanel.getDisciplinaryInfoPanel;

public class GuestDetailsPanel extends JPanel {
    private JLabel guestNameLabel;
    private JTextField searchField;
    private JButton searchButton;

    // data fields in the Basic Guest Data panel
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextArea notesTextArea;
    private JDateChooser guestSinceDate;
    private JDateChooser lastVisitDate;

    // data fields in the Guest Info Checks panel
    private JCheckBox caseCheckBox;
    private JCheckBox HMISCheckBox;
    private JCheckBox sleepingBagBox;
    private JDateChooser sleepingBagDate;
    private JCheckBox tentBox;
    private JDateChooser tentDate;
    private JCheckBox backpackBox;
    private JDateChooser backpackDate;
    private JCheckBox outreachBackpackBox;
    private JDateChooser outreachBackpackDate;
    private JCheckBox sleepingPadBox;
    private JDateChooser sleepingPadDate;

    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private String activeGuestID = null;
    private Map<String, String> activeGuestData = null;

    public GuestDetailsPanel() {
        setLayout(new BorderLayout());

        guestNameLabel = new JLabel("Guest Name Lookup:");
        searchField = new JTextField(20);
        ((AbstractDocument) searchField.getDocument()).setDocumentFilter(new NameAutocompleteDocumentFilter(searchField));
        searchButton = new JButton("Search");

        JPanel topBarPanel = new JPanel();
        topBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topBarPanel.add(guestNameLabel);
        topBarPanel.add(searchField);
        topBarPanel.add(searchButton);

        Border topBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY);
        Border regBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
        topBarPanel.setBorder(topBorder);


        addBasicInfo();

        addInfoChecks();

        addStorageInfo();

        JPanel disciplinaryInfoPanel = getDisciplinaryInfoPanel();
        disciplinaryInfoPanel.setBorder(regBorder);

        JPanel storageInfoPanel = new JPanel();
        storageInfoPanel.setLayout(new BorderLayout());
        JLabel storageInfoLabel = new JLabel("Storage Info");
        storageInfoPanel.add(storageInfoLabel, BorderLayout.NORTH);
        storageInfoPanel.setBorder(regBorder);


        add(topBarPanel, BorderLayout.NORTH);

        add(disciplinaryInfoPanel, BorderLayout.CENTER);
    
        // Add listeners for text fields
        firstNameField.addActionListener(e -> storeTextFieldState(firstNameField, "FirstName"));
        lastNameField.addActionListener(e -> storeTextFieldState(lastNameField, "LastName"));
    
        // Add listeners for text areas
        notesTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(notesTextArea, "Notes");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(notesTextArea, "Notes");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(notesTextArea, "Notes");
            }
        });

        // Add property change listeners for date choosers
        guestSinceDate.addPropertyChangeListener(e-> storeDateState(guestSinceDate, "FirstHoused"));
        lastVisitDate.addPropertyChangeListener(e-> storeDateState(lastVisitDate, "LastVisit"));
    
        // Add action listeners for checkboxes
        caseCheckBox.addActionListener(e -> storeCheckboxState(caseCheckBox, "CaseCheck"));
        HMISCheckBox.addActionListener(e -> storeCheckboxState(HMISCheckBox, "HMISCheck"));
        sleepingBagBox.addActionListener(e -> storeCheckboxState(sleepingBagBox, "SleepingBagCheck"));
        tentBox.addActionListener(e -> storeCheckboxState(tentBox, "TentCheck"));
        backpackBox.addActionListener(e -> storeCheckboxState(backpackBox, "BackpackCheck"));
        outreachBackpackBox.addActionListener(e -> storeCheckboxState(outreachBackpackBox, "OutreachBackpackCheck"));
        sleepingPadBox.addActionListener(e -> storeCheckboxState(sleepingPadBox, "SleepingPadCheck"));
    
        // Add property change listeners for date choosers associated with checkboxes
        sleepingBagDate.addPropertyChangeListener(e -> storeDateState(sleepingBagDate, "SleepingBagDate"));
        tentDate.addPropertyChangeListener(e -> storeDateState(tentDate, "TentDate"));
        backpackDate.addPropertyChangeListener(e -> storeDateState(backpackDate, "BackpackDate"));
        outreachBackpackDate.addPropertyChangeListener(e -> storeDateState(outreachBackpackDate, "OutreachBackpackDate"));
        sleepingPadDate.addPropertyChangeListener(e -> storeDateState(sleepingPadDate, "SleepingPadDate"));

        // Add action listeners for the search button and search field
        searchButton.addActionListener(e -> {
            String key = getGuestTableKey(searchField.getText());
            setActiveGuestID(key);}
        );

        searchField.addActionListener(e -> {
            String key = getGuestTableKey(searchField.getText());
            setActiveGuestID(key);
        });

        db.subscribeRunnableToDBUpdate(this::onDBUpdate);
    }

    private static void addSeparator(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.GRAY);
        panel.add(separator, gbc);
    }

    private static void addVertSeparator(JPanel panel, GridBagConstraints gbc) {
        gbc.gridy = 0;
        gbc.gridx++;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(Color.GRAY);
        panel.add(separator, gbc);
    }

    private void addInfoChecks() {
        JPanel infoChecksPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 20, 5);

        JLabel caseCheckLabel = new JLabel("CW");
        infoChecksPanel.add(caseCheckLabel, gbc);

        gbc.gridx++;
        caseCheckBox = new JCheckBox();
        infoChecksPanel.add(caseCheckBox, gbc);

        gbc.gridx++;
        JLabel HMISCheckLabel = new JLabel("HMIS");
        infoChecksPanel.add(HMISCheckLabel, gbc);

        gbc.gridx++;
        HMISCheckBox = new JCheckBox();
        infoChecksPanel.add(HMISCheckBox, gbc);

        addVertSeparator(infoChecksPanel, gbc);

        gbc.gridx++;
        JLabel sleepingBagLabel = new JLabel("Sleeping Bag");
        infoChecksPanel.add(sleepingBagLabel, gbc);

        gbc.gridx++;
        sleepingBagBox = new JCheckBox();
        infoChecksPanel.add(sleepingBagBox, gbc);

        gbc.gridx++;
        sleepingBagDate = new JDateChooser();
        sleepingBagDate.setDateFormatString("MM/dd/yyyy");
        sleepingBagDate.setVisible(false); // Start hidden
        infoChecksPanel.add(sleepingBagDate, gbc);

        addVertSeparator(infoChecksPanel, gbc);

        sleepingBagBox.addActionListener(e -> {
            boolean isSelected = sleepingBagBox.isSelected();
            sleepingBagDate.setVisible(isSelected);
            if (!isSelected) {
                sleepingBagDate.setDate(null);
            }
            infoChecksPanel.revalidate();
            infoChecksPanel.repaint();
        });

        gbc.gridx++;
        JLabel tentLabel = new JLabel("Tent");
        infoChecksPanel.add(tentLabel, gbc);

        gbc.gridx++;
        tentBox = new JCheckBox();
        infoChecksPanel.add(tentBox, gbc);

        gbc.gridx++;
        tentDate = new JDateChooser();
        tentDate.setDateFormatString("MM/dd/yyyy");
        tentDate.setVisible(false);
        infoChecksPanel.add(tentDate, gbc);

        tentBox.addActionListener(e -> {
            boolean isSelected = tentBox.isSelected();
            tentDate.setVisible(isSelected);
            if (!isSelected) {
                tentDate.setDate(null);
            }
            infoChecksPanel.revalidate();
            infoChecksPanel.repaint();
        });

        addVertSeparator(infoChecksPanel, gbc);

        gbc.gridx++;
        JLabel backpackLabel = new JLabel("Backpack");
        infoChecksPanel.add(backpackLabel, gbc);

        gbc.gridx++;
        backpackBox = new JCheckBox();
        infoChecksPanel.add(backpackBox, gbc);

        gbc.gridx++;
        backpackDate = new JDateChooser();
        backpackDate.setDateFormatString("MM/dd/yyyy");
        backpackDate.setVisible(false);
        infoChecksPanel.add(backpackDate, gbc);

        backpackBox.addActionListener(e -> {
            boolean isSelected = backpackBox.isSelected();
            backpackDate.setVisible(isSelected);
            if (!isSelected) {
                backpackDate.setDate(null);
            }
            infoChecksPanel.revalidate();
            infoChecksPanel.repaint();
        });

        addVertSeparator(infoChecksPanel, gbc);


        gbc.gridx++;
        JLabel outreachBackpackLabel = new JLabel("Outreach Backpack");
        infoChecksPanel.add(outreachBackpackLabel, gbc);

        gbc.gridx++;
        outreachBackpackBox = new JCheckBox();
        infoChecksPanel.add(outreachBackpackBox, gbc);

        gbc.gridx++;
        outreachBackpackDate = new JDateChooser();
        outreachBackpackDate.setDateFormatString("MM/dd/yyyy");
        outreachBackpackDate.setVisible(false); // Start hidden
        infoChecksPanel.add(outreachBackpackDate, gbc);

        outreachBackpackBox.addActionListener(e -> {
            boolean isSelected = outreachBackpackBox.isSelected();
            outreachBackpackDate.setVisible(isSelected);
            if (!isSelected) {
                outreachBackpackDate.setDate(null);
            }
            infoChecksPanel.revalidate();
            infoChecksPanel.repaint();
        });

        addVertSeparator(infoChecksPanel, gbc);

        gbc.gridx++;
        JLabel sleepingPadLabel = new JLabel("Sleeping Pad");
        infoChecksPanel.add(sleepingPadLabel, gbc);

        gbc.gridx++;
        sleepingPadBox = new JCheckBox();
        infoChecksPanel.add(sleepingPadBox, gbc);

        gbc.gridx++;
        sleepingPadDate = new JDateChooser();
        sleepingPadDate.setDateFormatString("MM/dd/yyyy");
        sleepingPadDate.setVisible(false);
        infoChecksPanel.add(sleepingPadDate, gbc);

        sleepingPadBox.addActionListener(e -> {
            boolean isSelected = sleepingPadBox.isSelected();
            sleepingPadDate.setVisible(isSelected);
            if (!isSelected) {
                sleepingPadDate.setDate(null);
            }
            infoChecksPanel.revalidate();
            infoChecksPanel.repaint();
        });

        infoChecksPanel.setBorder(BorderFactory.createTitledBorder("Guest Info Checks"));
        add(infoChecksPanel, BorderLayout.SOUTH);
    }

    private void addStorageInfo() {
        JPanel storageInfoPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dayStoragePanel = createDayStoragePanel();
        JPanel lockerMediumPanel = createMedLockerPanel();
        JPanel lockerSmallPanel = createSmLockerPanel();
        JPanel cubeStoragePanel = createCubeStoragePanel();

        tabbedPane.addTab("Day Storage", dayStoragePanel);
        tabbedPane.addTab("Medium Lockers", lockerMediumPanel);
        tabbedPane.addTab("Small Lockers", lockerSmallPanel);
        tabbedPane.addTab("Cube Storage", cubeStoragePanel);

        storageInfoPanel.add(tabbedPane, BorderLayout.CENTER);

        storageInfoPanel.setBorder(BorderFactory.createTitledBorder("Storage Info"));

        add(storageInfoPanel, BorderLayout.EAST);
    }

    private JPanel createDayStoragePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("Shelf:");
        panel.add(firstNameLabel, gbc);

        gbc.gridy++;
        JTextField firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);

        gbc.gridy++;
        JLabel lastNameLabel = new JLabel("Slot:");
        panel.add(lastNameLabel, gbc);

        gbc.gridy++;
        JTextField lastNameField = new JTextField(20);
        panel.add(lastNameField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("Start Date:");
        panel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        JDateChooser guestSinceDate = new JDateChooser();
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        panel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Expiration Date:");
        panel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        JDateChooser lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        panel.add(lastVisitDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Container Description:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        JTextArea notesTextArea = new JTextArea(5, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel staffIDLabel = new JLabel("Staff Initials:");
        panel.add(staffIDLabel, gbc);

        gbc.gridy++;
        JTextField staffIDField = new JTextField(5);
        panel.add(staffIDField, gbc);

        gbc.gridy++;
        JLabel contractLabel = new JLabel("Contract Signed?");
        panel.add(contractLabel, gbc);

        gbc.gridy++;
        JCheckBox contractBox = new JCheckBox();
        panel.add(contractBox, gbc);

        return panel;
    }

    private JPanel createMedLockerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("Locker Number:");
        panel.add(firstNameLabel, gbc);

        gbc.gridy++;
        JTextField firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);

        gbc.gridy++;
        JLabel lastNameLabel = new JLabel("Approved Accommodation Link:");
        panel.add(lastNameLabel, gbc);

        gbc.gridy++;
        JTextField lastNameField = new JTextField(20);
        panel.add(lastNameField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("Start Date:");
        panel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        JDateChooser guestSinceDate = new JDateChooser();
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        panel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Date Last Accessed:");
        panel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        JDateChooser lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        panel.add(lastVisitDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        JTextArea notesTextArea = new JTextArea(5, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel staffIDLabel = new JLabel("Assigning Staff Member:");
        panel.add(staffIDLabel, gbc);

        gbc.gridy++;
        JTextField staffIDField = new JTextField(5);
        panel.add(staffIDField, gbc);

        return panel;
    }

    private JPanel createSmLockerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("Locker Number:");
        panel.add(firstNameLabel, gbc);

        gbc.gridy++;
        JTextField firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("Start Date:");
        panel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        JDateChooser guestSinceDate = new JDateChooser();
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        panel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Date Last Accessed:");
        panel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        JDateChooser lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        panel.add(lastVisitDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        JTextArea notesTextArea = new JTextArea(5, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel staffIDLabel = new JLabel("Assigning Staff Member:");
        panel.add(staffIDLabel, gbc);

        gbc.gridy++;
        JTextField staffIDField = new JTextField(5);
        panel.add(staffIDField, gbc);

        return panel;
    }

    private JPanel createCubeStoragePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("Previous Location:");
        panel.add(firstNameLabel, gbc);

        gbc.gridy++;
        JTextField firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Reason for Move:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        JTextArea notesTextArea = new JTextArea(5, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        panel.add(notesScrollPane, gbc);

        gbc.gridy++;
        JLabel notesLabel2 = new JLabel("Container Description:");
        panel.add(notesLabel2, gbc);

        gbc.gridy++;
        JTextArea notesTextArea2 = new JTextArea(5, 20);
        notesTextArea2.setLineWrap(true);
        notesTextArea2.setWrapStyleWord(true);
        JScrollPane notesScrollPane2 = new JScrollPane(notesTextArea2);
        panel.add(notesScrollPane2, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("Start Date:");
        panel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        JDateChooser guestSinceDate = new JDateChooser();
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        panel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Expiration Date:");
        panel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        JDateChooser lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        panel.add(lastVisitDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel contractLabel = new JLabel("Guest Notified?");
        panel.add(contractLabel, gbc);

        gbc.gridy++;
        JCheckBox contractBox = new JCheckBox();
        panel.add(contractBox, gbc);

        return panel;
    }

    private void addBasicInfo() {
        JPanel basicDataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("First Name:");
        basicDataPanel.add(firstNameLabel, gbc);

        gbc.gridy++;
        firstNameField = new JTextField(20);
        basicDataPanel.add(firstNameField, gbc);

        gbc.gridy++;
        JLabel lastNameLabel = new JLabel("Last Name:");
        basicDataPanel.add(lastNameLabel, gbc);

        gbc.gridy++;
        lastNameField = new JTextField(20);
        basicDataPanel.add(lastNameField, gbc);

        addSeparator(basicDataPanel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("First Housed On:");
        basicDataPanel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        guestSinceDate = new JDateChooser();
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        basicDataPanel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Last Visit:");
        basicDataPanel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        lastVisitDate = new JDateChooser();
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        basicDataPanel.add(lastVisitDate, gbc);

        addSeparator(basicDataPanel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        basicDataPanel.add(notesLabel, gbc);

        gbc.gridy++;
        notesTextArea = new JTextArea(10, 20);
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        basicDataPanel.add(notesScrollPane, gbc);

        basicDataPanel.setBorder(BorderFactory.createTitledBorder("Basic Guest Data"));

        add(basicDataPanel, BorderLayout.WEST);
    }

    public void setActiveGuestID(String guestID) {
        this.activeGuestID = guestID;

        this.activeGuestData = db.database.guests.get(guestID);

        if(activeGuestData == null) {
            // Show error window
            JOptionPane.showMessageDialog(null, "Error: The guest " + guestID + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updateFieldsFromActiveGuest();
    }

    private void updateFieldsFromActiveGuest() {
        // Populate simple text fields
        retrieveTextFieldState(firstNameField, "FirstName");
        retrieveTextFieldState(lastNameField, "LastName");
        retrieveTextAreaState(notesTextArea, "Notes");

        // Populate date fields
        retrieveDateState(guestSinceDate, "FirstHoused");
        retrieveDateState(lastVisitDate, "LastVisit");

        // Populate checkboxes and their corresponding dates
        retrieveCheckboxState(caseCheckBox, "CaseCheck");
        retrieveCheckboxState(HMISCheckBox, "HMISCheck");

        retrieveCheckboxState(sleepingBagBox, "SleepingBagCheck");
        retrieveDateState(sleepingBagDate, "SleepingBagDate");
    
        retrieveCheckboxState(tentBox, "TentCheck");
        retrieveDateState(tentDate, "TentDate");
    
        retrieveCheckboxState(backpackBox, "BackpackCheck");
        retrieveDateState(backpackDate, "BackpackDate");
    
        retrieveCheckboxState(outreachBackpackBox, "OutreachBackpackCheck");
        retrieveDateState(outreachBackpackDate, "OutreachBackpackDate");
    
        retrieveCheckboxState(sleepingPadBox, "SleepingPadCheck");
        retrieveDateState(sleepingPadDate, "SleepingPadDate");
    }

    private String getValueFromActiveGuest(String key)
    {
        return activeGuestData.get(key);
    }

    private void putActiveGuestValue(String key, String value)
    {
        if (activeGuestData == null)
        {
            activeGuestData = new HashMap<>();
        }

        if(activeGuestID == null)
        {
            return;
        }
        activeGuestData.put(key, value);
        db.database.guests.put(activeGuestID, activeGuestData);
        db.push();
    }

    private void storeCheckboxState(JCheckBox toStore, String stateKey) {
        putActiveGuestValue(stateKey, Boolean.toString(toStore.isSelected()));
    }

    private void retrieveCheckboxState(JCheckBox toSet, String stateKey) {
        toSet.setSelected(Boolean.parseBoolean(getValueFromActiveGuest(stateKey)));
    }

    private void storeDateState(JDateChooser toStore, String stateKey) {
        putActiveGuestValue(stateKey, formatDate(toStore.getDate()));
    }
    
    private void retrieveDateState(JDateChooser toSet, String stateKey) {
        if(parseDate(activeGuestData.get(stateKey)) != null) {
            toSet.setDate(parseDate(getValueFromActiveGuest(stateKey)));
        }
    }

    private void storeTextFieldState(JTextField toStore, String stateKey) {
        putActiveGuestValue(stateKey, toStore.getText());
    }

    private void retrieveTextFieldState(JTextField toSet, String stateKey) {
        toSet.setText(getValueFromActiveGuest(stateKey));
    }

    private void storeTextAreaState(JTextArea toStore, String stateKey) {
        putActiveGuestValue(stateKey, toStore.getText());
    }

    private void retrieveTextAreaState(JTextArea toSet, String stateKey) {
        toSet.setText(getValueFromActiveGuest(stateKey));
    }

    private Date parseDate(String dateString)
    {
        try
        {
            return new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        return new SimpleDateFormat("MM/dd/yyyy").format(date);
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

    public void onDBUpdate()
    {
        updateFieldsFromActiveGuest();
    }
}