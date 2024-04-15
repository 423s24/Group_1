package sssp.View;

import sssp.Helper.DBConnectorV2Singleton;
import sssp.View.components.*;
import sssp.Helper.DBConnectorV2;
import sssp.Model.GuestDBKeys;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;

import java.awt.*;
import java.util.Map;

public class GuestDetailsPanel extends JPanel {
    private JLabel guestNameLabel;
    private JTextField searchField;
    private JButton searchButton;

    // data fields in the Basic Guest Data panel
    private DBSyncedTextField firstNameField;
    private DBSyncedTextField lastNameField;
    private DBSyncedTextArea notesTextArea;
    private DBSyncedDateChooser guestSinceDate;
    private DBSyncedDateChooser lastVisitDate;

    // data fields in the Guest Info Checks panel
    private DBSyncedCheckBox caseCheckBox;
    private DBSyncedCheckBox HMISCheckBox;
    private DBSyncedCheckBox sleepingBagBox;
    private DBSyncedDateChooser sleepingBagDate;
    private DBSyncedCheckBox tentBox;
    private DBSyncedDateChooser tentDate;
    private DBSyncedCheckBox backpackBox;
    private DBSyncedDateChooser backpackDate;
    private DBSyncedCheckBox outreachBackpackBox;
    private DBSyncedDateChooser outreachBackpackDate;
    private DBSyncedCheckBox sleepingPadBox;
    private DBSyncedDateChooser sleepingPadDate;

    // data fields in the Storage Info panel

    // small lockers
    private DBSyncedTextField smLockerLockerNumberField;
    private DBSyncedDateChooser smLockerStartDate;
    private DBSyncedDateChooser smLockerLastAccessedDate;
    private DBSyncedTextArea smLockerNotesTextArea;
    private DBSyncedTextField smLockerAssigningStaffField;

    // day storage
    private DBSyncedTextField dayStorageShelfField;
    private DBSyncedTextField dayStorageSlotField;
    private DBSyncedDateChooser dayStorageStartDate;
    private DBSyncedDateChooser dayStorageExpirationDate;
    private DBSyncedTextArea dayStorageContainerDescriptionTextArea;
    private DBSyncedTextField dayStorageStaffInitialsField;
    private DBSyncedCheckBox dayStorageContractBox;

    // cube storage
    private DBSyncedTextField csPreviousLocationField;
    private DBSyncedTextArea csReasonForMoveTextArea;
    private DBSyncedTextArea csContainerDescriptionTextArea;
    private DBSyncedDateChooser csStartDate;
    private DBSyncedDateChooser csExpirationDate;
    private DBSyncedCheckBox csGuestNotifiedBox;

    // medium lockers
    private DBSyncedTextField medlockerNumberField;
    private DBSyncedTextField medlockerAccommodationLinkField;
    private DBSyncedDateChooser medlockerStartDate;
    private DBSyncedDateChooser medlockerLastAccessedDate;
    private DBSyncedTextArea medlockerNotesTextArea;
    private DBSyncedTextField medlockerAssigningStaffField;

    // disciplinary info panel
    private DisciplinaryInfoPanel disciplinaryInfoPanel;

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

        disciplinaryInfoPanel = new DisciplinaryInfoPanel();
        disciplinaryInfoPanel.setBorder(regBorder);

        JPanel storageInfoPanel = new JPanel();
        storageInfoPanel.setLayout(new BorderLayout());
        JLabel storageInfoLabel = new JLabel("Storage Info");
        storageInfoPanel.add(storageInfoLabel, BorderLayout.NORTH);
        storageInfoPanel.setBorder(regBorder);

        add(topBarPanel, BorderLayout.NORTH);

        add(disciplinaryInfoPanel, BorderLayout.CENTER);

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
        caseCheckBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, "Caseworthy");
        infoChecksPanel.add(caseCheckBox, gbc);

        gbc.gridx++;
        JLabel HMISCheckLabel = new JLabel("HMIS");
        infoChecksPanel.add(HMISCheckLabel, gbc);

        gbc.gridx++;
        HMISCheckBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.HMIS_CHECK.getKey());
        infoChecksPanel.add(HMISCheckBox, gbc);

        addVertSeparator(infoChecksPanel, gbc);

        gbc.gridx++;
        JLabel sleepingBagLabel = new JLabel("Sleeping Bag");
        infoChecksPanel.add(sleepingBagLabel, gbc);

        gbc.gridx++;
        sleepingBagBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.SLEEPING_BAG_CHECK.getKey());
        infoChecksPanel.add(sleepingBagBox, gbc);

        gbc.gridx++;
        sleepingBagDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.SLEEPING_BAG_DATE.getKey());
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
        tentBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.TENT_CHECK.getKey());
        infoChecksPanel.add(tentBox, gbc);

        gbc.gridx++;
        tentDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.TENT_DATE.getKey());
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
        backpackBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.BACKPACK_CHECK.getKey());
        infoChecksPanel.add(backpackBox, gbc);

        gbc.gridx++;
        backpackDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.BACKPACK_DATE.getKey());
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
        outreachBackpackBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.OUTREACH_BACKPACK_CHECK.getKey());
        infoChecksPanel.add(outreachBackpackBox, gbc);

        gbc.gridx++;
        outreachBackpackDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.OUTREACH_BACKPACK_DATE.getKey());
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
        sleepingPadBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.SLEEPING_PAD_CHECK.getKey());
        infoChecksPanel.add(sleepingPadBox, gbc);

        gbc.gridx++;
        sleepingPadDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.SLEEPING_PAD_DATE.getKey());
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
        dayStorageShelfField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_SHELF.getKey());
        panel.add(dayStorageShelfField, gbc);

        gbc.gridy++;
        JLabel slotLabel = new JLabel("Slot:");
        panel.add(slotLabel, gbc);

        gbc.gridy++;
        dayStorageSlotField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_SLOT.getKey());
        panel.add(dayStorageSlotField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel dayStorageStartDateLabel = new JLabel("Start Date:");
        panel.add(dayStorageStartDateLabel, gbc);

        gbc.gridy++;
        dayStorageStartDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_START_DATE.getKey());
        dayStorageStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(dayStorageStartDate, gbc);

        gbc.gridy++;
        JLabel expirationDateLabel = new JLabel("Expiration Date:");
        panel.add(expirationDateLabel, gbc);

        gbc.gridy++;
        dayStorageExpirationDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_EXPIRATION_DATE.getKey());
        dayStorageExpirationDate.setDateFormatString("MM/dd/yyyy");
        panel.add(dayStorageExpirationDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel containerDescriptionLabel = new JLabel("Container Description:");
        panel.add(containerDescriptionLabel, gbc);

        gbc.gridy++;
        dayStorageContainerDescriptionTextArea = new DBSyncedTextArea(5, 20, db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_CONTAINER_DESCRIPTION.getKey());
        dayStorageContainerDescriptionTextArea.setLineWrap(true);
        dayStorageContainerDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(dayStorageContainerDescriptionTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel staffInitialsLabel = new JLabel("Staff Initials:");
        panel.add(staffInitialsLabel, gbc);

        gbc.gridy++;
        dayStorageStaffInitialsField = new DBSyncedTextField(5, db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_STAFF_INITIALS.getKey());
        panel.add(dayStorageStaffInitialsField, gbc);

        gbc.gridy++;
        JLabel contractLabel = new JLabel("Contract Signed?");
        panel.add(contractLabel, gbc);

        gbc.gridy++;
        dayStorageContractBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.DAY_STORAGE_CONTRACT_CHECK.getKey());
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
        medlockerNumberField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_NUMBER.getKey());
        panel.add(medlockerNumberField, gbc);

        gbc.gridy++;
        JLabel accommodationLinkLabel = new JLabel("Approved Accommodation Link:");
        panel.add(accommodationLinkLabel, gbc);

        gbc.gridy++;
        medlockerAccommodationLinkField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_ACCOMMODATION_LINK.getKey());
        panel.add(medlockerAccommodationLinkField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel startDateLabel = new JLabel("Start Date:");
        panel.add(startDateLabel, gbc);

        gbc.gridy++;
        medlockerStartDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_START_DATE.getKey());
        medlockerStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(medlockerStartDate, gbc);

        gbc.gridy++;
        JLabel lastAccessedLabel = new JLabel("Date Last Accessed:");
        panel.add(lastAccessedLabel, gbc);

        gbc.gridy++;
        medlockerLastAccessedDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_LAST_ACCESSED_DATE.getKey());
        medlockerLastAccessedDate.setDateFormatString("MM/dd/yyyy");
        panel.add(medlockerLastAccessedDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        panel.add(notesLabel, gbc);

        gbc.gridy++;
        medlockerNotesTextArea = new DBSyncedTextArea(5, 20, db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_NOTES.getKey());
        medlockerNotesTextArea.setLineWrap(true);
        medlockerNotesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(medlockerNotesTextArea);
        panel.add(notesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel assigningStaffLabel = new JLabel("Assigning Staff Member:");
        panel.add(assigningStaffLabel, gbc);

        gbc.gridy++;
        medlockerAssigningStaffField = new DBSyncedTextField(5, db.database.guests, this.activeGuestID, GuestDBKeys.MED_LOCKER_ASSIGNING_STAFF.getKey());
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
        smLockerLockerNumberField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.SM_LOCKER_NUMBER.getKey());
        panel.add(smLockerLockerNumberField, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerStartDateLabel = new JLabel("Start Date:");
        panel.add(smLockerStartDateLabel, gbc);

        gbc.gridy++;
        smLockerStartDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.SM_LOCKER_START_DATE.getKey());
        smLockerStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(smLockerStartDate, gbc);

        gbc.gridy++;
        JLabel smLockerLastAccessedLabel = new JLabel("Date Last Accessed:");
        panel.add(smLockerLastAccessedLabel, gbc);

        gbc.gridy++;
        smLockerLastAccessedDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.SM_LOCKER_LAST_ACCESSED_DATE.getKey());
        smLockerLastAccessedDate.setDateFormatString("MM/dd/yyyy");
        panel.add(smLockerLastAccessedDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerNotesLabel = new JLabel("Notes:");
        panel.add(smLockerNotesLabel, gbc);

        gbc.gridy++;
        smLockerNotesTextArea = new DBSyncedTextArea(5, 20, db.database.guests, this.activeGuestID, GuestDBKeys.SM_LOCKER_NOTES.getKey());
        smLockerNotesTextArea.setLineWrap(true);
        smLockerNotesTextArea.setWrapStyleWord(true);
        JScrollPane smLockerNotesScrollPane = new JScrollPane(smLockerNotesTextArea);
        panel.add(smLockerNotesScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel smLockerAssigningStaffLabel = new JLabel("Assigning Staff Member:");
        panel.add(smLockerAssigningStaffLabel, gbc);

        gbc.gridy++;
        smLockerAssigningStaffField = new DBSyncedTextField(5, db.database.guests, this.activeGuestID, GuestDBKeys.SM_LOCKER_ASSIGNING_STAFF.getKey());
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
        csPreviousLocationField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.CS_PREVIOUS_LOCATION.getKey());
        panel.add(csPreviousLocationField, gbc);

        gbc.gridy++;
        JLabel csReasonForMoveLabel = new JLabel("Reason for Move:");
        panel.add(csReasonForMoveLabel, gbc);

        gbc.gridy++;
        csReasonForMoveTextArea = new DBSyncedTextArea(5, 20, db.database.guests, this.activeGuestID, GuestDBKeys.CS_REASON_FOR_MOVE.getKey());
        csReasonForMoveTextArea.setLineWrap(true);
        csReasonForMoveTextArea.setWrapStyleWord(true);
        JScrollPane csReasonForMoveScrollPane = new JScrollPane(csReasonForMoveTextArea);
        panel.add(csReasonForMoveScrollPane, gbc);

        gbc.gridy++;
        JLabel csContainerDescriptionLabel = new JLabel("Container Description:");
        panel.add(csContainerDescriptionLabel, gbc);

        gbc.gridy++;
        csContainerDescriptionTextArea = new DBSyncedTextArea(5, 20, db.database.guests, this.activeGuestID, GuestDBKeys.CS_CONTAINER_DESCRIPTION.getKey());
        csContainerDescriptionTextArea.setLineWrap(true);
        csContainerDescriptionTextArea.setWrapStyleWord(true);
        JScrollPane csContainerDescriptionScrollPane = new JScrollPane(csContainerDescriptionTextArea);
        panel.add(csContainerDescriptionScrollPane, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel csStartDateLabel = new JLabel("Start Date:");
        panel.add(csStartDateLabel, gbc);

        gbc.gridy++;
        csStartDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.CS_START_DATE.getKey());
        csStartDate.setDateFormatString("MM/dd/yyyy");
        panel.add(csStartDate, gbc);

        gbc.gridy++;
        JLabel csExpirationDateLabel = new JLabel("Expiration Date:");
        panel.add(csExpirationDateLabel, gbc);

        gbc.gridy++;
        csExpirationDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.CS_EXPIRATION_DATE.getKey());
        csExpirationDate.setDateFormatString("MM/dd/yyyy");
        panel.add(csExpirationDate, gbc);

        addSeparator(panel, gbc);

        gbc.gridy++;
        JLabel csGuestNotifiedLabel = new JLabel("Guest Notified?");
        panel.add(csGuestNotifiedLabel, gbc);

        gbc.gridy++;
        csGuestNotifiedBox = new DBSyncedCheckBox(db.database.guests, this.activeGuestID, GuestDBKeys.CS_GUEST_NOTIFIED_CHECK.getKey());
        panel.add(csGuestNotifiedBox, gbc);

        return panel;
    }

    private void addBasicInfo() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel basicDataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 20, 5, 20);

        JLabel firstNameLabel = new JLabel("First Name:");
        basicDataPanel.add(firstNameLabel, gbc);

        gbc.gridy++;
        firstNameField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.FIRST_NAME.getKey());
        basicDataPanel.add(firstNameField, gbc);

        gbc.gridy++;
        JLabel lastNameLabel = new JLabel("Last Name:");
        basicDataPanel.add(lastNameLabel, gbc);

        gbc.gridy++;
        lastNameField = new DBSyncedTextField(20, db.database.guests, this.activeGuestID, GuestDBKeys.LAST_NAME.getKey());
        basicDataPanel.add(lastNameField, gbc);

        addSeparator(basicDataPanel, gbc);

        gbc.gridy++;
        JLabel firstHousedLabel = new JLabel("First Housed On:");
        basicDataPanel.add(firstHousedLabel, gbc);

        gbc.gridy++;
        guestSinceDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.GUEST_SINCE_DATE.getKey());
        guestSinceDate.setDateFormatString("MM/dd/yyyy");
        basicDataPanel.add(guestSinceDate, gbc);

        gbc.gridy++;
        JLabel lastVisitLabel = new JLabel("Last Visit:");
        basicDataPanel.add(lastVisitLabel, gbc);

        gbc.gridy++;
        lastVisitDate = new DBSyncedDateChooser(db.database.guests, this.activeGuestID, GuestDBKeys.LAST_VISIT_DATE.getKey());
        lastVisitDate.setDateFormatString("MM/dd/yyyy");
        basicDataPanel.add(lastVisitDate, gbc);

        addSeparator(basicDataPanel, gbc);

        gbc.gridy++;
        JLabel notesLabel = new JLabel("Notes:");
        basicDataPanel.add(notesLabel, gbc);

        gbc.gridy++;
        notesTextArea = new DBSyncedTextArea(10, 20, db.database.guests, this.activeGuestID, GuestDBKeys.NOTES.getKey());
        notesTextArea.setLineWrap(true);
        notesTextArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesTextArea);
        basicDataPanel.add(notesScrollPane, gbc);

        //basicDataPanel.setBorder(BorderFactory.createTitledBorder("Basic Guest Data"));

        tabbedPane.addTab("Basic Guest Data", basicDataPanel);
        tabbedPane.addTab("Bunk Info", BunkReservationsPanel.getBunkReservationsPanel(this.activeGuestID));

        add(tabbedPane, BorderLayout.WEST);
    }

    public boolean setActiveGuestID(String guestID) {
        this.activeGuestID = guestID;

        this.activeGuestData = db.database.guests.get(guestID);

        if(activeGuestData == null) {
            // Show error window
            JOptionPane.showMessageDialog(null, "Error: The guest " + guestID + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        disciplinaryInfoPanel.setActiveGuestID(guestID);

        onActiveGuestChanged();

        return true;
    }

    private void onActiveGuestChanged() {
        firstNameField.setObjKey(activeGuestID);
        lastNameField.setObjKey(activeGuestID);
        notesTextArea.setObjKey(activeGuestID);
        guestSinceDate.setObjKey(activeGuestID);
        lastVisitDate.setObjKey(activeGuestID);

        caseCheckBox.setObjKey(activeGuestID);
        HMISCheckBox.setObjKey(activeGuestID);
        sleepingBagBox.setObjKey(activeGuestID);
        tentBox.setObjKey(activeGuestID);
        backpackBox.setObjKey(activeGuestID);
        outreachBackpackBox.setObjKey(activeGuestID);
        sleepingPadBox.setObjKey(activeGuestID);

        smLockerLockerNumberField.setObjKey(activeGuestID);
        smLockerStartDate.setObjKey(activeGuestID);
        smLockerLastAccessedDate.setObjKey(activeGuestID);
        smLockerNotesTextArea.setObjKey(activeGuestID);
        smLockerAssigningStaffField.setObjKey(activeGuestID);

        dayStorageShelfField.setObjKey(activeGuestID);
        dayStorageSlotField.setObjKey(activeGuestID);
        dayStorageStartDate.setObjKey(activeGuestID);
        dayStorageExpirationDate.setObjKey(activeGuestID);
        dayStorageContainerDescriptionTextArea.setObjKey(activeGuestID);
        dayStorageStaffInitialsField.setObjKey(activeGuestID);
        dayStorageContractBox.setObjKey(activeGuestID);

        csPreviousLocationField.setObjKey(activeGuestID);
        csReasonForMoveTextArea.setObjKey(activeGuestID);
        csContainerDescriptionTextArea.setObjKey(activeGuestID);
        csStartDate.setObjKey(activeGuestID);
        csExpirationDate.setObjKey(activeGuestID);
        csGuestNotifiedBox.setObjKey(activeGuestID);

        medlockerNumberField.setObjKey(activeGuestID);
        medlockerAccommodationLinkField.setObjKey(activeGuestID);
        medlockerStartDate.setObjKey(activeGuestID);
        medlockerLastAccessedDate.setObjKey(activeGuestID);
        medlockerNotesTextArea.setObjKey(activeGuestID);
        medlockerAssigningStaffField.setObjKey(activeGuestID);

        BunkReservationsPanel.setObjKey(activeGuestID);
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
        onActiveGuestChanged();
    }
}