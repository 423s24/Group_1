package sssp.View;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;

public class NewIssuePopup {

    public static int WIDTH = 600;
    public static int HEIGHT = 500;

    public static final JLabel guestNameLabel = new JLabel("Guest Name: John Doe");

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

        JButton newWarningBtn = new JButton("New Warning");
        JButton newSuspensionBtn = new JButton("New Suspension");
        JButton newTrespassBtn = new JButton("New No Trespass");
        newWarningBtn.setFont(new Font("Serif", Font.PLAIN,18));
        newSuspensionBtn.setFont(new Font("Serif", Font.PLAIN,18));
        newTrespassBtn.setFont(new Font("Serif", Font.PLAIN,18));

        JPanel btnTabs = new JPanel(new GridLayout(1,3));
        btnTabs.add(newWarningBtn);
        btnTabs.add(newSuspensionBtn);
        btnTabs.add(newTrespassBtn);

        header.add(btnTabs, BorderLayout.SOUTH);
        popupFrame.add(header, BorderLayout.NORTH);

        CardLayout cardLayout = new CardLayout();
        JPanel newIssueCardPanel = new JPanel(cardLayout);

        JPanel newWarningPanel = getNewWarningPanel();
        JPanel newSuspensionPanel = new JPanel(new GridBagLayout());
        JPanel newTrespassPanel = new JPanel(new GridBagLayout());

        newIssueCardPanel.add(newWarningPanel, "NewWarning");
        newIssueCardPanel.add(newSuspensionPanel, "NewSuspension");
        newIssueCardPanel.add(newTrespassPanel, "NewTrespass");

        popupFrame.add(newIssueCardPanel, BorderLayout.CENTER);
    }


    private static final JLabel dateLabel = new JLabel("Warning Date: ");
    private static final JDateChooser dateChooser = new JDateChooser();
    private static final JLabel staffInitialsLabel = new JLabel("Staff Initials: ");
    private static final JTextField staffInitials = new JTextField();

    private static final JLabel warningNotesLabel = new JLabel("Staff Initials: ");
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
}
