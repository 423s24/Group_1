package sssp.View.GuestDetails.Panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import sssp.Model.GuestDBKeys;
import sssp.Model.WarningDBKeys;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

public class WarningsPanel extends JPanel{
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private final JLabel lastSixMonthsLabel = new JLabel("Last 6 Months");
    private final JLabel lastSixMonthsTotalLabel = new JLabel("Total: ");
    private final JLabel olderLabel = new JLabel("Older Than 6 Months");
    private final JLabel olderTotalLabel = new JLabel("Total: ");
    private final JLabel noWarningsHeader = new JLabel("No Warnings");

    private JTable sixMonthWarningsTable;
    private JTable olderWarningsTable;
    private JLabel warningsGuestNameLabel = new JLabel("Guest Name: None");

    public WarningsPanel(){
        super(new GridBagLayout());

        warningsGuestNameLabel.setOpaque(true);

        lastSixMonthsLabel.setBackground(Color.white);
        lastSixMonthsLabel.setOpaque(true);
        
        lastSixMonthsTotalLabel.setBackground(Color.white);
        lastSixMonthsTotalLabel.setOpaque(true);
        
        lastSixMonthsTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        olderLabel.setBackground(Color.white);
        olderLabel.setOpaque(true);
        
        olderTotalLabel.setBackground(Color.white);
        olderTotalLabel.setOpaque(true);
        
        olderTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        noWarningsHeader.setBackground(Color.white);
        noWarningsHeader.setOpaque(true);
        
        noWarningsHeader.setVisible(false);

        // Column Names
        String[] columnNames = {
            WarningDBKeys.DATE.getPrettyName(),
            WarningDBKeys.STAFF_INITIALS.getPrettyName(),
            WarningDBKeys.NOTES.getPrettyName()
        };

        DefaultTableModel model = createWarningsTable(null, columnNames);

        // Create tables
        sixMonthWarningsTable = new JTable(model);
        sixMonthWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sixMonthWarningsTable.setRowHeight(50);
        sixMonthWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        sixMonthWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);

        olderWarningsTable = new JTable(model);
        olderWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderWarningsTable.setRowHeight(50);
        olderWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);

        // Add left-side elements
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        this.add(warningsGuestNameLabel, c);
        this.add(noWarningsHeader, c);

        c.gridwidth = 1;
        this.add(lastSixMonthsLabel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;

        this.add(sixMonthWarningsTable.getTableHeader(), c);
        this.add(sixMonthWarningsTable, c);

        c.gridwidth = 1;
        this.add(olderLabel, c);
        c.gridwidth = GridBagConstraints.REMAINDER;

        this.add(olderWarningsTable.getTableHeader(), c);

        this.add(olderWarningsTable, c);

        // add right-side elements
        
        c.gridx = 1;
        c.gridy = 1;
        this.add(lastSixMonthsTotalLabel, c);

        c.gridy = 4;
        this.add(olderTotalLabel, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(new JLabel(), c);

        setActiveGuestID(null);
    }
        
    public void setActiveGuestID(String activeGuestID) {
        String guestNameString;
        
        if(activeGuestID == null) {
            guestNameString = "None";
            sixMonthWarningsTable.setVisible(false);
            sixMonthWarningsTable.getTableHeader().setVisible(false);
            lastSixMonthsLabel.setVisible(false);
            lastSixMonthsTotalLabel.setVisible(false);

            olderWarningsTable.setVisible(false);
            olderWarningsTable.getTableHeader().setVisible(false);
            olderLabel.setVisible(false);
            olderTotalLabel.setVisible(false);

            noWarningsHeader.setVisible(true);
            return;
        }

        Map<String,String> activeGuestData = db.database.guests.get(activeGuestID);
        List<Map<String,String>> warningData = DBConnectorV2.joinOnKey(db.database.conflicts.get("Warnings"), "GuestId", activeGuestID);

        guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());


        warningsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date sixMonthsAgo = new Date(System.currentTimeMillis() - 6L * 30 * 24 * 60 * 60 * 1000); // 6 months ago

        List<Map<String, String>> sixMonthWarnings = new ArrayList<>();
        List<Map<String, String>> olderWarnings = new ArrayList<>();

        if(warningData != null)
        {
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
            dataArray[i][0] = row.get(WarningDBKeys.DATE.getKey());
            dataArray[i][1] = row.get(WarningDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][2] = row.get(WarningDBKeys.NOTES.getKey());
        }
    
        // Create new uneditable DefaultTableModel with data
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
