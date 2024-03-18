package sssp.View;

import javax.swing.*;
import java.awt.*;

public class IssueDetailsPopup {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    public static final JLabel noTrespassLabel = new JLabel("No Trespass Details");
    public static JFrame getNoTrespassDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new FlowLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        warningDetailsPanel.add(noTrespassLabel);

        popupFrame.add(warningDetailsPanel);


        return popupFrame;
    }

    public static final JLabel suspensionsLabel = new JLabel("Suspensions Details");
    public static JFrame getSuspensionsDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new FlowLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        warningDetailsPanel.add(suspensionsLabel);

        popupFrame.add(warningDetailsPanel);


        return popupFrame;
    }

    public static final JLabel guestNameLabel = new JLabel("Guest Name:   ");
    public static final JLabel guestName = new JLabel("John Doe");
    public static final JLabel lastSixMonthsLabel = new JLabel("Last 6 Months");
    public static final JLabel lastSixMonthsTotalLabel = new JLabel("Total: ");
    public static final JLabel lastSixMonthsTotal = new JLabel("2");
    public static final JLabel olderLabel = new JLabel("Older Than 6 Months");
    public static final JLabel olderTotalLabel = new JLabel("Total: ");
    public static final JLabel olderTotal = new JLabel("4");
    public static JFrame getWarningDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new GridBagLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setBackground(Color.red);

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
        String[][] data = {
                { "3/15/2024", "RS", "They were mean to me :(" },
                { "3/13/2024", "RS", "They didn't remember my birthday..." }
        };

        // Column Names
        String[] columnNames = { "Date", "Staff Initials", "Notes" };

        // Initializing the JTable
        JTable sixMonthWarningsTable = new JTable(data, columnNames);
        sixMonthWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sixMonthWarningsTable.setRowHeight(50);
        sixMonthWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);


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

        GridBagConstraints frameC = new GridBagConstraints();
        frameC.fill = GridBagConstraints.HORIZONTAL;
        frameC.weightx = 1.0;
        frameC.weighty = 1.0;
        frameC.gridx = 0;
        frameC.gridy = 0;
        frameC.anchor = GridBagConstraints.NORTH;
        popupFrame.add(warningDetailsPanel, frameC);

        return popupFrame;
    }
}
