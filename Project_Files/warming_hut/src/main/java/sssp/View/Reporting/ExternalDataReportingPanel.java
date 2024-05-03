package sssp.View.Reporting;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.DateHelper;
import sssp.Model.AttributesDBKeys;
import sssp.Model.CheckinsDBKeys;
import sssp.Model.GuestDBKeys;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExternalDataReportingPanel extends JPanel {

    public static JPanel externalDataReportingPanel;
    private final DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
    private JPanel reportingPanel;
    private JTable reportingTable;

    public JPanel createExternalDataReportingPanel() {
        //JPanel panel = new JPanel(new BorderLayout());

        db.subscribeRunnableToDBUpdate(this::onDatabasePut);

        // Data
        reportingPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Date", "Name", "Services Only", "Laundry", "CaseWorthy", "HMIS"};
        Object[][] data = {};

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        reportingTable = new JTable(tableModel) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0,1:
                        return String.class;
                    case 2,3,4,5:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
        reportingTable.getModel().addTableModelListener(dbUpdater);

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

        tableModel.removeTableModelListener(dbUpdater);
        // Clear the table
        tableModel.setRowCount(0);

        // set up strings for table params

        for (Map<String,String> oneCheckin : checkinsToReport) {
            if(!Boolean.parseBoolean(oneCheckin.get("ShouldDisplay")))
                continue;

            if(!oneCheckin.containsKey(CheckinsDBKeys.GUEST_ID.getKey()))
            {
                continue;
            }

            // get Date
            String date = oneCheckin.get("Date");

            Instant dateInstant = Instant.parse(date);
            Date d = Date.from(dateInstant);
            date = d.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
            // format the date
            String formatDate = sdf.format(d);
            String guestName;
            // get Emergency Shelter Status, Laundry status, and the two External Database booleans
            Boolean servicesOnly, laundry, caseWorthy, hmis;

            String checkinGuestId = oneCheckin.get(CheckinsDBKeys.GUEST_ID.getKey());
            Map<String, String> guest = db.database.guests.get(checkinGuestId);
            guestName = guest.get(GuestDBKeys.FIRST_NAME.getKey()) + " " + guest.get(GuestDBKeys.LAST_NAME.getKey());

            servicesOnly = Boolean.parseBoolean(oneCheckin.get(CheckinsDBKeys.SERVICES_ONLY.getKey()));
            laundry = Boolean.parseBoolean(oneCheckin.get(CheckinsDBKeys.LAUNDRY.getKey()));
            caseWorthy = Boolean.parseBoolean(oneCheckin.get(CheckinsDBKeys.CASEWORTHY_ENTERED.getKey()));
            hmis = Boolean.parseBoolean(oneCheckin.get(CheckinsDBKeys.HMIS_ENTERED.getKey()));

            Object[] rowData = {formatDate, guestName, servicesOnly, laundry, caseWorthy, hmis};
            tableModel.addRow(rowData);
        }
        reportingTable.getModel().addTableModelListener(dbUpdater);
    }

    private String getGuestTableKey(String guestName) {
        return "Guest_" + guestName.hashCode();
    }

    TableModelListener dbUpdater = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            DefaultTableModel model = (DefaultTableModel)e.getSource();
            int row = e.getFirstRow();

            String guestId = getGuestTableKey(model.getValueAt(row, 1).toString());
            String thisDate = model.getValueAt(row, 0).toString();

            List<Map.Entry<String,Map<String,String>>> entries = db.database.attributes.get("Checkins").entrySet().stream().toList();

            entries = entries.stream().filter(entry ->
            {
                if(entry.getValue().containsKey("Warning"))
                    return false;

                return entry.getValue().get("GuestId").equals(guestId);
            }).toList();
            entries = entries.stream().filter(entry ->
                    {
                        Instant storedInstant = Instant.parse(entry.getValue().get("Date"));
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
                        String storedDateTruncated = sdf.format(DateHelper.truncateToDay(Date.from(storedInstant)));
                        return thisDate.equals(storedDateTruncated);
                    }
            ).toList();

            Map.Entry<String,Map<String,String>> entry = entries.get(0);


            String checkinID = entry.getKey();

            Map<String, String> checkin = entry.getValue();
            checkin.put(CheckinsDBKeys.SERVICES_ONLY.getKey(), Boolean.toString(((Boolean) model.getValueAt(row, 2))));
            checkin.put(CheckinsDBKeys.LAUNDRY.getKey(), Boolean.toString(((Boolean) model.getValueAt(row, 3))));
            checkin.put(CheckinsDBKeys.CASEWORTHY_ENTERED.getKey(), Boolean.toString(((Boolean) model.getValueAt(row, 4))));
            checkin.put(CheckinsDBKeys.HMIS_ENTERED.getKey(), Boolean.toString(((Boolean) model.getValueAt(row, 5))));

            if(((Boolean) model.getValueAt(row, 4) && ((Boolean) model.getValueAt(row, 5))))
            {
                // set timer
                Timer t = new Timer(30000, e1 -> {
                    db.database.attributes.get("Checkins").get(checkinID).put(CheckinsDBKeys.SHOULD_DISPLAY.getKey(), Boolean.toString(false));
                    db.asyncPush();
                    updateReportingTable();
                });
                t.setRepeats(false);
                t.start();
            }

            db.database.attributes.get("Checkins").put(checkinID, checkin);
            db.asyncPush();
        }
    };

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
