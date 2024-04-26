package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Model.AttributesDBKeys;

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

        String[] columnNames = {"Date", "Name", "Emergency Shelter", "Laundry", "CaseWorthy", "HMIS"};
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

/*
        for (Map<String,String> oneCheckin : checkinsToReport) {

        }
*/
//
//        String[] rowData = {oneCheckin.get("FirstName") + " " + guest.get("LastName"),
//                locker, storage, bunk, issue}; //TODO add updated guest info data here
//        tableModel.addRow(rowData);
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
