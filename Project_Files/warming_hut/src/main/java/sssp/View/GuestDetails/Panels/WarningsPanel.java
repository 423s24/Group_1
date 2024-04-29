package sssp.View.GuestDetails.Panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Map.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import sssp.Model.GuestDBKeys;
import sssp.Model.WarningDBKeys;
import sssp.View.listeners.DeselectOtherTableListener;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.UUIDGenerator;

public class WarningsPanel extends JPanel{
    private DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private String activeGuestID = null;

    private final JLabel lastSixMonthsLabel = new JLabel("Last 6 Months");
    private final JLabel lastSixMonthsTotalLabel = new JLabel("Total: ");
    private final JLabel olderLabel = new JLabel("Older Than 6 Months");
    private final JLabel olderTotalLabel = new JLabel("Total: ");
    private final JLabel noWarningsHeader = new JLabel("No Warnings");

    private JTable sixMonthWarningsTable;
    private JTable olderWarningsTable;
    private JLabel warningsGuestNameLabel = new JLabel("Guest Name: None");

    TableModelListener dbUpdater = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if(activeGuestID == null)
                return;

            DefaultTableModel model = (DefaultTableModel)e.getSource();
            int row = e.getFirstRow();

            String warningID = model.getValueAt(row, 3).toString();

            Map<String, String> warning = db.database.conflicts.get("Warnings").get(warningID);
            warning.put(WarningDBKeys.DATE.getKey(), model.getValueAt(row, 0).toString());
            warning.put(WarningDBKeys.STAFF_INITIALS.getKey(), model.getValueAt(row, 1).toString());
            warning.put(WarningDBKeys.NOTES.getKey(), model.getValueAt(row, 2).toString());

            db.database.conflicts.get("Warnings").put(warningID, warning);
            db.asyncPush();
        }
    };


    public WarningsPanel(){
        super(new GridBagLayout());

        db.subscribeRunnableToDBUpdate(this::updateTables);

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
        sixMonthWarningsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        olderWarningsTable = new JTable(model);
        olderWarningsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        olderWarningsTable.setRowHeight(50);
        olderWarningsTable.getColumnModel().getColumn(0).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(1).setMaxWidth(75);
        olderWarningsTable.getColumnModel().getColumn(2).setMinWidth(100);
        olderWarningsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        sixMonthWarningsTable.getSelectionModel().addListSelectionListener(new DeselectOtherTableListener(olderWarningsTable));
        olderWarningsTable.getSelectionModel().addListSelectionListener(new DeselectOtherTableListener(sixMonthWarningsTable));
        

        // Create Add and Remove buttons
        JButton addWarningButton = new JButton("Add Warning");

        addWarningButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;

            String thisID = UUIDGenerator.getNewUUID();

            // Create row in the sixMonthWarningsTable- blank except for date
            DefaultTableModel model1 = (DefaultTableModel)sixMonthWarningsTable.getModel();

            model1.removeTableModelListener(dbUpdater);
            model1.addRow(new String[] {new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "", "", thisID});
            model1.addTableModelListener(dbUpdater);

            // Create Warning object
            Map<String, String> warning = new HashMap<>();
            warning.put(WarningDBKeys.GUEST_ID.getKey(), activeGuestID);
            warning.put(WarningDBKeys.DATE.getKey(), new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
            warning.put(WarningDBKeys.STAFF_INITIALS.getKey(), "");
            warning.put(WarningDBKeys.NOTES.getKey(), "");

            // Add to DB
            db.database.conflicts.get("Warnings").put(thisID, warning);
            db.asyncPush();
        });

        JButton removeWarningButton = new JButton("Remove Warning");

        removeWarningButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;
        
            // Get selected row from both tables
            int selectedRowSixMonth = sixMonthWarningsTable.getSelectedRow();
            int selectedRowOlder = olderWarningsTable.getSelectedRow();
        
            // If no row is selected in either table, return
            if(selectedRowSixMonth == -1 && selectedRowOlder == -1)
                return;
        
            // Initialize warningID and selectedTableModel
            String warningID = null;
            DefaultTableModel selectedTableModel = null;
        
            // Check which table has a selected row and get the warningID and model
            if(selectedRowSixMonth != -1) {
                warningID = sixMonthWarningsTable.getModel().getValueAt(selectedRowSixMonth, 3).toString();
                selectedTableModel = (DefaultTableModel)sixMonthWarningsTable.getModel();
            } else if(selectedRowOlder != -1) {
                warningID = olderWarningsTable.getModel().getValueAt(selectedRowOlder, 3).toString();
                selectedTableModel = (DefaultTableModel)olderWarningsTable.getModel();
            }
        
            // Remove from DB
            db.database.conflicts.get("Warnings").put(warningID, null);
            db.asyncPush();
        
            // Remove from table
            selectedTableModel.removeTableModelListener(dbUpdater);
            if(selectedRowSixMonth != -1) {
                selectedTableModel.removeRow(selectedRowSixMonth);
            } else if(selectedRowOlder != -1) {
                selectedTableModel.removeRow(selectedRowOlder);
            }
            selectedTableModel.addTableModelListener(dbUpdater);
        });

        // Add left-side elements
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.gridx = 0;
        c.weightx = 1;

        JPanel p1 = new JPanel(new GridLayout(1, 2));
        JPanel p2 = new JPanel(new GridLayout(1, 2));
        JPanel p3 = new JPanel(new GridLayout(1, 2));

        p1.add(addWarningButton);
        p1.add(removeWarningButton);
        this.add(p1, c);
        
        this.add(warningsGuestNameLabel, c);
        this.add(noWarningsHeader, c);

        p2.add(lastSixMonthsLabel, c);
        p2.add(lastSixMonthsTotalLabel, c);
        this.add(p2, c);

        this.add(sixMonthWarningsTable.getTableHeader(), c);
        this.add(sixMonthWarningsTable, c);

        p3.add(olderLabel, c);
        p3.add(olderTotalLabel, c);
        this.add(p3, c);

        this.add(olderWarningsTable.getTableHeader(), c);

        this.add(olderWarningsTable, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(new JLabel(), c);

        setActiveGuestID(null);
    }
        
    private void updateTables()
    {
        this.setActiveGuestID(activeGuestID);
    }

    public void setActiveGuestID(String activeGuestID) {
        this.activeGuestID = activeGuestID;
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

        if (db.database.conflicts.get("Warnings") == null) {
            db.database.conflicts.put("Warnings", new HashMap<>());
        }

        List<Entry<String,Map<String,String>>> warningData = DBConnectorV2.filterEntriesByKeyValuePair(db.database.conflicts.get("Warnings"), "GuestId", activeGuestID);

        guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());


        warningsGuestNameLabel.setText("Guest Name: " + guestNameString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date sixMonthsAgo = new Date(System.currentTimeMillis() - 6L * 30 * 24 * 60 * 60 * 1000); // 6 months ago

        List<Entry<String,Map<String,String>>> sixMonthWarnings = new ArrayList<>();
        List<Entry<String,Map<String,String>>> olderWarnings = new ArrayList<>();

        if(warningData != null)
        {
            for (Entry<String, Map<String, String>> warning : warningData) {
                try {
                    Date warningDate = sdf.parse(warning.getValue().get(WarningDBKeys.DATE.getKey()));
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
            WarningDBKeys.NOTES.getPrettyName(),
            "ID"
        };

        boolean shouldShowSixMonthWarnings = !sixMonthWarnings.isEmpty();
        boolean shouldShowOlderWarnings = !olderWarnings.isEmpty();

        if (shouldShowSixMonthWarnings) {
            sixMonthWarningsTable.setModel(createWarningsTable(sixMonthWarnings, columnNames));

            sixMonthWarningsTable.getModel().addTableModelListener(dbUpdater);
            
            // make the ID column invisible
            sixMonthWarningsTable.getColumnModel().removeColumn(sixMonthWarningsTable.getColumnModel().getColumn(3));

            lastSixMonthsTotalLabel.setText("Total: " + String.valueOf(sixMonthWarnings.size()));
        }

        sixMonthWarningsTable.setVisible(shouldShowSixMonthWarnings);
        sixMonthWarningsTable.getTableHeader().setVisible(shouldShowSixMonthWarnings);
        lastSixMonthsLabel.setVisible(shouldShowSixMonthWarnings);
        lastSixMonthsTotalLabel.setVisible(shouldShowSixMonthWarnings);

        if (shouldShowOlderWarnings) {
            olderWarningsTable.setModel(createWarningsTable(olderWarnings, columnNames));

            olderWarningsTable.getModel().addTableModelListener(dbUpdater);

            // make the ID column invisible
            olderWarningsTable.getColumnModel().removeColumn(olderWarningsTable.getColumnModel().getColumn(3));

            olderTotalLabel.setText("Total: " + String.valueOf(olderWarnings.size()));
        }

        olderWarningsTable.setVisible(shouldShowOlderWarnings);
        olderWarningsTable.getTableHeader().setVisible(shouldShowOlderWarnings);
        olderLabel.setVisible(shouldShowOlderWarnings);
        olderTotalLabel.setVisible(shouldShowOlderWarnings);

        noWarningsHeader.setVisible(!shouldShowSixMonthWarnings && !shouldShowOlderWarnings);
    }
    
    private DefaultTableModel createWarningsTable(List<Entry<String, Map<String, String>>> data, String[] columnNames) {
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
            dataArray[i][0] = row.getValue().get(WarningDBKeys.DATE.getKey());
            dataArray[i][1] = row.getValue().get(WarningDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][2] = row.getValue().get(WarningDBKeys.NOTES.getKey());
            dataArray[i][3] = row.getKey();
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
