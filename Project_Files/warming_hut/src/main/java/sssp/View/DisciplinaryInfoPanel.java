package sssp.View;

//import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Model.GuestDBKeys;
import sssp.Model.NoTrespassDBKeys;
import sssp.Model.SuspensionDBKeys;
import sssp.Model.WarningDBKeys;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.text.ParseException;
import java.text.SimpleDateFormat;



public class DisciplinaryInfoPanel extends JPanel {
    JPanel noTrespassDetailsPanel;
    JPanel suspensionDetailsPanel;
    JPanel warningDetailsPanel;

    public DisciplinaryInfoPanel()
    {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        noTrespassDetailsPanel = new JPanel(new BorderLayout());
        suspensionDetailsPanel = new JPanel(new BorderLayout());
        warningDetailsPanel = new JPanel(new BorderLayout());

        noTrespassDetailsPanel.add(createNoTrespassDetailsPanel(), BorderLayout.NORTH);
        suspensionDetailsPanel.add(createSuspensionsDetailsPanel(), BorderLayout.NORTH);
        warningDetailsPanel.add(createWarningDetailsPanel(), BorderLayout.NORTH);

        tabbedPane.addTab("No Trespass", noTrespassDetailsPanel);
        tabbedPane.addTab("Suspensions", suspensionDetailsPanel);
        tabbedPane.addTab("Warnings", warningDetailsPanel);

        this.add(tabbedPane);
    }

    private String activeGuestID;
    private Map<String, String> activeGuestData;
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    List<Map<String,String>> trespassData;
    List<Map<String,String>> suspensionData;
    List<Map<String,String>> probationData;
    List<Map<String,String>> warningData;

    public boolean setActiveGuestID(String guestID) {
        this.activeGuestID = guestID;

        activeGuestData = db.database.guests.get(activeGuestID);

        trespassData = DBConnectorV2.joinOnKey(db.database.conflicts.get("NoTrespass"), "GuestId", guestID);
        suspensionData = DBConnectorV2.joinOnKey(db.database.conflicts.get("Suspensions"), "GuestId", guestID);
        probationData = DBConnectorV2.joinOnKey(db.database.conflicts.get("Probation"), "GuestId", guestID);
        warningData = DBConnectorV2.joinOnKey(db.database.conflicts.get("Warnings"), "GuestId", guestID);


        if(activeGuestData == null) {
            // Show error window
            JOptionPane.showMessageDialog(null, "Error: The guest " + guestID + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        onActiveGuestChanged();

        return true;
    }

    private void onActiveGuestChanged() {
        updateTrespassPanel();
        updateSuspensionPanel();
        updateWarningPanel();
    }

    
    public static final JLabel noTrespassLabel = new JLabel("No Trespass Orders");
    public static final JLabel noNoTrespassesLabel = new JLabel("No Orders To Display");
    JLabel noTrespassGuestNameLabel;
    JTable noTrespassTable;

    private static final String[] noTrespassColumnNames = {
        NoTrespassDBKeys.DATE_OF_INCIDENT.getPrettyName(),
        NoTrespassDBKeys.NO_TRESPASS_FROM.getPrettyName(),
        NoTrespassDBKeys.STAFF_INITIALS.getPrettyName(),
        NoTrespassDBKeys.BPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.LPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.CW_ALERT.getPrettyName(),
        NoTrespassDBKeys.NOTES.getPrettyName()
    };

    public JScrollPane createNoTrespassDetailsPanel(){
        noTrespassDetailsPanel = new JPanel(new GridBagLayout());

        noTrespassGuestNameLabel = new JLabel("Guest Name: " + "John Doe");

        noTrespassGuestNameLabel.setBackground(Color.gray);
        noTrespassGuestNameLabel.setOpaque(true);
        noTrespassGuestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));

        noTrespassLabel.setBackground(Color.lightGray);
        noTrespassLabel.setOpaque(true);
        noTrespassLabel.setFont(new Font("Serif", Font.PLAIN, 18));

        noNoTrespassesLabel.setBackground(Color.lightGray);
        noNoTrespassesLabel.setOpaque(true);
        noNoTrespassesLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        noNoTrespassesLabel.setVisible(false);

        // Data to be displayed in the JTable
        String[][] activeSuspensionData = {
                { "3/15/2024", "ALL HRDC", "RS", "Notified", "Not Notified", "Added", "Threatened to tickle me..."}
        };

        // Initializing the JTable
        noTrespassTable = new JTable(activeSuspensionData, noTrespassColumnNames);
        noTrespassTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        noTrespassTable.setRowHeight(50);
        noTrespassTable.getColumnModel().getColumn(0).setMaxWidth(250);
        noTrespassTable.getColumnModel().getColumn(1).setMaxWidth(250);
        noTrespassTable.getColumnModel().getColumn(2).setMaxWidth(75);
        noTrespassTable.getColumnModel().getColumn(3).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(4).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(5).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(6).setMinWidth(100);
        noTrespassTable.setBackground(Color.RED);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridwidth = GridBagConstraints.REMAINDER;

        noTrespassDetailsPanel.add(noTrespassGuestNameLabel, c);
        noTrespassDetailsPanel.add(noNoTrespassesLabel, c);
        noTrespassDetailsPanel.add(noTrespassLabel, c);
        noTrespassDetailsPanel.add(noTrespassTable.getTableHeader(), c);
        noTrespassDetailsPanel.add(noTrespassTable, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridy = GridBagConstraints.RELATIVE;
        noTrespassDetailsPanel.add(new JLabel(), c);

        return new JScrollPane(noTrespassDetailsPanel);
    }

    private void updateTrespassPanel() {
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());
        noTrespassGuestNameLabel.setText("Guest Name: " + guestNameString);

        boolean shouldShowNoTrespass = !trespassData.isEmpty();
        
        if (shouldShowNoTrespass) {
            noTrespassTable.setModel(createTrespassTableModel(trespassData, noTrespassColumnNames));
        }

        noTrespassTable.setVisible(shouldShowNoTrespass);
        noTrespassTable.getTableHeader().setVisible(shouldShowNoTrespass);
        noTrespassLabel.setVisible(shouldShowNoTrespass);
    
        noNoTrespassesLabel.setVisible(!shouldShowNoTrespass);
    }

    private DefaultTableModel createTrespassTableModel(List<Map<String, String>> data, String[] columnNames) {
        // Convert data to 2D array
        String[][] dataArray = new String[data.size()][columnNames.length];
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            dataArray[i][0] = row.get(NoTrespassDBKeys.DATE_OF_INCIDENT.getKey());
            dataArray[i][1] = row.get(NoTrespassDBKeys.NO_TRESPASS_FROM.getKey());
            dataArray[i][2] = row.get(NoTrespassDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][3] = row.get(NoTrespassDBKeys.BPD_STATUS.getKey());
            dataArray[i][4] = row.get(NoTrespassDBKeys.LPD_STATUS.getKey());
            dataArray[i][5] = row.get(NoTrespassDBKeys.CW_ALERT.getKey());
            dataArray[i][6] = row.get(NoTrespassDBKeys.NOTES.getKey());
        }
    
        // Create new DefaultTableModel with data
        return new DefaultTableModel(dataArray, columnNames);
    }

// region Warnings Panel
    public static final JLabel lastSixMonthsLabel = new JLabel("Last 6 Months");
    public static final JLabel lastSixMonthsTotalLabel = new JLabel("Total: ");
    public static final JLabel olderLabel = new JLabel("Older Than 6 Months");
    public static final JLabel olderTotalLabel = new JLabel("Total: ");
    private static final JLabel noWarningsHeader = new JLabel("No Warnings");

    private JTable sixMonthWarningsTable;
    private JTable olderWarningsTable;
    private JLabel warningsGuestNameLabel;
    private JScrollPane createWarningDetailsPanel(){
        warningDetailsPanel = new JPanel(new GridBagLayout());

        warningsGuestNameLabel = new JLabel("Guest Name: John Doe");

        warningsGuestNameLabel.setBackground(Color.gray);
        warningsGuestNameLabel.setOpaque(true);
        warningsGuestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));

        lastSixMonthsLabel.setBackground(Color.lightGray);
        lastSixMonthsLabel.setOpaque(true);
        lastSixMonthsLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        lastSixMonthsTotalLabel.setBackground(Color.lightGray);
        lastSixMonthsTotalLabel.setOpaque(true);
        lastSixMonthsTotalLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        lastSixMonthsTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        olderLabel.setBackground(Color.lightGray);
        olderLabel.setOpaque(true);
        olderLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        olderTotalLabel.setBackground(Color.lightGray);
        olderTotalLabel.setOpaque(true);
        olderTotalLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        olderTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        noWarningsHeader.setBackground(Color.lightGray);
        noWarningsHeader.setOpaque(true);
        noWarningsHeader.setFont(new Font("Serif", Font.PLAIN, 18));
        noWarningsHeader.setVisible(false);

        // Data to be displayed in the JTable
        String[][] sixMonthData = {
                { "3/15/2024", "RS", "They were mean to me :(" },
                { "3/13/2024", "RS", "They didn't remember my birthday..." }
        };

        // Column Names
        String[] columnNames = {
            WarningDBKeys.DATE.getPrettyName(),
            WarningDBKeys.STAFF_INITIALS.getPrettyName(),
            WarningDBKeys.NOTES.getPrettyName()
        };

        // Initializing the JTable
        sixMonthWarningsTable = new JTable(sixMonthData, columnNames);
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
        olderWarningsTable = new JTable(data, columnNames);
        olderWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderWarningsTable.setRowHeight(50);
        olderWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);
        olderWarningsTable.setBackground(Color.YELLOW);


        // Add left-side elements
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        warningDetailsPanel.add(warningsGuestNameLabel, c);
        warningDetailsPanel.add(noWarningsHeader, c);

        c.gridwidth = 1;
        warningDetailsPanel.add(lastSixMonthsLabel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;

        warningDetailsPanel.add(sixMonthWarningsTable.getTableHeader(), c);
        warningDetailsPanel.add(sixMonthWarningsTable, c);

        c.gridwidth = 1;
        warningDetailsPanel.add(olderLabel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;

        warningDetailsPanel.add(olderWarningsTable.getTableHeader(), c);

        warningDetailsPanel.add(olderWarningsTable, c);

        // add right-side elements
        
        c.gridx = 1;
        c.gridy = 1;
        warningDetailsPanel.add(lastSixMonthsTotalLabel, c);

        c.gridy = 4;
        warningDetailsPanel.add(olderTotalLabel, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        warningDetailsPanel.add(new JLabel(), c);

        return new JScrollPane(warningDetailsPanel);
    }
        
    private void updateWarningPanel() {
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());
        warningsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date sixMonthsAgo = new Date(System.currentTimeMillis() - 6L * 30 * 24 * 60 * 60 * 1000); // 6 months ago

        List<Map<String, String>> sixMonthWarnings = new ArrayList<>();
        List<Map<String, String>> olderWarnings = new ArrayList<>();

        for (Map<String, String> warning : warningData) {
            try {
                Date warningDate = sdf.parse(warning.get(WarningDBKeys.DATE.getKey()));
                if (warningDate.after(sixMonthsAgo)) {
                    sixMonthWarnings.add(warning);
                } else {
                    olderWarnings.add(warning);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String[] columnNames = {
            WarningDBKeys.DATE.getPrettyName(),
            WarningDBKeys.STAFF_INITIALS.getPrettyName(),
            WarningDBKeys.NOTES.getPrettyName()
        };

        boolean shouldShowSixMonthWarnings = !sixMonthWarnings.isEmpty();
        boolean shouldShowOlderWarnings = !olderWarnings.isEmpty();

        if (shouldShowSixMonthWarnings) {
            sixMonthWarningsTable.setModel(createWarningsTable(sixMonthWarnings, columnNames));
            lastSixMonthsTotalLabel.setText("Total: " + String.valueOf(sixMonthWarnings.size()));
        }

        sixMonthWarningsTable.setVisible(shouldShowSixMonthWarnings);
        sixMonthWarningsTable.getTableHeader().setVisible(shouldShowSixMonthWarnings);
        lastSixMonthsLabel.setVisible(shouldShowSixMonthWarnings);
        lastSixMonthsTotalLabel.setVisible(shouldShowSixMonthWarnings);

        if (shouldShowOlderWarnings) {
            olderWarningsTable.setModel(createWarningsTable(olderWarnings, columnNames));
            olderTotalLabel.setText("Total: " + String.valueOf(olderWarnings.size()));
        }

        olderWarningsTable.setVisible(shouldShowOlderWarnings);
        olderWarningsTable.getTableHeader().setVisible(shouldShowOlderWarnings);
        olderLabel.setVisible(shouldShowOlderWarnings);
        olderTotalLabel.setVisible(shouldShowOlderWarnings);

        noWarningsHeader.setVisible(!shouldShowSixMonthWarnings && !shouldShowOlderWarnings);
    }
    
    private DefaultTableModel createWarningsTable(List<Map<String, String>> data, String[] columnNames) {
        // Convert data to 2D array
        String[][] dataArray = new String[data.size()][columnNames.length];
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            dataArray[i][0] = row.get(WarningDBKeys.DATE.getKey());
            dataArray[i][1] = row.get(WarningDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][2] = row.get(WarningDBKeys.NOTES.getKey());
        }
    
        // Create new DefaultTableModel with data
        return new DefaultTableModel(dataArray, columnNames);
    }
// endregion

//region Suspension Panel
    private JTable activeSuspensionsTable;
    private JTable olderSuspensionsTable;
    private JLabel activeSuspensionsHeader;
    private JLabel olderSuspensionsHeader;
    private JLabel noSuspensionsHeader;
    private JLabel suspensionsGuestNameLabel;

    private JScrollPane createSuspensionsDetailsPanel(){
        JPanel suspensionsDetailsPanel = new JPanel(new GridBagLayout());

        suspensionsGuestNameLabel = new JLabel("Guest Name: John Doe");
        activeSuspensionsHeader = new JLabel("Active Suspensions");
        olderSuspensionsHeader = new JLabel("Expired Suspensions");

        // Visible when there are no suspensions.
        noSuspensionsHeader = new JLabel("No Suspensions");
        noSuspensionsHeader.setVisible(false);

        suspensionsGuestNameLabel.setBackground(Color.gray);
        suspensionsGuestNameLabel.setOpaque(true);
        suspensionsGuestNameLabel.setFont(new Font("Serif", Font.PLAIN, 24));

        activeSuspensionsHeader.setBackground(Color.lightGray);
        activeSuspensionsHeader.setOpaque(true);
        activeSuspensionsHeader.setFont(new Font("Serif", Font.PLAIN, 18));
        olderSuspensionsHeader.setBackground(Color.lightGray);
        olderSuspensionsHeader.setOpaque(true);
        olderSuspensionsHeader.setFont(new Font("Serif", Font.PLAIN, 18));
        noSuspensionsHeader.setBackground(Color.lightGray);
        noSuspensionsHeader.setOpaque(true);
        noSuspensionsHeader.setFont(new Font("Serif", Font.PLAIN, 18));

        // Column Names
        String[] columnNames = {
            SuspensionDBKeys.ISSUING_DATE.getPrettyName(),
            SuspensionDBKeys.EXPIRY_DATE.getPrettyName(),
            SuspensionDBKeys.SERVICE_SUSPENDED.getPrettyName(),
            SuspensionDBKeys.STAFF_INITIALS.getPrettyName(),
            SuspensionDBKeys.NOTES.getPrettyName()
        };

        // Data to be displayed in the JTable
        String[][] activeSuspensionData = {
                { "3/15/2024", "3/15/2025", "Nightly Bunking", "RS", "Refused to leave after 5 warnings" }
        };
        

        // Initializing the JTable
        activeSuspensionsTable = new JTable(activeSuspensionData, columnNames);
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
        olderSuspensionsTable = new JTable(olderData, columnNames);
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
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        suspensionsDetailsPanel.add(suspensionsGuestNameLabel, c);
        suspensionsDetailsPanel.add(noSuspensionsHeader, c);
        suspensionsDetailsPanel.add(activeSuspensionsHeader, c);
        suspensionsDetailsPanel.add(activeSuspensionsTable.getTableHeader(), c);
        suspensionsDetailsPanel.add(activeSuspensionsTable, c);
        suspensionsDetailsPanel.add(olderSuspensionsHeader, c);
        suspensionsDetailsPanel.add(olderSuspensionsTable.getTableHeader(), c);
        suspensionsDetailsPanel.add(olderSuspensionsTable, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        noTrespassDetailsPanel.add(new JLabel(), c);

        return new JScrollPane(suspensionsDetailsPanel);
    }

    private void updateSuspensionPanel() {
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());
        suspensionsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date(System.currentTimeMillis());

        List<Map<String, String>> activeSuspensions = new ArrayList<>();
        List<Map<String, String>> olderSuspensions = new ArrayList<>();

        for (Map<String, String> suspension : suspensionData) {
            try {
                Date expirationDate = sdf.parse(suspension.get(SuspensionDBKeys.EXPIRY_DATE.getKey()));
                if (expirationDate.after(today)) {
                    activeSuspensions.add(suspension);
                } else {
                    olderSuspensions.add(suspension);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String[] columnNames = {
            SuspensionDBKeys.ISSUING_DATE.getPrettyName(),
            SuspensionDBKeys.EXPIRY_DATE.getPrettyName(),
            SuspensionDBKeys.SERVICE_SUSPENDED.getPrettyName(),
            SuspensionDBKeys.STAFF_INITIALS.getPrettyName(),
            SuspensionDBKeys.NOTES.getPrettyName()
        };
        
        boolean shouldShowActiveSuspensions = !activeSuspensions.isEmpty();
        boolean shouldShowOlderSuspensions = !olderSuspensions.isEmpty();

        if (shouldShowActiveSuspensions) {
            activeSuspensionsTable.setModel(createSuspensionsTableModel(activeSuspensions, columnNames));
        }

        activeSuspensionsTable.setVisible(shouldShowActiveSuspensions);
        activeSuspensionsTable.getTableHeader().setVisible(shouldShowActiveSuspensions);
        activeSuspensionsHeader.setVisible(shouldShowActiveSuspensions);

        if (shouldShowOlderSuspensions) {
            olderSuspensionsTable.setModel(createSuspensionsTableModel(olderSuspensions, columnNames));
        }

        olderSuspensionsTable.setVisible(shouldShowOlderSuspensions);
        olderSuspensionsTable.getTableHeader().setVisible(shouldShowOlderSuspensions);
        olderSuspensionsHeader.setVisible(shouldShowOlderSuspensions);

        noSuspensionsHeader.setVisible(!shouldShowActiveSuspensions && !shouldShowOlderSuspensions);
    }

    private DefaultTableModel createSuspensionsTableModel(List<Map<String, String>> data, String[] columnNames) {
        // Convert data to 2D array
        String[][] dataArray = new String[data.size()][columnNames.length];
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            dataArray[i][0] = row.get(SuspensionDBKeys.ISSUING_DATE.getKey());
            dataArray[i][1] = row.get(SuspensionDBKeys.EXPIRY_DATE.getKey());
            dataArray[i][2] = row.get(SuspensionDBKeys.SERVICE_SUSPENDED.getKey());
            dataArray[i][3] = row.get(SuspensionDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][4] = row.get(SuspensionDBKeys.NOTES.getKey());
        }
    
        // Create new DefaultTableModel with data
        return new DefaultTableModel(dataArray, columnNames);
    }
//endregion
}