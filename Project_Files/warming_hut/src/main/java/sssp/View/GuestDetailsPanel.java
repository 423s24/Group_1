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

    // data fields in the Storage Info panel

    // small lockers
    private JTextField smLockerLockerNumberField;
    private JDateChooser smLockerStartDate;
    private JDateChooser smLockerLastAccessedDate;
    private JTextArea smLockerNotesTextArea;
    private JTextField smLockerAssigningStaffField;

    // day storage
    private JTextField dayStorageShelfField;
    private JTextField dayStorageSlotField;
    private JDateChooser dayStorageStartDate;
    private JDateChooser dayStorageExpirationDate;
    private JTextArea dayStorageContainerDescriptionTextArea;
    private JTextField dayStorageStaffInitialsField;
    private JCheckBox dayStorageContractBox;

    // cube storage
    private JTextField csPreviousLocationField;
    private JTextArea csReasonForMoveTextArea;
    private JTextArea csContainerDescriptionTextArea;
    private JDateChooser csStartDate;
    private JDateChooser csExpirationDate;
    private JCheckBox csGuestNotifiedBox;

    // medium lockers
    private JTextField medlockerNumberField;
    private JTextField medlockerAccommodationLinkField;
    private JDateChooser medlockerStartDate;
    private JDateChooser medlockerLastAccessedDate;
    private JTextArea medlockerNotesTextArea;
    private JTextField medlockerAssigningStaffField;

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

// region STORAGE INFO LISTENERS
        // Add listeners for storage info
        smLockerLockerNumberField.addActionListener(e -> storeTextFieldState(smLockerLockerNumberField, "SmallLockerNumber"));
        smLockerAssigningStaffField.addActionListener(e -> storeTextFieldState(smLockerAssigningStaffField, "SmallLockerAssigningStaff"));
        dayStorageShelfField.addActionListener(e -> storeTextFieldState(dayStorageShelfField, "DayStorageShelf"));
        dayStorageSlotField.addActionListener(e -> storeTextFieldState(dayStorageSlotField, "DayStorageSlot"));
        dayStorageStaffInitialsField.addActionListener(e -> storeTextFieldState(dayStorageStaffInitialsField, "DayStorageStaffInitials"));
        csPreviousLocationField.addActionListener(e -> storeTextFieldState(csPreviousLocationField, "CubeStoragePreviousLocation"));
        medlockerNumberField.addActionListener(e -> storeTextFieldState(medlockerNumberField, "MediumLockerNumber"));
        medlockerAccommodationLinkField.addActionListener(e -> storeTextFieldState(medlockerAccommodationLinkField, "MediumLockerAccommodationLink"));
        medlockerAssigningStaffField.addActionListener(e -> storeTextFieldState(medlockerAssigningStaffField, "MediumLockerAssigningStaff"));

        // Add listeners for text areas
        smLockerNotesTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(smLockerNotesTextArea, "SmallLockerNotes");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(smLockerNotesTextArea, "SmallLockerNotes");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(smLockerNotesTextArea, "SmallLockerNotes");
            }
        });

        dayStorageContainerDescriptionTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(dayStorageContainerDescriptionTextArea, "DayStorageDescription");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(dayStorageContainerDescriptionTextArea, "DayStorageDescription");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(dayStorageContainerDescriptionTextArea, "DayStorageDescription");
            }
        });

        csReasonForMoveTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(csReasonForMoveTextArea, "CubeStorageReasonForMove");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(csReasonForMoveTextArea, "CubeStorageReasonForMove");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(csReasonForMoveTextArea, "CubeStorageReasonForMove");
            }
        });

        csContainerDescriptionTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(csContainerDescriptionTextArea, "CubeStorageDescription");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(csContainerDescriptionTextArea, "CubeStorageDescription");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(csContainerDescriptionTextArea, "CubeStorageDescription");
            }
        });

        medlockerNotesTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                storeTextAreaState(medlockerNotesTextArea, "MediumLockerNotes");
            }
            public void removeUpdate(DocumentEvent e) {
                storeTextAreaState(medlockerNotesTextArea, "MediumLockerNotes");
            }
            public void insertUpdate(DocumentEvent e) {
                storeTextAreaState(medlockerNotesTextArea, "MediumLockerNotes");
            }
        });

        // Add property change listeners for date choosers
        smLockerStartDate.addPropertyChangeListener(e-> storeDateState(smLockerStartDate, "SmallLockerStartDate"));
        smLockerLastAccessedDate.addPropertyChangeListener(e-> storeDateState(smLockerLastAccessedDate, "SmallLockerLastAccessedDate"));
        dayStorageStartDate.addPropertyChangeListener(e-> storeDateState(dayStorageStartDate, "DayStorageStartDate"));
        dayStorageExpirationDate.addPropertyChangeListener(e-> storeDateState(dayStorageExpirationDate, "DayStorageExpirationDate"));
        csStartDate.addPropertyChangeListener(e-> storeDateState(csStartDate, "CubeStorageStartDate"));
        csExpirationDate.addPropertyChangeListener(e-> storeDateState(csExpirationDate, "CubeStorageExpirationDate"));
        medlockerStartDate.addPropertyChangeListener(e-> storeDateState(medlockerStartDate, "MediumLockerStartDate"));
        medlockerLastAccessedDate.addPropertyChangeListener(e-> storeDateState(medlockerLastAccessedDate, "MediumLockerLastAccessedDate"));

        // Add action listeners for checkboxes
        dayStorageContractBox.addActionListener(e -> storeCheckboxState(dayStorageContractBox, "DayStorageContract"));
        csGuestNotifiedBox.addActionListener(e -> storeCheckboxState(csGuestNotifiedBox, "CubeStorageGuestNotified"));
// endregion

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

        JLabel shelfLabel = new JLabel("Shelf:");
        panel.add(shelfLabel, gbc);

        gbc.gridy++;
        dayStorageShelfField = new JTextField(20);
        panel.add(dayStorageShelfField, gbc);

        gbc.gridy++;
        JLabel slotLabel = new JLabel("Slot:");
        panel.add(slotLabel, gbc);

        gbc.gridy++;
        dayStorageSlotField = new JTextField(20);
        panel.add(dayStorageSlotField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel dayStorageStartDateLabel = new JLabel("Start Date:");
        panel.add(dayStorageStartDateLabel, gbc);

        gbc.gridy++;
        dayStorageStartDate = new JDateChooser();
        dayStorageStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(dayStorageStartDate, gbc);

        gbc.gridy++;
        JLabel expirationDateLabel = new JLabel("Expiration Date:");
        panel.add(expirationDateLabel, gbc);

        gbc.gridy++;
        dayStorageExpirationDate = new JDateChooser();
        dayStorageExpirationDate.setDateFormatString("MM/dd/yyyy");
        panel.add(dayStorageExpirationDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel containerDescriptionLabel = new JLabel("Container Description:");
        panel.add(containerDescriptionLabel, gbc);

        gbc.gridy++;
        dayStorageContainerDescriptionTextArea = new JTextArea(5, 20);
        dayStorageContainerDescriptionTextArea.setLineWrap(true);
        dayStorageContainerDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(dayStorageContainerDescriptionTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel staffInitialsLabel = new JLabel("Staff Initials:");
        panel.add(staffInitialsLabel, gbc);

        gbc.gridy++;
        dayStorageStaffInitialsField = new JTextField(5);
        panel.add(dayStorageStaffInitialsField, gbc);

        gbc.gridy++;
        JLabel contractLabel = new JLabel("Contract Signed?");
        panel.add(contractLabel, gbc);

        gbc.gridy++;
        dayStorageContractBox = new JCheckBox();
        panel.add(dayStorageContractBox, gbc);

        return panel;
    }

    private JPanel createMedLockerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel lockerNumberLabel = new JLabel("Locker Number:");
        panel.add(lockerNumberLabel, gbc);

        gbc.gridy++;
        medlockerNumberField = new JTextField(20);
        panel.add(medlockerNumberField, gbc);

        gbc.gridy++;
        JLabel accommodationLinkLabel = new JLabel("Approved Accommodation Link:");
        panel.add(accommodationLinkLabel, gbc);

        gbc.gridy++;
        medlockerAccommodationLinkField = new JTextField(20);
        panel.add(medlockerAccommodationLinkField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel startDateLabel = new JLabel("Start Date:");
        panel.add(startDateLabel, gbc);

        gbc.gridy++;
        medlockerStartDate = new JDateChooser();
        medlockerStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(medlockerStartDate, gbc);

        gbc.gridy++;
        JLabel lastAccessedLabel = new JLabel("Date Last Accessed:");
        panel.add(lastAccessedLabel, gbc);

        gbc.gridy++;
        medlockerLastAccessedDate = new JDateChooser();
        medlockerLastAccessedDate.setDateFormatString("MM/dd/yyyy");
        panel.add(medlockerLastAccessedDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        medlockerNotesTextArea = new JTextArea(5, 20);
        medlockerNotesTextArea.setLineWrap(true);
        medlockerNotesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(medlockerNotesTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel assigningStaffLabel = new JLabel("Assigning Staff Member:");
        panel.add(assigningStaffLabel, gbc);

        gbc.gridy++;
        medlockerAssigningStaffField = new JTextField(5);
        panel.add(medlockerAssigningStaffField, gbc);

        return panel;
    }

    private JPanel createSmLockerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel smLockerLockerNumberLabel = new JLabel("Locker Number:");
        panel.add(smLockerLockerNumberLabel, gbc);

        gbc.gridy++;
        smLockerLockerNumberField = new JTextField(20);
        panel.add(smLockerLockerNumberField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerStartDateLabel = new JLabel("Start Date:");
        panel.add(smLockerStartDateLabel, gbc);

        gbc.gridy++;
        smLockerStartDate = new JDateChooser();
        smLockerStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(smLockerStartDate, gbc);

        gbc.gridy++;
        JLabel smLockerLastAccessedLabel = new JLabel("Date Last Accessed:");
        panel.add(smLockerLastAccessedLabel, gbc);

        gbc.gridy++;
        smLockerLastAccessedDate = new JDateChooser();
        smLockerLastAccessedDate.setDateFormatString("MM/dd/yyyy");
        panel.add(smLockerLastAccessedDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerNotesLabel = new JLabel("Notes:");
        panel.add(smLockerNotesLabel, gbc);

        gbc.gridy++;
        smLockerNotesTextArea = new JTextArea(5, 20);
        smLockerNotesTextArea.setLineWrap(true);
        smLockerNotesTextArea.setWrapStyleWord(true);
        JScrollPane smLockerNotesScrollPane = new JScrollPane(smLockerNotesTextArea);
        panel.add(smLockerNotesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerAssigningStaffLabel = new JLabel("Assigning Staff Member:");
        panel.add(smLockerAssigningStaffLabel, gbc);

        gbc.gridy++;
        smLockerAssigningStaffField = new JTextField(5);
        panel.add(smLockerAssigningStaffField, gbc);

        return panel;
    }

    private JPanel createCubeStoragePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel csPreviousLocationLabel = new JLabel("Previous Location:");
        panel.add(csPreviousLocationLabel, gbc);

        gbc.gridy++;
        csPreviousLocationField = new JTextField(20);
        panel.add(csPreviousLocationField, gbc);

        gbc.gridy++;
        JLabel csReasonForMoveLabel = new JLabel("Reason for Move:");
        panel.add(csReasonForMoveLabel, gbc);

        gbc.gridy++;
        csReasonForMoveTextArea = new JTextArea(5, 20);
        csReasonForMoveTextArea.setLineWrap(true);
        csReasonForMoveTextArea.setWrapStyleWord(true);
        JScrollPane csReasonForMoveScrollPane = new JScrollPane(csReasonForMoveTextArea);
        panel.add(csReasonForMoveScrollPane, gbc);

        gbc.gridy++;
        JLabel csContainerDescriptionLabel = new JLabel("Container Description:");
        panel.add(csContainerDescriptionLabel, gbc);

        gbc.gridy++;
        csContainerDescriptionTextArea = new JTextArea(5, 20);
        csContainerDescriptionTextArea.setLineWrap(true);
        csContainerDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane csContainerDescriptionScrollPane = new JScrollPane(csContainerDescriptionTextArea);
        panel.add(csContainerDescriptionScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel csStartDateLabel = new JLabel("Start Date:");
        panel.add(csStartDateLabel, gbc);

        gbc.gridy++;
        csStartDate = new JDateChooser();
        csStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(csStartDate, gbc);

        gbc.gridy++;
        JLabel csExpirationDateLabel = new JLabel("Expiration Date:");
        panel.add(csExpirationDateLabel, gbc);

        gbc.gridy++;
        csExpirationDate = new JDateChooser();
        csExpirationDate.setDateFormatString("MM/dd/yyyy");
        panel.add(csExpirationDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel csGuestNotifiedLabel = new JLabel("Guest Notified?");
        panel.add(csGuestNotifiedLabel, gbc);

        gbc.gridy++;
        csGuestNotifiedBox = new JCheckBox();
        panel.add(csGuestNotifiedBox, gbc);

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

        // Populate the fields for the Storage Info panel
        retrieveTextFieldState(smLockerLockerNumberField, "SmallLockerNumber");
        retrieveDateState(smLockerStartDate, "SmallLockerStartDate");
        retrieveDateState(smLockerLastAccessedDate, "SmallLockerLastAccessedDate");
        retrieveTextAreaState(smLockerNotesTextArea, "SmallLockerNotes");
        retrieveTextFieldState(smLockerAssigningStaffField, "SmallLockerAssigningStaff");

        retrieveTextFieldState(dayStorageShelfField, "DayStorageShelf");
        retrieveTextFieldState(dayStorageSlotField, "DayStorageSlot");
        retrieveDateState(dayStorageStartDate, "DayStorageStartDate");
        retrieveDateState(dayStorageExpirationDate, "DayStorageExpirationDate");
        retrieveTextAreaState(dayStorageContainerDescriptionTextArea, "DayStorageDescription");
        retrieveTextFieldState(dayStorageStaffInitialsField, "DayStorageStaffInitials");
        retrieveCheckboxState(dayStorageContractBox, "DayStorageContract");

        retrieveTextFieldState(csPreviousLocationField, "CubeStoragePreviousLocation");
        retrieveTextAreaState(csReasonForMoveTextArea, "CubeStorageReasonForMove");
        retrieveTextAreaState(csContainerDescriptionTextArea, "CubeStorageDescription");
        retrieveDateState(csStartDate, "CubeStorageStartDate");
        retrieveDateState(csExpirationDate, "CubeStorageExpirationDate");
        retrieveCheckboxState(csGuestNotifiedBox, "CubeStorageGuestNotified");

        retrieveTextFieldState(medlockerNumberField, "MediumLockerNumber");
        retrieveTextFieldState(medlockerAccommodationLinkField, "MediumLockerAccommodationLink");
        retrieveDateState(medlockerStartDate, "MediumLockerStartDate");
        retrieveDateState(medlockerLastAccessedDate, "MediumLockerLastAccessedDate");
        retrieveTextAreaState(medlockerNotesTextArea, "MediumLockerNotes");
        retrieveTextFieldState(medlockerAssigningStaffField, "MediumLockerAssigningStaff");
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