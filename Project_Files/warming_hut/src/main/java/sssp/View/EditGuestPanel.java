package sssp.View;

import javax.swing.*;
import java.awt.*;
import java.util.HexFormat;

public class EditGuestPanel {

    private static final PopupFactory popupFactory = new PopupFactory();
    public static final int WIDTH = 400;
    public static final Font NORMAL_FONT = new Font("Serif", Font.PLAIN, 17);

    public static final JPanel editGuestPanel = new JPanel(new FlowLayout());
    public static final JLabel title = new JLabel("Guest Overview");

    public static JPanel getEditGuestPanel(JPanel mainPanel){
        //This is testing for the Edit Guest panel:

        title.setFont(new Font("Serif", Font.PLAIN, 32));

        editGuestPanel.setPreferredSize(new Dimension(WIDTH, mainPanel.getHeight()));

        editGuestPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        editGuestPanel.add(title);
        editGuestPanel.add(getGuestDetailsPanel());
        editGuestPanel.add(getIssueTrackerPanel());
        editGuestPanel.add(getStorageTrackerPanel());
        return editGuestPanel;
    }

    public static final JLabel nameField = new JLabel();
    public static final JLabel nameLabel = new JLabel("Guest Name:");
    public static final JLabel lastCheckedInLabel = new JLabel("Last Checked In: ");
    public static final JLabel lastCheckedIn = new JLabel("");
    private static JPanel getGuestDetailsPanel(){
        nameField.setText("John Doe");
        nameLabel.setLabelFor(nameField);

        lastCheckedIn.setText("3/13/2024");
        lastCheckedInLabel.setLabelFor(lastCheckedIn);

        nameLabel.setFont(NORMAL_FONT);
        lastCheckedInLabel.setFont(NORMAL_FONT);
        lastCheckedIn.setFont(NORMAL_FONT);
        nameField.setFont(NORMAL_FONT);

        JPanel guestDetailsPanel = new JPanel(new GridBagLayout());
        guestDetailsPanel.setPreferredSize(new Dimension(WIDTH, 200));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,20,5,20);
        c.gridx = 0;
        c.gridy = 0;
        guestDetailsPanel.add(nameLabel, c);
        c.gridx = 1;
        c.ipadx = 100;
        guestDetailsPanel.add(nameField, c);
        return guestDetailsPanel;
    }


    public static final JLabel issueTrackerTitle = new JLabel("  Issues");

    public static final JLabel noTrespassLabel = new JLabel("No Trespass: ");

    public static final JLabel noTrespass = new JLabel("All HRDC");
    public static final JButton noTrespassDetails = new JButton("Details");

    public static final JLabel suspensionsLabel = new JLabel("Suspensions: ");
    public static final JLabel suspensions = new JLabel("1 Suspension");
    public static final JButton suspensionDetails = new JButton("Details");
    public static final JLabel warningLabel = new JLabel("Warnings: ");
    public static final JLabel warnings = new JLabel("2 Warnings");
    public static final JButton warningDetails = new JButton("Details");



    public static final JButton newIssueButton = new JButton("New Issue");
    private static JPanel getIssueTrackerPanel(){
        issueTrackerTitle.setFont(new Font("Serif", Font.PLAIN, 24));
        issueTrackerTitle.setBackground(Color.LIGHT_GRAY);
        issueTrackerTitle.setPreferredSize(new Dimension(WIDTH, 30));
        issueTrackerTitle.setOpaque(true);

        warnings.setBackground(Color.yellow);
        warnings.setOpaque(true);
        warnings.setHorizontalAlignment(0);
        warnings.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        warnings.setPreferredSize(new Dimension(120, 20));

        suspensions.setBackground(Color.orange);
        suspensions.setOpaque(true);
        suspensions.setHorizontalAlignment(0);
        suspensions.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        suspensions.setPreferredSize(new Dimension(120, 20));

        noTrespass.setBackground(Color.RED);
        noTrespass.setOpaque(true);
        noTrespass.setHorizontalAlignment(0);
        noTrespass.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        noTrespass.setPreferredSize(new Dimension(120, 20));

        noTrespassDetails.setPreferredSize(new Dimension(100, 20));
        suspensionDetails.setPreferredSize(new Dimension(100, 20));
        warningDetails.setPreferredSize(new Dimension(100, 20));
        newIssueButton.setPreferredSize(new Dimension(100, 20));

        newIssueButton.addActionListener(e -> {
            NewIssuePopup.ShowNewIssuePopup();
        });
        noTrespassDetails.addActionListener(e -> {
            IssueDetailsPopup.getNoTrespassDetailsPopup();
        });
        suspensionDetails.addActionListener(e -> {
            IssueDetailsPopup.getSuspensionsDetailsPopup();
        });
        warningDetails.addActionListener(e -> {
            IssueDetailsPopup.getWarningDetailsPopup();
        });

        warningLabel.setFont(NORMAL_FONT);
        warnings.setFont(NORMAL_FONT);
        warningDetails.setFont(NORMAL_FONT);
        suspensionsLabel.setFont(NORMAL_FONT);
        suspensions.setFont(NORMAL_FONT);
        suspensionDetails.setFont(NORMAL_FONT);
        noTrespassLabel.setFont(NORMAL_FONT);
        noTrespass.setFont(NORMAL_FONT);
        noTrespassDetails.setFont(NORMAL_FONT);

        JPanel issueTrackerPanel = new JPanel(new GridBagLayout());
        issueTrackerPanel.setPreferredSize(new Dimension(WIDTH, 200));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        issueTrackerPanel.add(newIssueButton, c);
        c.gridx = 0;
        c.gridwidth = 3;
        issueTrackerPanel.add(issueTrackerTitle, c);
        c.insets = new Insets(5, 10, 5, 10);
        c.gridwidth = 1;

        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(noTrespassLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(noTrespass, c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.2;
        issueTrackerPanel.add(noTrespassDetails, c);

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(suspensionsLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(suspensions, c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.2;
        issueTrackerPanel.add(suspensionDetails, c);

        c.gridy = 3;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(warningLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(warnings, c);
        c.weightx = 0.2;
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        issueTrackerPanel.add(warningDetails, c);

        return issueTrackerPanel;
    }












    public static final JLabel storageTrackerTitle = new JLabel("  Storage");

    public static final JLabel reservedLegendLabel = new JLabel("Reserved is blue ");

    public static final JLabel dayStorageLabel = new JLabel("Day Storage: ");

    public static final JLabel dayStorage = new JLabel("A12");
    public static final JButton dayStorageDetails = new JButton("Details");

    public static final JLabel smallLockerLabel = new JLabel("Small Locker: ");
    public static final JLabel smallLocker = new JLabel("#7");
    public static final JButton smallLockerDetails = new JButton("Details");
    public static final JLabel mediumLockerLabel = new JLabel("Medium Locker: ");
    public static final JLabel mediumLocker = new JLabel("None");
    public static final JButton mediumLockerDetails = new JButton("Details");
    public static final JButton openStorageAssignment = new JButton("Storage Assignment");
    private static JPanel getStorageTrackerPanel(){
        storageTrackerTitle.setFont(new Font("Serif", Font.PLAIN, 24));
        storageTrackerTitle.setBackground(Color.LIGHT_GRAY);
        storageTrackerTitle.setPreferredSize(new Dimension(WIDTH, 30));
        storageTrackerTitle.setOpaque(true);

        dayStorage.setBackground(Color.green);
        dayStorage.setOpaque(true);
        dayStorage.setHorizontalAlignment(0);
        dayStorage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        dayStorage.setPreferredSize(new Dimension(120, 20));

        smallLocker.setBackground(Color.CYAN);
        smallLocker.setOpaque(true);
        smallLocker.setHorizontalAlignment(0);
        smallLocker.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        smallLocker.setPreferredSize(new Dimension(120, 20));

        mediumLocker.setBackground(Color.white);
        mediumLocker.setOpaque(true);
        mediumLocker.setHorizontalAlignment(0);
        mediumLocker.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        mediumLocker.setPreferredSize(new Dimension(120, 20));

        dayStorageDetails.setPreferredSize(new Dimension(100, 20));
        smallLockerDetails.setPreferredSize(new Dimension(100, 20));
        mediumLockerDetails.setPreferredSize(new Dimension(100, 20));
        openStorageAssignment.setPreferredSize(new Dimension(100, 20));

        openStorageAssignment.addActionListener(e -> {
            NewIssuePopup.ShowNewIssuePopup();
        });
        mediumLockerDetails.addActionListener(e -> {
            IssueDetailsPopup.getNoTrespassDetailsPopup();
        });
        smallLockerDetails.addActionListener(e -> {
            IssueDetailsPopup.getSuspensionsDetailsPopup();
        });
        dayStorageDetails.addActionListener(e -> {
            IssueDetailsPopup.getWarningDetailsPopup();
        });

        dayStorageLabel.setFont(NORMAL_FONT);
        dayStorage.setFont(NORMAL_FONT);
        dayStorageDetails.setFont(NORMAL_FONT);
        smallLockerLabel.setFont(NORMAL_FONT);
        smallLocker.setFont(NORMAL_FONT);
        smallLockerDetails.setFont(NORMAL_FONT);
        mediumLockerLabel.setFont(NORMAL_FONT);
        mediumLocker.setFont(NORMAL_FONT);
        mediumLockerDetails.setFont(NORMAL_FONT);
        reservedLegendLabel.setFont(NORMAL_FONT);
        reservedLegendLabel.setForeground(Color.CYAN);
        reservedLegendLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel issueTrackerPanel = new JPanel(new GridBagLayout());
        issueTrackerPanel.setPreferredSize(new Dimension(WIDTH, 200));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        issueTrackerPanel.add(openStorageAssignment, c);
        c.gridx = 0;
        c.gridwidth = 3;
        issueTrackerPanel.add(storageTrackerTitle, c);
        c.insets = new Insets(5, 10, 5, 10);

        c.gridy = 1;
        issueTrackerPanel.add(reservedLegendLabel, c);

        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(dayStorageLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(dayStorage, c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.2;
        issueTrackerPanel.add(dayStorageDetails, c);

        c.gridy = 3;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(smallLockerLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(smallLocker, c);
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0.2;
        issueTrackerPanel.add(smallLockerDetails, c);

        c.gridy = 4;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0.2;
        issueTrackerPanel.add(mediumLockerLabel, c);
        c.gridx = 1;
        c.weightx = 0.6;
        issueTrackerPanel.add(mediumLocker, c);
        c.weightx = 0.2;
        c.gridx = 2;
        c.anchor = GridBagConstraints.EAST;
        issueTrackerPanel.add(mediumLockerDetails, c);

        return issueTrackerPanel;
    }
}
