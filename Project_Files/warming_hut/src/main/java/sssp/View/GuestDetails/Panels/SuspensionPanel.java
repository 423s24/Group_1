package sssp.View.GuestDetails.Panels;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

import sssp.Model.GuestDBKeys;
import sssp.Model.SuspensionDBKeys;

public class SuspensionPanel extends JPanel{
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private JTable activeSuspensionsTable;
    private JTable olderSuspensionsTable;

    // these appear above the tables when they are visible
    private JLabel activeSuspensionsHeader = new JLabel("Active Suspensions");
    private JLabel olderSuspensionsHeader = new JLabel("Expired Suspensions");

    // this appears when there are no suspensions
    private JLabel noSuspensionsHeader = new JLabel("No Suspensions");

    private JLabel suspensionsGuestNameLabel = new JLabel("Guest Name: None");

    public SuspensionPanel(){
        super(new GridBagLayout());

        // Visible when there are no suspensions.
        noSuspensionsHeader.setVisible(false);

        suspensionsGuestNameLabel.setOpaque(true);

        activeSuspensionsHeader.setBackground(Color.white);
        activeSuspensionsHeader.setOpaque(true);

        olderSuspensionsHeader.setBackground(Color.white);
        olderSuspensionsHeader.setOpaque(true);

        noSuspensionsHeader.setBackground(Color.white);
        noSuspensionsHeader.setOpaque(true);

        // Column Names
        String[] columnNames = {
            SuspensionDBKeys.ISSUING_DATE.getPrettyName(),
            SuspensionDBKeys.EXPIRY_DATE.getPrettyName(),
            SuspensionDBKeys.SERVICE_SUSPENDED.getPrettyName(),
            SuspensionDBKeys.STAFF_INITIALS.getPrettyName(),
            SuspensionDBKeys.NOTES.getPrettyName()
        };
        
        // init with empty model having only our column names
        DefaultTableModel model = createSuspensionsTableModel(null, columnNames);

        // Initializing the JTable
        activeSuspensionsTable = new JTable(model);
        activeSuspensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        activeSuspensionsTable.setRowHeight(50);
        activeSuspensionsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        activeSuspensionsTable.getColumnModel().getColumn(1).setMaxWidth(100);
        activeSuspensionsTable.getColumnModel().getColumn(2).setMaxWidth(275);
        activeSuspensionsTable.getColumnModel().getColumn(3).setMaxWidth(75);
        activeSuspensionsTable.getColumnModel().getColumn(4).setMinWidth(100);

        olderSuspensionsTable = new JTable(model);
        olderSuspensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderSuspensionsTable.setRowHeight(50);
        olderSuspensionsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(1).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(2).setMaxWidth(200);
        olderSuspensionsTable.getColumnModel().getColumn(3).setMaxWidth(75);
        olderSuspensionsTable.getColumnModel().getColumn(4).setMinWidth(100);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        this.add(suspensionsGuestNameLabel, c);
        this.add(noSuspensionsHeader, c);
        this.add(activeSuspensionsHeader, c);
        this.add(activeSuspensionsTable.getTableHeader(), c);
        this.add(activeSuspensionsTable, c);
        this.add(olderSuspensionsHeader, c);
        this.add(olderSuspensionsTable.getTableHeader(), c);
        this.add(olderSuspensionsTable, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(new JLabel(), c);

        this.setActiveGuestID(null);
    }

    public void setActiveGuestID(String activeGuestID) {
        if(activeGuestID == null) {
            suspensionsGuestNameLabel.setText("Guest Name: None");
            activeSuspensionsTable.setVisible(false);
            activeSuspensionsTable.getTableHeader().setVisible(false);
            activeSuspensionsHeader.setVisible(false);

            olderSuspensionsTable.setVisible(false);
            olderSuspensionsTable.getTableHeader().setVisible(false);
            olderSuspensionsHeader.setVisible(false);

            noSuspensionsHeader.setVisible(true);
            return;
        }

        Map<String,String> activeGuestData = db.database.guests.get(activeGuestID);

        /**
         * Holds the suspension data for the active guest.
         * The suspension data is retrieved from the database by joining the "Suspensions" table on the "GuestId" key.
         * Each suspension record is represented as a map with key-value pairs.
         */
        List<Map<String,String>> suspensionData = DBConnectorV2.filterByKeyValuePair(db.database.conflicts.get("Suspensions"), "GuestId", activeGuestID);
        
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());
        suspensionsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date(System.currentTimeMillis());

        List<Map<String, String>> activeSuspensions = new ArrayList<>();
        List<Map<String, String>> olderSuspensions = new ArrayList<>();

        if (suspensionData != null) {
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
        if(data == null)
        {
            return new DefaultTableModel(new String[0][0], columnNames)
            {
                @Override
                public boolean isCellEditable(int row, int column)
                {
                    return false;
                }
            };
        }

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
        return new DefaultTableModel(dataArray, columnNames)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
    }
}
