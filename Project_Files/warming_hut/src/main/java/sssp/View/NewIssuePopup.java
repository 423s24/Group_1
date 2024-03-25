package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NewIssuePopup {

    public static int WIDTH = 600;
    public static int HEIGHT = 500;

    public static final JLabel guestNameLabel = new JLabel("Guest Name: John Doe");
    public static final CardLayout cardLayout = new CardLayout();
    public static JButton activeButton;

    public static void ShowNewIssuePopup(){
        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new BorderLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setMaximumSize(popupFrame.getSize());
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);
        popupFrame.setTitle("Create New Issue");

        JPanel header = new JPanel(new BorderLayout());

        guestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        guestNameLabel.setBackground(Color.lightGray);
        guestNameLabel.setOpaque(true);

        header.add(guestNameLabel, BorderLayout.NORTH);
        JPanel newIssueCardPanel = new JPanel(cardLayout);

        JButton newWarningBtn = new JButton("New Warning");
        JButton newSuspensionBtn = new JButton("New Suspension");
        JButton newTrespassBtn = new JButton("New No Trespass");
        newWarningBtn.setFont(new Font("Serif", Font.PLAIN,18));
        newSuspensionBtn.setFont(new Font("Serif", Font.PLAIN,18));
        newTrespassBtn.setFont(new Font("Serif", Font.PLAIN,18));

        activeButton = newWarningBtn;
        activeButton.setEnabled(false);
        newWarningBtn.addActionListener(switchTabActionListener(newIssueCardPanel, newWarningBtn, "NewWarning"));
        newSuspensionBtn.addActionListener(switchTabActionListener(newIssueCardPanel, newSuspensionBtn, "NewSuspension"));
        newTrespassBtn.addActionListener(switchTabActionListener(newIssueCardPanel, newTrespassBtn, "NewTrespass"));


        JPanel btnTabs = new JPanel(new GridLayout(1,3));
        btnTabs.add(newWarningBtn);
        btnTabs.add(newSuspensionBtn);
        btnTabs.add(newTrespassBtn);

        header.add(btnTabs, BorderLayout.SOUTH);
        popupFrame.add(header, BorderLayout.NORTH);



        JPanel newWarningPanel = getNewWarningPanel();
        JPanel newSuspensionPanel = getNewSuspensionPanel();
        JPanel newTrespassPanel = getNewNoTrespassPanel();

        newIssueCardPanel.add(newWarningPanel, "NewWarning");
        newIssueCardPanel.add(newSuspensionPanel, "NewSuspension");
        newIssueCardPanel.add(newTrespassPanel, "NewTrespass");

        popupFrame.add(newIssueCardPanel, BorderLayout.CENTER);
    }


    private static final JLabel dateLabel = new JLabel("Warning Date: ");
    private static final JDateChooser dateChooser = new JDateChooser();
    private static final JLabel staffInitialsLabel = new JLabel("Staff Initials: ");
    private static final JTextField staffInitials = new JTextField();

    private static final JLabel warningNotesLabel = new JLabel("Notes: ");
    private static final JTextArea warningNotes = new JTextArea();

    private static final JButton cancelBtn = new JButton("Cancel");
    private static final JButton submitBtn = new JButton("Submit");

    private static JPanel getNewWarningPanel(){
        JPanel newWarningPanel = new JPanel(new GridBagLayout());

        dateLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        staffInitialsLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        warningNotesLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        cancelBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        submitBtn.setFont(new Font("Serif", Font.PLAIN, 18));

        dateChooser.setPreferredSize(new Dimension(100, 30));
        staffInitials.setPreferredSize(new Dimension(100, 30));
        warningNotes.setPreferredSize(new Dimension(300, 150));
        cancelBtn.setPreferredSize(new Dimension(150, 40));
        submitBtn.setPreferredSize(new Dimension(150, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        newWarningPanel.add(dateLabel, c);
        c.gridx = 1;
        newWarningPanel.add(dateChooser, c);
        c.gridx = 0;
        c.gridy = 1;
        newWarningPanel.add(staffInitialsLabel, c);
        c.gridx = 1;
        newWarningPanel.add(staffInitials, c);
        c.gridx = 0;
        c.gridy = 2;
        newWarningPanel.add(warningNotesLabel, c);
        c.gridx = 1;
        newWarningPanel.add(warningNotes, c);
        c.gridx = 0;
        c.gridy = 3;
        newWarningPanel.add(cancelBtn, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        newWarningPanel.add(submitBtn, c);

        return newWarningPanel;
    }


    private static final JLabel suspensionDateLabel = new JLabel("Suspension Date: ");
    private static final JLabel expirationDateLabel = new JLabel("Expiration Date: ");
    private static final JDateChooser suspensionDateChooser = new JDateChooser();
    private static final JDateChooser expirationDateChooser = new JDateChooser();
    private static final JLabel suspensionNotesLabel = new JLabel("Notes: ");
    private static final JTextArea suspensionNotes = new JTextArea();
    private static final JLabel suspensionsStaffInitialsLabel = new JLabel("Staff Initials: ");
    private static final JTextField suspensionsStaffInitials = new JTextField();
    private static final String[] suspendedFromOptions = {"Bunking", "Storage", "Showers", "Laundry", "All"};
    private static final JLabel suspendedFromLabel = new JLabel("Suspended From: ");
    private static final JComboBox<String> suspendedFrom = new JComboBox<>(suspendedFromOptions);
    private static final JButton suspensionCancelBtn = new JButton("Cancel");
    private static final JButton suspensionSubmitBtn = new JButton("Submit");
    private static JPanel getNewSuspensionPanel(){
        JPanel newSuspensionPanel = new JPanel(new GridBagLayout());

        suspensionDateLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        expirationDateLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        suspensionsStaffInitialsLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        suspensionNotesLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        suspendedFromLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        suspensionCancelBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        suspensionSubmitBtn.setFont(new Font("Serif", Font.PLAIN, 18));

        suspensionDateChooser.setPreferredSize(new Dimension(100, 30));
        expirationDateChooser.setPreferredSize(new Dimension(100, 30));
        suspensionsStaffInitials.setPreferredSize(new Dimension(100, 30));
        suspensionNotes.setPreferredSize(new Dimension(300, 150));
        suspensionCancelBtn.setPreferredSize(new Dimension(150, 40));
        suspensionSubmitBtn.setPreferredSize(new Dimension(150, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        newSuspensionPanel.add(suspensionDateLabel, c);
        c.gridx = 1;
        newSuspensionPanel.add(suspensionDateChooser, c);
        c.gridx = 0;
        c.gridy = 1;
        newSuspensionPanel.add(expirationDateLabel, c);
        c.gridx = 1;
        newSuspensionPanel.add(expirationDateChooser, c);
        c.gridx = 0;
        c.gridy = 2;
        newSuspensionPanel.add(suspendedFromLabel, c);
        c.gridx = 1;
        newSuspensionPanel.add(suspendedFrom, c);
        c.gridx = 0;
        c.gridy = 3;
        newSuspensionPanel.add(suspensionsStaffInitialsLabel, c);
        c.gridx = 1;
        newSuspensionPanel.add(suspensionsStaffInitials, c);
        c.gridx = 0;
        c.gridy = 4;
        newSuspensionPanel.add(suspensionNotesLabel, c);
        c.gridx = 1;
        newSuspensionPanel.add(suspensionNotes, c);
        c.gridx = 0;
        c.gridy = 5;
        newSuspensionPanel.add(suspensionCancelBtn, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        newSuspensionPanel.add(suspensionSubmitBtn, c);

        return newSuspensionPanel;
    }

    private static final JLabel noTrespassDateLabel = new JLabel("Suspension Date: ");
    private static final JDateChooser noTrespassDateChooser = new JDateChooser();
    private static final JLabel noTrespassNotesLabel = new JLabel("Notes: ");
    private static final JTextArea noTrespassNotes = new JTextArea();
    private static final JLabel noTrespassStaffInitialsLabel = new JLabel("Staff Initials: ");
    private static final JTextField noTrespasssStaffInitials = new JTextField();
    private static final String[] noTrespassOptions = {"All HRDC", "Wheat Drive"};
    private static final JLabel noTrespassFromLabel = new JLabel("Suspended From: ");
    private static final JComboBox<String> noTrespassFrom = new JComboBox<>(noTrespassOptions);
    private static final JButton noTrespassCancelBtn = new JButton("Cancel");
    private static final JButton noTrespassSubmitBtn = new JButton("Submit");
    private static JPanel getNewNoTrespassPanel(){
        JPanel newNoTrespassPanel = new JPanel(new GridBagLayout());

        noTrespassDateLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        expirationDateLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        noTrespassStaffInitialsLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        noTrespassNotesLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        noTrespassFromLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        noTrespassCancelBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        noTrespassSubmitBtn.setFont(new Font("Serif", Font.PLAIN, 18));

        noTrespassDateChooser.setPreferredSize(new Dimension(100, 30));
        noTrespasssStaffInitials.setPreferredSize(new Dimension(100, 30));
        noTrespassNotes.setPreferredSize(new Dimension(300, 150));
        noTrespassCancelBtn.setPreferredSize(new Dimension(150, 40));
        noTrespassSubmitBtn.setPreferredSize(new Dimension(150, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5,5,5,5);
        newNoTrespassPanel.add(suspensionDateLabel, c);
        c.gridx = 1;
        newNoTrespassPanel.add(suspensionDateChooser, c);
        c.gridx = 0;
        c.gridy = 2;
        newNoTrespassPanel.add(noTrespassFromLabel, c);
        c.gridx = 1;
        newNoTrespassPanel.add(noTrespassFrom, c);
        c.gridx = 0;
        c.gridy = 3;
        newNoTrespassPanel.add(noTrespassStaffInitialsLabel, c);
        c.gridx = 1;
        newNoTrespassPanel.add(noTrespasssStaffInitials, c);
        c.gridx = 0;
        c.gridy = 4;
        newNoTrespassPanel.add(noTrespassNotesLabel, c);
        c.gridx = 1;
        newNoTrespassPanel.add(noTrespassNotes, c);
        c.gridx = 0;
        c.gridy = 5;
        newNoTrespassPanel.add(noTrespassCancelBtn, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        newNoTrespassPanel.add(noTrespassSubmitBtn, c);

        return newNoTrespassPanel;
    }

    private static ActionListener switchTabActionListener(JPanel mainPanel, JButton button, String panelName) {
        return e -> {
            CardLayout layout = (CardLayout)(mainPanel.getLayout());
            layout.show(mainPanel, panelName);
            activeButton.setEnabled(true); // Enable previously active button
            button.setEnabled(false); // Disable clicked button
            activeButton = button;
        };
    }
}
