package sssp.View.GuestDetails.Panels;

import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Model.GuestDBKeys;
import sssp.Model.NoTrespassDBKeys;

public class NoTrespassPanel extends JPanel {
    private final JLabel noTrespassLabel = new JLabel("No Trespass Orders");
    private final JLabel noNoTrespassesLabel = new JLabel("No Orders To Display");
    JLabel noTrespassGuestNameLabel = new JLabel("Guest Name: None");
    JTable noTrespassTable;

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    private static final String[] noTrespassColumnNames = {
        NoTrespassDBKeys.DATE_OF_INCIDENT.getPrettyName(),
        NoTrespassDBKeys.NO_TRESPASS_FROM.getPrettyName(),
        NoTrespassDBKeys.STAFF_INITIALS.getPrettyName(),
        NoTrespassDBKeys.BPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.LPD_STATUS.getPrettyName(),
        NoTrespassDBKeys.CW_ALERT.getPrettyName(),
        NoTrespassDBKeys.NOTES.getPrettyName()
    };

    public NoTrespassPanel()
    {
        super(new GridBagLayout());

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

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = GridBagConstraints.RELATIVE;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

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

        if (activeGuestID == null) {        
            noTrespassGuestNameLabel.setText("Guest Name: " + "None");
            noTrespassTable.setVisible(false);
            noTrespassTable.getTableHeader().setVisible(false);
            noTrespassLabel.setVisible(false); 
            noNoTrespassesLabel.setVisible(true); 
            return;
        }

        Map<String,String> activeGuestData = db.database.guests.get(activeGuestID);
        List<Map<String,String>> trespassData = DBConnectorV2.joinOnKey(db.database.conflicts.get("NoTrespass"), "GuestId", activeGuestID);
        String guestNameString = activeGuestData.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + activeGuestData.get(GuestDBKeys.LAST_NAME.getKey());

        noTrespassGuestNameLabel.setText("Guest Name: " + guestNameString);


        boolean shouldShowNoTrespass = trespassData != null && !trespassData.isEmpty();
        
        if (shouldShowNoTrespass) {
            noTrespassTable.setModel(createTrespassTableModel(trespassData, noTrespassColumnNames));
        }

        noTrespassTable.setVisible(shouldShowNoTrespass);
        noTrespassTable.getTableHeader().setVisible(shouldShowNoTrespass);
        noTrespassLabel.setVisible(shouldShowNoTrespass);
    
        noNoTrespassesLabel.setVisible(!shouldShowNoTrespass);
    }

    private DefaultTableModel createTrespassTableModel(List<Map<String, String>> data, String[] columnNames) {
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
            dataArray[i][0] = row.get(NoTrespassDBKeys.DATE_OF_INCIDENT.getKey());
            dataArray[i][1] = row.get(NoTrespassDBKeys.NO_TRESPASS_FROM.getKey());
            dataArray[i][2] = row.get(NoTrespassDBKeys.STAFF_INITIALS.getKey());
            dataArray[i][3] = row.get(NoTrespassDBKeys.BPD_STATUS.getKey());
            dataArray[i][4] = row.get(NoTrespassDBKeys.LPD_STATUS.getKey());
            dataArray[i][5] = row.get(NoTrespassDBKeys.CW_ALERT.getKey());
            dataArray[i][6] = row.get(NoTrespassDBKeys.NOTES.getKey());
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
