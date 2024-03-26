package sssp.View;

import javax.swing.*;
import java.awt.*;

public class IssueDetailsPopup {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 750;
    public static final JLabel noTrespassLabel = new JLabel("No Trespass Orders");

    private static void showIssueDetailsPopup(String title, JPanel panel){
        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new GridBagLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);
        popupFrame.setTitle(title);

        GridBagConstraints frameC = new GridBagConstraints();
        frameC.fill = GridBagConstraints.HORIZONTAL;
        frameC.weightx = 1.0;
        frameC.weighty = 1.0;
        frameC.gridx = 0;
        frameC.gridy = 0;
        frameC.anchor = GridBagConstraints.NORTH;
        popupFrame.add(panel, frameC);
    }

    public static void getNoTrespassDetailsPopup(){
        showIssueDetailsPopup("No Trespass Details", getNoTrespassDetailsPanel());
    }
    public static void getSuspensionsDetailsPopup(){
        showIssueDetailsPopup("Suspension Details", getSuspensionsDetailsPanel());
    }

    public static void getWarningDetailsPopup(){
        showIssueDetailsPopup("Warning Details", getWarningDetailsPanel());
    }

    public static JPanel getNoTrespassDetailsPanel(){
        JPanel noTrespassDetailsPanel = new JPanel(new GridBagLayout());

        JLabel guestNameLabel = new JLabel("Guest Name:   ");
        JLabel guestName = new JLabel("John Doe");

        guestNameLabel.setBackground(Color.gray);
        guestNameLabel.setOpaque(true);
        guestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        guestName.setBackground(Color.gray);
        guestName.setOpaque(true);
        guestName.setFont(new Font("Serif", Font.PLAIN, 24));

        noTrespassLabel.setBackground(Color.lightGray);
        noTrespassLabel.setOpaque(true);
        noTrespassLabel.setFont(new Font("Serif", Font.PLAIN, 18));

        // Column Names
        String[] columnNames = { "Date of incident", "No Trespassed From", "Staff Initials", "BPD Status", "LBD Status", "CW Alert", "Notes" };

        // Data to be displayed in the JTable
        String[][] activeSuspensionData = {
                { "3/15/2024", "ALL HRDC", "RS", "Notified", "Not Notified", "Added", "Threatened to tickle me..."}
        };

        // Initializing the JTable
        JTable sixMonthWarningsTable = new JTable(activeSuspensionData, columnNames);
        sixMonthWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sixMonthWarningsTable.setRowHeight(50);
        sixMonthWarningsTable.getColumnModel().getColumn(0).setMaxWidth(250);
        sixMonthWarningsTable.getColumnModel().getColumn(1).setMaxWidth(250);
        sixMonthWarningsTable.getColumnModel().getColumn(2).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(3).setMaxWidth(100);
        sixMonthWarningsTable.getColumnModel().getColumn(4).setMaxWidth(100);
        sixMonthWarningsTable.getColumnModel().getColumn(5).setMaxWidth(100);
        sixMonthWarningsTable.getColumnModel().getColumn(6).setMinWidth(100);
        sixMonthWarningsTable.setBackground(Color.RED);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        noTrespassDetailsPanel.add(guestNameLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        noTrespassDetailsPanel.add(guestName, c);
        c.weightx = 0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        noTrespassDetailsPanel.add(noTrespassLabel, c);
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 2;
        noTrespassDetailsPanel.add(sixMonthWarningsTable.getTableHeader(), c);
        c.gridy = 3;
        noTrespassDetailsPanel.add(sixMonthWarningsTable, c);

        return noTrespassDetailsPanel;
    }

    public static JPanel getSuspensionsDetailsPanel(){
        JPanel suspensionsDetailsPanel = new JPanel(new GridBagLayout());

        JLabel guestNameLabel = new JLabel("Guest Name:   ");
        JLabel guestName = new JLabel("John Doe");
        JLabel activeSuspensionsLabel = new JLabel("  Active Suspensions");
        JLabel olderSuspensionsLabel = new JLabel("  Expired Suspensions");

        guestNameLabel.setBackground(Color.gray);
        guestNameLabel.setOpaque(true);
        guestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        guestName.setBackground(Color.gray);
        guestName.setOpaque(true);
        guestName.setFont(new Font("Serif", Font.PLAIN, 24));

        activeSuspensionsLabel.setBackground(Color.lightGray);
        activeSuspensionsLabel.setOpaque(true);
        activeSuspensionsLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        olderSuspensionsLabel.setBackground(Color.lightGray);
        olderSuspensionsLabel.setOpaque(true);
        olderSuspensionsLabel.setFont(new Font("Serif", Font.PLAIN, 18));

        // Column Names
        String[] columnNames = { "Date Issued", "Expiration", "Service Suspended", "Staff Initials", "Notes" };

        // Data to be displayed in the JTable
        String[][] activeSuspensionData = {
                { "3/15/2024", "3/15/2025", "Nightly Bunking", "RS", "Refused to leave after 5 warnings" }
        };

        // Initializing the JTable
        JTable activeSuspensionsTable = new JTable(activeSuspensionData, columnNames);
        activeSuspensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        activeSuspensionsTable.setRowHeight(50);
        activeSuspensionsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        activeSuspensionsTable.getColumnModel().getColumn(1).setMaxWidth(100);
        activeSuspensionsTable.getColumnModel().getColumn(2).setMaxWidth(275);
        activeSuspensionsTable.getColumnModel().getColumn(3).setMaxWidth(75);
        activeSuspensionsTable.getColumnModel().getColumn(4).setMinWidth(100);
        activeSuspensionsTable.setBackground(Color.ORANGE);

        // Data to be displayed in the JTable
        String[][] olderData = {
                { "3/13/2023", "3/13/2024", "Showers", "RS", "Harassed other guests" },
                { "1/20/2023", "8/22/2023", "Storage", "RS", "Would not remove his items" }
        };

        // Initializing the JTable
        JTable olderSuspensionsTable = new JTable(olderData, columnNames);
        olderSuspensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderSuspensionsTable.setRowHeight(50);
        olderSuspensionsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(1).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(2).setMaxWidth(200);
        olderSuspensionsTable.getColumnModel().getColumn(3).setMaxWidth(75);
        olderSuspensionsTable.getColumnModel().getColumn(4).setMinWidth(100);
        olderSuspensionsTable.setBackground(Color.ORANGE);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        suspensionsDetailsPanel.add(guestNameLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        suspensionsDetailsPanel.add(guestName, c);
        c.weightx = 0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        suspensionsDetailsPanel.add(activeSuspensionsLabel, c);
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 2;
        suspensionsDetailsPanel.add(activeSuspensionsTable.getTableHeader(), c);
        c.gridy = 3;
        suspensionsDetailsPanel.add(activeSuspensionsTable, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.weightx = 0;
        suspensionsDetailsPanel.add(olderSuspensionsLabel, c);
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 5;
        suspensionsDetailsPanel.add(olderSuspensionsTable.getTableHeader(), c);
        c.gridy = 6;
        suspensionsDetailsPanel.add(olderSuspensionsTable, c);

        return suspensionsDetailsPanel;
    }

    public static final JLabel lastSixMonthsLabel = new JLabel("Last 6 Months");
    public static final JLabel lastSixMonthsTotalLabel = new JLabel("Total: ");
    public static final JLabel lastSixMonthsTotal = new JLabel("2");
    public static final JLabel olderLabel = new JLabel("Older Than 6 Months");
    public static final JLabel olderTotalLabel = new JLabel("Total: ");
    public static final JLabel olderTotal = new JLabel("4");
    public static JPanel getWarningDetailsPanel(){
        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());

        JLabel guestNameLabel = new JLabel("Guest Name:   ");
        JLabel guestName = new JLabel("John Doe");

        guestNameLabel.setBackground(Color.gray);
        guestNameLabel.setOpaque(true);
        guestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        guestName.setBackground(Color.gray);
        guestName.setOpaque(true);
        guestName.setFont(new Font("Serif", Font.PLAIN, 24));

        lastSixMonthsLabel.setBackground(Color.lightGray);
        lastSixMonthsLabel.setOpaque(true);
        lastSixMonthsLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        lastSixMonthsTotalLabel.setBackground(Color.lightGray);
        lastSixMonthsTotalLabel.setOpaque(true);
        lastSixMonthsTotalLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        lastSixMonthsTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lastSixMonthsTotal.setBackground(Color.lightGray);
        lastSixMonthsTotal.setOpaque(true);
        lastSixMonthsTotal.setFont(new Font("Serif", Font.PLAIN, 18));

        olderLabel.setBackground(Color.lightGray);
        olderLabel.setOpaque(true);
        olderLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        olderTotalLabel.setBackground(Color.lightGray);
        olderTotalLabel.setOpaque(true);
        olderTotalLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        olderTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        olderTotal.setBackground(Color.lightGray);
        olderTotal.setOpaque(true);
        olderTotal.setFont(new Font("Serif", Font.PLAIN, 18));

        // Data to be displayed in the JTable
        String[][] sixMonthData = {
                { "3/15/2024", "RS", "They were mean to me :(" },
                { "3/13/2024", "RS", "They didn't remember my birthday..." }
        };

        // Column Names
        String[] columnNames = { "Date", "Staff Initials", "Notes" };

        // Initializing the JTable
        JTable sixMonthWarningsTable = new JTable(sixMonthData, columnNames);
        sixMonthWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sixMonthWarningsTable.setRowHeight(50);
        sixMonthWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);
        sixMonthWarningsTable.setBackground(Color.YELLOW);

        // Data to be displayed in the JTable
        String[][] data = {
                { "3/13/2023", "RS", "They didn't remember my birthday..." },
                { "2/24/2023", "RS", "They didn't laugh at my joke" },
                { "1/18/2023", "RS", "They smelt funny" },
                { "1/12/2023", "RS", "They said bad words" }
        };

        // Initializing the JTable
        JTable olderWarningsTable = new JTable(data, columnNames);
        olderWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderWarningsTable.setRowHeight(50);
        olderWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);
        olderWarningsTable.setBackground(Color.YELLOW);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        warningDetailsPanel.add(guestNameLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        warningDetailsPanel.add(guestName, c);
        c.weightx = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        warningDetailsPanel.add(lastSixMonthsLabel, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        warningDetailsPanel.add(lastSixMonthsTotalLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        warningDetailsPanel.add(lastSixMonthsTotal, c);
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 2;
        c.gridwidth = 3;
        warningDetailsPanel.add(sixMonthWarningsTable.getTableHeader(), c);
        c.gridy = 3;
        warningDetailsPanel.add(sixMonthWarningsTable, c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.weightx = 0;
        warningDetailsPanel.add(olderLabel, c);
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        warningDetailsPanel.add(olderTotalLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.ipadx = 5;
        c.anchor = GridBagConstraints.WEST;
        warningDetailsPanel.add(olderTotal, c);
        c.gridx = 0;
        c.weightx = 1;
        c.gridy = 5;
        c.gridwidth = 3;
        warningDetailsPanel.add(olderWarningsTable.getTableHeader(), c);
        c.gridy = 6;
        warningDetailsPanel.add(olderWarningsTable, c);

        return warningDetailsPanel;
    }
}
