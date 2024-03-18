package sssp.View;

import javax.swing.*;
import java.awt.*;

public class EditGuestPanel {

    private static final PopupFactory popupFactory = new PopupFactory();
    public static final int WIDTH = 400;
    public static final Font NORMAL_FONT = new Font("Serif", Font.PLAIN, 17);

    public static final JPanel editGuestPanel = new JPanel(new FlowLayout());
    public static final JLabel title = new JLabel("Guest Details");

    public static JPanel getEditGuestPanel(JPanel mainPanel){
        //This is testing for the Edit Guest panel:

        title.setFont(new Font("Serif", Font.PLAIN, 32));

        editGuestPanel.setPreferredSize(new Dimension(WIDTH, mainPanel.getHeight()));

        editGuestPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        editGuestPanel.add(title);
        editGuestPanel.add(getGuestDetailsPanel());
        editGuestPanel.add(getIssueTrackerPanel());
        return editGuestPanel;
    }

    public static final JTextField nameField = new JFormattedTextField();
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

    private static void issueDetailsClicked(){

    }
}
