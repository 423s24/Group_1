package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Model.AttributesDBKeys;
import sssp.Model.CheckinsDBKeys;
import sssp.Model.GuestDBKeys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExternalDataReportingPanel extends JPanel {

    public static JPanel externalDataReportingPanel;
    private final DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
    private JPanel reportingPanel;
    private JTable reportingTable;

    public JPanel createExternalDataReportingPanel() {
        //JPanel panel = new JPanel(new BorderLayout());

        // Data
        reportingPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Date", "Name", "Services Only", "Laundry", "CaseWorthy", "HMIS"};
        Object[][] data = {};

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        reportingTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(reportingTable);
        reportingPanel.add(scrollPane, BorderLayout.CENTER);

        updateReportingTable();

        return reportingPanel;
    }

    /**
     * Updates the reporting table with the latest data from the database.
     */
    private void updateReportingTable() {
        List<Map<String, String>> checkinsToReport = db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).values().stream().toList();

        // Update the reporting table
        DefaultTableModel tableModel = (DefaultTableModel) reportingTable.getModel();

        // Clear the table
        tableModel.setRowCount(0);

        // set up strings for table params

        for (Map<String,String> oneCheckin : checkinsToReport) {
            if(!oneCheckin.containsKey(CheckinsDBKeys.GUEST_ID.getKey()))
            {
                continue;
            }

            //get Date
            String date = oneCheckin.get("Date");
            String guestName;
            // get Emergency Shelter Status
            String servicesOnly="", laundry="";
            String caseWorthy ="", hmis = "";

            String checkinGuestId = oneCheckin.get(CheckinsDBKeys.GUEST_ID.getKey());
            Map<String, String> guest = db.database.guests.get(checkinGuestId);
            guestName = guest.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + guest.get(GuestDBKeys.LAST_NAME.getKey());

            servicesOnly = oneCheckin.get(CheckinsDBKeys.SERVICES_ONLY.getKey());
            laundry = oneCheckin.get(CheckinsDBKeys.LAUNDRY.getKey());
            caseWorthy = oneCheckin.get(CheckinsDBKeys.CASEWORTHY_ENTERED.getKey());
            hmis = oneCheckin.get(CheckinsDBKeys.HMIS_ENTERED.getKey());

            String[] rowData = {date, guestName, servicesOnly, laundry, caseWorthy, hmis};
            tableModel.addRow(rowData);
        }
    }

//    private List<Map<String, String>> getCheckinsToReport() {
//        if (db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()) == null)
//            return null;
//
//        List<Map<String, String>> checkinsToReport = db.database.attributes.get(AttributesDBKeys.CHECK_INS.getKey()).values().stream().toList();
//        return checkinsToReport;
//    }

    private void onDatabasePut()
    {
        updateReportingTable();
    }
}
