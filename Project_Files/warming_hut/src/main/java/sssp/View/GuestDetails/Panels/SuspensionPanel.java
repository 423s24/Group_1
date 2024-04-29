package sssp.View.GuestDetails.Panels;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.UUIDGenerator;
import sssp.Model.GuestDBKeys;
import sssp.Model.SuspensionDBKeys;

public class SuspensionPanel extends JPanel{
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private String activeGuestID = null;

    private JTable activeSuspensionsTable;
    private JTable olderSuspensionsTable;

    // these appear above the tables when they are visible
    private JLabel activeSuspensionsHeader = new JLabel("Active Suspensions");
    private JLabel olderSuspensionsHeader = new JLabel("Expired Suspensions");

    // this appears when there are no suspensions
    private JLabel noSuspensionsHeader = new JLabel("No Suspensions");

    private JLabel suspensionsGuestNameLabel = new JLabel("Guest Name: None");

    TableModelListener dbUpdater = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if(activeGuestID == null)
                return;

            DefaultTableModel model = (DefaultTableModel)e.getSource();
            int row = e.getFirstRow();

            String suspensionID = model.getValueAt(row, 5).toString();

            Map<String, String> warning = db.database.conflicts.get("Suspensions").get(suspensionID);
            warning.put(SuspensionDBKeys.ISSUING_DATE.getKey(), model.getValueAt(row, 0).toString());
            warning.put(SuspensionDBKeys.EXPIRY_DATE.getKey(), model.getValueAt(row, 1).toString());
            warning.put(SuspensionDBKeys.SERVICE_SUSPENDED.getKey(), model.getValueAt(row, 2).toString());
            warning.put(SuspensionDBKeys.STAFF_INITIALS.getKey(), model.getValueAt(row, 3).toString());
            warning.put(SuspensionDBKeys.NOTES.getKey(), model.getValueAt(row, 4).toString());

            db.database.conflicts.get("Suspensions").put(suspensionID, warning);
            db.asyncPush();
        }
    };

    public SuspensionPanel(){
        super(new GridBagLayout());

        db.subscribeRunnableToDBUpdate(this::updateTables);

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
        activeSuspensionsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        olderSuspensionsTable = new JTable(model);
        olderSuspensionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderSuspensionsTable.setRowHeight(50);
        olderSuspensionsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(1).setMaxWidth(100);
        olderSuspensionsTable.getColumnModel().getColumn(2).setMaxWidth(200);
        olderSuspensionsTable.getColumnModel().getColumn(3).setMaxWidth(75);
        olderSuspensionsTable.getColumnModel().getColumn(4).setMinWidth(100);
        olderSuspensionsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JButton addSuspensionButton = new JButton("Add Suspension");

        // Add listener to Add button
        addSuspensionButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;

            String newSuspensionID = UUIDGenerator.getNewUUID();

            DefaultTableModel model1 = (DefaultTableModel)activeSuspensionsTable.getModel();

            String todayDateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

            model1.removeTableModelListener(dbUpdater);
            model1.addRow(new Object[] {todayDateString, "MM/dd/yyyy", "", "", "", newSuspensionID});
            model1.addTableModelListener(dbUpdater);

            // create suspension object
            Map<String, String> newSuspension = new HashMap<>();
            newSuspension.put(SuspensionDBKeys.GUEST_ID.getKey(), activeGuestID);
            newSuspension.put(SuspensionDBKeys.ISSUING_DATE.getKey(), todayDateString);
            newSuspension.put(SuspensionDBKeys.EXPIRY_DATE.getKey(), "MM/dd/yyyy");
            newSuspension.put(SuspensionDBKeys.SERVICE_SUSPENDED.getKey(), "");
            newSuspension.put(SuspensionDBKeys.STAFF_INITIALS.getKey(), "");
            newSuspension.put(SuspensionDBKeys.NOTES.getKey(), "");

            // Add to DB
            db.database.conflicts.get("Suspensions").put(newSuspensionID, newSuspension);
            db.asyncPush();
        });

        JButton removeSuspensionButton = new JButton("Remove Suspension");

        removeSuspensionButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;
        
            // Get selected row from both tables
            int selectedRowActive = activeSuspensionsTable.getSelectedRow();
            int selectedRowExpired = olderSuspensionsTable.getSelectedRow();
        
            // If no row is selected in either table, return
            if(selectedRowActive == -1 && selectedRowExpired == -1)
                return;
        
            // Initialize warningID and selectedTableModel
            String suspensionID = null;
            DefaultTableModel selectedTableModel = null;
        
            // Check which table has a selected row and get the warningID and model
            if(selectedRowActive != -1) {
                suspensionID = activeSuspensionsTable.getModel().getValueAt(selectedRowActive, 5).toString();
                selectedTableModel = (DefaultTableModel)activeSuspensionsTable.getModel();
            } else if(selectedRowExpired != -1) {
                suspensionID = olderSuspensionsTable.getModel().getValueAt(selectedRowExpired, 5).toString();
                selectedTableModel = (DefaultTableModel)olderSuspensionsTable.getModel();
            }
        
            // Remove from DB
            db.database.conflicts.get("Suspensions").put(suspensionID, null);
            db.asyncPush();
        
            // Remove from table
            selectedTableModel.removeTableModelListener(dbUpdater);
            if(selectedRowActive != -1) {
                selectedTableModel.removeRow(selectedRowActive);
            } else if(selectedRowExpired != -1) {
                selectedTableModel.removeRow(selectedRowExpired);
            }
            selectedTableModel.addTableModelListener(dbUpdater);
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.gridy = 0;
        c.gridwidth = 1;
        this.add(addSuspensionButton, c);

        c.gridx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(removeSuspensionButton, c);
        c.gridx = 0;

        c.gridy = GridBagConstraints.RELATIVE;
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

    private void updateTables()
    {
        this.setActiveGuestID(activeGuestID);
    }

    public void setActiveGuestID(String activeGuestID) {
        this.activeGuestID = activeGuestID;

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

        if (db.database.conflicts.get("Suspensions") == null) {
            db.database.conflicts.put("Suspensions", new HashMap<>());
        }

        /**
         * Holds the suspension data for the active guest.
         * The suspension data is retrieved from the database by joining the "Suspensions" table on the "GuestId" key.
         * Each suspension record is represented as a map with key-value pairs.
         */
        List<Entry<String,Map<String,String>>> suspensionData = DBConnectorV2.filterEntriesByKeyValuePair(db.database.conflicts.get("Suspensions"), "GuestId", activeGuestID);
        
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());
        suspensionsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date(System.currentTimeMillis());

        List<Entry<String,Map<String,String>>> activeSuspensions = new ArrayList<>();
        List<Entry<String,Map<String,String>>> olderSuspensions = new ArrayList<>();

        if (suspensionData != null) {
            for (Entry<String, Map<String, String>> suspension : suspensionData) {
                try {
                    Date expirationDate = sdf.parse(suspension.getValue().get(SuspensionDBKeys.EXPIRY_DATE.getKey()));
                    if (expirationDate.after(today)) {
                        activeSuspensions.add(suspension);
                    } else {
                        olderSuspensions.add(suspension);
                    }
                } catch (ParseException e) {
                    activeSuspensions.add(suspension);
                }
            }
        }

        String[] columnNames = {
            SuspensionDBKeys.ISSUING_DATE.getPrettyName(),
            SuspensionDBKeys.EXPIRY_DATE.getPrettyName(),
            SuspensionDBKeys.SERVICE_SUSPENDED.getPrettyName(),
            SuspensionDBKeys.STAFF_INITIALS.getPrettyName(),
            SuspensionDBKeys.NOTES.getPrettyName(),
            "ID"
        };
        
        boolean shouldShowActiveSuspensions = !activeSuspensions.isEmpty();
        boolean shouldShowOlderSuspensions = !olderSuspensions.isEmpty();

        if (shouldShowActiveSuspensions) {
            activeSuspensionsTable.setModel(createSuspensionsTableModel(activeSuspensions, columnNames));

            activeSuspensionsTable.getModel().addTableModelListener(dbUpdater);

            // make the ID column invisible
            activeSuspensionsTable.getColumnModel().removeColumn(activeSuspensionsTable.getColumnModel().getColumn(5));
        }

        activeSuspensionsTable.setVisible(shouldShowActiveSuspensions);
        activeSuspensionsTable.getTableHeader().setVisible(shouldShowActiveSuspensions);
        activeSuspensionsHeader.setVisible(shouldShowActiveSuspensions);

        if (shouldShowOlderSuspensions) {
            olderSuspensionsTable.setModel(createSuspensionsTableModel(olderSuspensions, columnNames));

            olderSuspensionsTable.getModel().addTableModelListener(dbUpdater);

            // make the ID column invisible
            olderSuspensionsTable.getColumnModel().removeColumn(olderSuspensionsTable.getColumnModel().getColumn(5));
        }

        olderSuspensionsTable.setVisible(shouldShowOlderSuspensions);
        olderSuspensionsTable.getTableHeader().setVisible(shouldShowOlderSuspensions);
        olderSuspensionsHeader.setVisible(shouldShowOlderSuspensions);

        noSuspensionsHeader.setVisible(!shouldShowActiveSuspensions && !shouldShowOlderSuspensions);
    }

    private DefaultTableModel createSuspensionsTableModel(List<Entry<String, Map<String, String>>> data, String[] columnNames) {
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
            Entry<String, Map<String, String>> row = data.get(i);
            dataArray[i][0] = row.getValue().get(SuspensionDBKeys.ISSUING_DATE.getKey());
            dataArray[i][1] = row.getValue().get(SuspensionDBKeys.EXPIRY_DATE.getKey());
            dataArray[i][2] = row.getValue().get(SuspensionDBKeys.SERVICE_SUSPENDED.getKey());
            dataArray[i][3] = row.getValue().get(SuspensionDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][4] = row.getValue().get(SuspensionDBKeys.NOTES.getKey());
            dataArray[i][5] = row.getKey();
        }
    
        // Create new DefaultTableModel with data
        return new DefaultTableModel(dataArray, columnNames)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return true;
            }
        };
    }
}
