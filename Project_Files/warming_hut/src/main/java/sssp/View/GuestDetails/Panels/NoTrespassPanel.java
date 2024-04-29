package sssp.View.GuestDetails.Panels;

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

import java.util.Date;
import java.util.HashMap;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.UUIDGenerator;
import sssp.Model.GuestDBKeys;
import sssp.Model.NoTrespassDBKeys;


public class NoTrespassPanel extends JPanel {
    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    String activeGuestID = null;

    private final JLabel noTrespassLabel = new JLabel("No Trespass Orders");
    private final JLabel noNoTrespassesLabel = new JLabel("No Orders To Display");
    JLabel noTrespassGuestNameLabel = new JLabel("Guest Name: None");
    JTable noTrespassTable;

    private static final String[] noTrespassColumnNames = {
        NoTrespassDBKeys.DATE_OF_INCIDENT.getPrettyName(),
        NoTrespassDBKeys.NO_TRESPASS_FROM.getPrettyName(),
        NoTrespassDBKeys.STAFF_INITIALS.getPrettyName(),
        NoTrespassDBKeys.BPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.LPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.CW_ALERT.getPrettyName(),
        NoTrespassDBKeys.NOTES.getPrettyName(),
        "ID"
    };

    TableModelListener dbUpdater = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if(activeGuestID == null)
                return;

            DefaultTableModel model = (DefaultTableModel)e.getSource();
            int row = e.getFirstRow();

            String noTrespassID = model.getValueAt(row, 7).toString();

            Map<String, String> noTrespass = db.database.conflicts.get("NoTrespass").get(noTrespassID);
            noTrespass.put(NoTrespassDBKeys.DATE_OF_INCIDENT.getKey(), model.getValueAt(row, 0).toString());
            noTrespass.put(NoTrespassDBKeys.NO_TRESPASS_FROM.getKey(), model.getValueAt(row, 1).toString());
            noTrespass.put(NoTrespassDBKeys.STAFF_INITIALS.getKey(), model.getValueAt(row, 2).toString());
            noTrespass.put(NoTrespassDBKeys.BPD_STATUS.getKey(), model.getValueAt(row, 3).toString());
            noTrespass.put(NoTrespassDBKeys.LPD_STATUS.getKey(), model.getValueAt(row, 4).toString());
            noTrespass.put(NoTrespassDBKeys.CW_ALERT.getKey(), model.getValueAt(row, 5).toString());
            noTrespass.put(NoTrespassDBKeys.NOTES.getKey(), model.getValueAt(row, 6).toString());

            db.database.conflicts.get("NoTrespass").put(noTrespassID, noTrespass);
            db.asyncPush();
        }
    };

    public NoTrespassPanel()
    {
        super(new GridBagLayout());

        db.subscribeRunnableToDBUpdate(this::updateTables);

        noTrespassGuestNameLabel.setOpaque(true);

        noTrespassLabel.setBackground(Color.white);
        noTrespassLabel.setOpaque(true);

        noNoTrespassesLabel.setBackground(Color.white);
        noNoTrespassesLabel.setOpaque(true);
        noNoTrespassesLabel.setVisible(false);

        // Initializing the JTable
        DefaultTableModel model = createTrespassTableModel(null, noTrespassColumnNames);
        noTrespassTable = new JTable(model);
        noTrespassTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        noTrespassTable.setRowHeight(50);
        noTrespassTable.getColumnModel().getColumn(0).setMaxWidth(250);
        noTrespassTable.getColumnModel().getColumn(1).setMaxWidth(250);
        noTrespassTable.getColumnModel().getColumn(2).setMaxWidth(75);
        noTrespassTable.getColumnModel().getColumn(3).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(4).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(5).setMaxWidth(100);
        noTrespassTable.getColumnModel().getColumn(6).setMinWidth(100);
        noTrespassTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JButton addTrespassButton = new JButton("Add No Trespass Order");

        addTrespassButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;

            String thisID = UUIDGenerator.getNewUUID();

            db.database.conflicts.get("NoTrespass").put(thisID, Map.of(
                NoTrespassDBKeys.DATE_OF_INCIDENT.getKey(), new SimpleDateFormat("MM/dd/yyyy").format(new Date()),
                NoTrespassDBKeys.NO_TRESPASS_FROM.getKey(), "",
                NoTrespassDBKeys.STAFF_INITIALS.getKey(), "",
                NoTrespassDBKeys.BPD_STATUS.getKey(), "",
                NoTrespassDBKeys.LPD_STATUS.getKey(), "",
                NoTrespassDBKeys.CW_ALERT.getKey(), "",
                NoTrespassDBKeys.NOTES.getKey(), "",
                NoTrespassDBKeys.GUEST_ID.getKey(), activeGuestID
            ));

            db.asyncPush();

            DefaultTableModel model1 = (DefaultTableModel)noTrespassTable.getModel();

            model1.removeTableModelListener(dbUpdater);
            model1.addRow(new Object[] {
                new SimpleDateFormat("MM/dd/yyyy").format(new Date()),
                "",
                "",
                "",
                "",
                "",
                "",
                thisID
            });
            model1.addTableModelListener(dbUpdater);
        });

        JButton removeTrespassButton = new JButton("Remove No Trespass Order");

        removeTrespassButton.addActionListener(e -> {
            if(activeGuestID == null)
                return;

            DefaultTableModel model1 = (DefaultTableModel)noTrespassTable.getModel();
            int[] rows = noTrespassTable.getSelectedRows();

            for(int i = rows.length - 1; i >= 0; i--)
            {
                String noTrespassID = model1.getValueAt(rows[i], 7).toString();
                db.database.conflicts.get("NoTrespass").put(noTrespassID, null);

                model1.removeTableModelListener(dbUpdater);
                model1.removeRow(rows[i]);
                model1.addTableModelListener(dbUpdater);
            }

            db.asyncPush();
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.gridwidth = 1;
        c.gridy = 0;
        c.gridx = 0;
        this.add(addTrespassButton, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 1;
        this.add(removeTrespassButton, c);

        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;

        this.add(noTrespassGuestNameLabel, c);
        this.add(noNoTrespassesLabel, c);
        this.add(noTrespassLabel, c);
        this.add(noTrespassTable.getTableHeader(), c);
        this.add(noTrespassTable, c);

        // Add padding below the panel
        c.weighty = 1.0;
        c.gridy = GridBagConstraints.RELATIVE;
        this.add(new JLabel(), c);

        this.setActiveGuestID(null);
    }

    public void setActiveGuestID(String activeGuestID) {
        this.activeGuestID = activeGuestID;

        if (activeGuestID == null) {        
            noTrespassGuestNameLabel.setText("Guest Name: " + "None");
            noTrespassTable.setVisible(false);
            noTrespassTable.getTableHeader().setVisible(false);
            noTrespassLabel.setVisible(false); 
            noNoTrespassesLabel.setVisible(true); 
            return;
        }

        Map<String,String> activeGuestData = db.database.guests.get(activeGuestID);

        if (db.database.conflicts.get("NoTrespass") == null) {
            db.database.conflicts.put("NoTrespass", new HashMap<>());
        }

        List<Entry<String,Map<String,String>>> trespassData = DBConnectorV2.filterEntriesByKeyValuePair(db.database.conflicts.get("NoTrespass"), "GuestId", activeGuestID);
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());

        noTrespassGuestNameLabel.setText("Guest Name: " + guestNameString);


        boolean shouldShowNoTrespass = trespassData != null && !trespassData.isEmpty();
        
        if (shouldShowNoTrespass) {
            noTrespassTable.setModel(createTrespassTableModel(trespassData, noTrespassColumnNames));

            noTrespassTable.getModel().addTableModelListener(dbUpdater);

            noTrespassTable.getColumnModel().removeColumn(noTrespassTable.getColumnModel().getColumn(7));
        }

        noTrespassTable.setVisible(shouldShowNoTrespass);
        noTrespassTable.getTableHeader().setVisible(shouldShowNoTrespass);
        noTrespassLabel.setVisible(shouldShowNoTrespass);
    
        noNoTrespassesLabel.setVisible(!shouldShowNoTrespass);
    }

    private DefaultTableModel createTrespassTableModel(List<Entry<String, Map<String, String>>> data, String[] columnNames) {
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
            dataArray[i][0] = row.getValue().get(NoTrespassDBKeys.DATE_OF_INCIDENT.getKey());
            dataArray[i][1] = row.getValue().get(NoTrespassDBKeys.NO_TRESPASS_FROM.getKey());
            dataArray[i][2] = row.getValue().get(NoTrespassDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][3] = row.getValue().get(NoTrespassDBKeys.BPD_STATUS.getKey());
            dataArray[i][4] = row.getValue().get(NoTrespassDBKeys.LPD_STATUS.getKey());
            dataArray[i][5] = row.getValue().get(NoTrespassDBKeys.CW_ALERT.getKey());
            dataArray[i][6] = row.getValue().get(NoTrespassDBKeys.NOTES.getKey());
            dataArray[i][7] = row.getKey();
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

    private void updateTables() {
        setActiveGuestID(activeGuestID);
    }
}
