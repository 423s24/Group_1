package sssp.View;

import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BunkReservationsPanel {
    // data fields in the Bunk Info panel
    private static final JLabel bunkReservationLabel = new JLabel("Bunk Reservation");
    private static final JLabel bunkReservedSinceLabel = new JLabel("Reserved Since: ");
    private static final JLabel lastAssignedLabel = new JLabel("Last Assigned Bunk: ");
    private static String activeGuestId;

    public static void setObjKey(String guestId){
        activeGuestId = guestId;
    }
    public static JPanel getBunkReservationsPanel(String guestId) {
        setObjKey(guestId);
        JPanel panel = new JPanel(new GridBagLayout());
        Database db = DBConnectorV2Singleton.getInstance().database;
        Map<String, String> guest = db.guests.get(guestId);
        if(guest == null){
            guest = new HashMap<>();
        }

        JComboBox<String> bedSlot = new JComboBox<>(new String[]{"A", "B"});

        JLabel reservedSince = new JLabel("NA");
        String reservedBunk = guest.get("ReservedBunk");
        String reservedDate = guest.get("ReservedDate");
        if(reservedDate != null){
            if(reservedBunk != null && !reservedBunk.equals("None")){
                reservedSince.setText(reservedDate);
            }
        }

        JLabel lastAssigned = new JLabel("None in last 30 days");
        JLabel lastAssignedDate = new JLabel("NA");
        updateLastAssignedBunkId(guestId);
        if(lastAssignedBunkId != null){
            lastAssigned.setText(db.bunkList.get(lastAssignedBunkId).get("BunkNum"));
        }
        if(lastAssignedBunkDate != null){
            lastAssignedDate.setText("(" + lastAssignedBunkDate + ")");
        }

        JComboBox<String[]> bunkReservationCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks(bedSlot).toArray(new String[0][0])));
        bunkReservationCombo.setRenderer(new BunkAssignmentPanel.ComboBoxRenderer());
        bunkReservationCombo.setPreferredSize(new Dimension(225, 30));
        bunkReservationCombo.setSelectedItem(null);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 10 ,10);
        c.anchor = GridBagConstraints.WEST;
        panel.add(bunkReservationLabel, c);
        c.gridx = 1;
        panel.add(bedSlot, c);
        c.gridx = 2;
        panel.add(bunkReservationCombo, c);
        c.gridy = 1;
        c.gridx = 0;
        panel.add(bunkReservedSinceLabel, c);
        c.gridx = 1;
        panel.add(reservedSince, c);
        c.gridy = 2;
        c.gridx = 0;
        panel.add(lastAssignedLabel, c);
        c.gridx = 1;
        panel.add(lastAssigned, c);
        c.gridx = 2;
        panel.add(lastAssignedDate, c);

        return panel;
    }

    private static final ArrayList<String[]> mensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> womensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> observationBunkList = new ArrayList<>();
    private static final String[] bunkAreas = {"Mens", "Womens", "Observational"};
    private static final ArrayList<String[]>[] allBunkLists = new ArrayList[] {mensBunkList, womensBunkList, observationBunkList};
    private static final String[] bunkHeaders = new String[] {"Men's Bunks: ", "Women's Bunks: ", "Observation Area: "};
    private static ArrayList<String[]> getAvailableBunks(JComboBox<String> bunkSlot){
        Database db = DBConnectorV2Singleton.getInstance().database;
        boolean bunkSlotA = Objects.equals(bunkSlot.getSelectedItem(), "A");

        ArrayList<String> reservedBunks = new ArrayList<>();
        for(String guestId : db.guests.keySet()) {
            Map<String, String> guest = db.guests.get(guestId);
            if (guest.get("ReservedBunk") != null && guest.get("ReservedBunkSlot") != null) {
                if (bunkSlotA == guest.get("ReservedBunkSlot").equals("A")) {
                    reservedBunks.add(guest.get("ReservedBunk"));
                }
            }
        }

        for(String key : db.bunkList.keySet()){
            String bunkNum = db.bunkList.get(key).get("BunkNum");

            if(db.bunkList.get(key).get("BunkArea").equals(bunkAreas[0])){
                mensBunkList.add(new String[]{"Bunk " + bunkNum , key});
            } else if(db.bunkList.get(key).get("BunkArea").equals(bunkAreas[1])){
                womensBunkList.add(new String[]{"Bunk " + bunkNum, key});
            } else {
                observationBunkList.add(new String[]{"Bunk " + bunkNum , key});
            }
        }

        ArrayList<String[]> availableBunks = new ArrayList<>();
        int bunkListType = 0;
        for(List<String[]> bunkList : allBunkLists){
            switch (bunkListType) {
                case 0 -> availableBunks.add(new String[]{bunkHeaders[0]});
                case 1 -> availableBunks.add(new String[]{bunkHeaders[1]});
                default -> availableBunks.add(new String[]{bunkHeaders[2]});
            }
            for(String[] bunk : bunkList){
                if(!reservedBunks.contains(bunk[1])){
                    availableBunks.add(new String[]{bunk[0], bunk[1]});
                }
            }
            bunkListType++;
        }
        return availableBunks;
    }

    private static String lastAssignedBunkId = null;
    private static String lastAssignedBunkDate = null;
    private static void updateLastAssignedBunkId(String guestId) {
        Database db = DBConnectorV2Singleton.getInstance().database;
        int dayCounter = 0;
        for(String rosterId : db.guestRoster.keySet()){
            if(dayCounter > 30){
                return;
            }
            Map<String, Map<String, String>> roster = db.guestRoster.get(rosterId);
            if(roster.containsKey(guestId)){
                lastAssignedBunkId = roster.get(guestId).get("BunkAssigned");
                lastAssignedBunkDate = db.attributes.get("GuestRoster").get(rosterId).get("Date");
            }
            dayCounter++;
        }
    }
}
