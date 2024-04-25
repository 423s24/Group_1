package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BunkReservationsPanel {
    // data fields in the Bunk Info panel

    public static final String RESERVED = "RESERVED";
    private static final String RESERVED_BUNK = "ReservedBunk";
    private static final String RESERVED_BUNK_SLOT = "ReservedBunkSlot";
    private static final JLabel bunkReservationLabel = new JLabel("Bunk Reservation:");
    private static final JLabel bunkReservedSinceLabel = new JLabel("Reserved Since: ");
    private static final JLabel lastAssignedLabel = new JLabel("Last Assigned Bunk: ");
    private static final JComboBox<String> bedSlot = new JComboBox<>(new String[]{"A", "B"});
    private static JComboBox<String[]> bunkReservationCombo = new JComboBox<>();
    private static String activeGuestId;

    public static void setObjKey(String guestId){
        if(activeGuestId != guestId){
            activeGuestId = guestId;
            removeListeners();
            bedSlot.setSelectedIndex(0);
            bunkReservationCombo.setModel(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
            setActiveGuestReservedBunkIndex();
            addActionListeners();
        }

    }

    private static ActionListener bedSlotListener;
    private static ActionListener bunkReservationListener;
    private static void removeListeners(){
        for(ActionListener listener : bedSlot.getActionListeners()){
            bedSlot.removeActionListener(listener);
        }
        for(ActionListener listener : bunkReservationCombo.getActionListeners()){
            bunkReservationCombo.removeActionListener(listener);
        }
    }

    private static void addActionListeners(){
        bedSlot.addActionListener(bedSlotListener);
        bunkReservationCombo.addActionListener(bunkReservationListener);
    }

    public static JPanel getBunkReservationsPanel(String guestId) {

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        bunkReservationListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(bunkReservationCombo.getSelectedItem() != null && bedSlot.getSelectedItem() != null){
                    if(Arrays.stream(bunkHeaders).toList().contains(((String[]) Objects.requireNonNull(bunkReservationCombo.getSelectedItem()))[0])){
                        bunkReservationCombo.setSelectedIndex(0);
                    } else if (((String[])bunkReservationCombo.getSelectedItem())[1].equals(RESERVED)){
                        bunkReservationCombo.setSelectedIndex(0);
                    } else if(db.database.guests.containsKey(activeGuestId)){
                        String newBunkReservation = ((String[])bunkReservationCombo.getSelectedItem())[1];
                        db.database.guests.get(activeGuestId).put(RESERVED_BUNK, newBunkReservation);
                        db.database.guests.get(activeGuestId).put(RESERVED_BUNK_SLOT, bedSlot.getSelectedItem().toString());
                        db.asyncPush();
                        System.out.println(Arrays.stream(bunkReservationCombo.getActionListeners()).count());
                    }
                }
            }
        };
        bedSlotListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bunkReservationCombo.setModel(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
                bunkReservationCombo.setSelectedIndex(0);
            }
        };


        setObjKey(guestId);
        JPanel panel = new JPanel(new GridBagLayout());
        Map<String, String> guest = db.database.guests.get(activeGuestId);
        if(guest == null){
            guest = new HashMap<>();
        }

        JLabel reservedSince = new JLabel("NA");
        String reservedBunk = guest.get(RESERVED_BUNK);
        String reservedDate = guest.get("ReservedDate");
        if(reservedDate != null){
            if(reservedBunk != null && !reservedBunk.equals("None")){
                reservedSince.setText(reservedDate);
            }
        }

        JLabel lastAssigned = new JLabel("None in last 30 days");
        JLabel lastAssignedDate = new JLabel("NA");
        updateLastAssignedBunkId(activeGuestId);
        if(lastAssignedBunkId != null){
            lastAssigned.setText(db.database.bunkList.get(lastAssignedBunkId).get("BunkNum"));
        }
        if(lastAssignedBunkDate != null){
            lastAssignedDate.setText("(" + lastAssignedBunkDate + ")");
        }

        bunkReservationCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
        bunkReservationCombo.setRenderer(new ComboBoxRenderer());
        bunkReservationCombo.setPreferredSize(new Dimension(225, 30));
        bunkReservationCombo.setSelectedItem(null);

        addActionListeners();

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
    private static ArrayList<String[]> getAvailableBunks(){
        removeListeners();
        Database db = DBConnectorV2Singleton.getInstance().database;
        boolean bunkSlotA = Objects.equals(bedSlot.getSelectedItem(), "A");

        mensBunkList.clear();
        womensBunkList.clear();
        observationBunkList.clear();

        HashMap<String, String> reservedBunks = new HashMap<>();
        for(String guestId : db.guests.keySet()) {
            if(guestId.equals(activeGuestId)){
                continue;
            }
            Map<String, String> guest = db.guests.get(guestId);
            if (guest.get(RESERVED_BUNK) != null && guest.get(RESERVED_BUNK_SLOT) != null) {
                if (bunkSlotA == guest.get(RESERVED_BUNK_SLOT).equals("A")) {
                    reservedBunks.put(guest.get(RESERVED_BUNK), guestId);
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
        availableBunks.add(new String[] {"NONE", "NONE"});
        int bunkListType = 0;
        for(List<String[]> bunkList : allBunkLists){
            switch (bunkListType) {
                case 0 -> availableBunks.add(new String[]{bunkHeaders[0], ""});
                case 1 -> availableBunks.add(new String[]{bunkHeaders[1], ""});
                default -> availableBunks.add(new String[]{bunkHeaders[2], ""});
            }
            for(String[] bunk : bunkList){
                if(!reservedBunks.containsKey(bunk[1])){
                    availableBunks.add(new String[]{bunk[0], bunk[1]});
                } else {
                    Map<String, String> guest = db.guests.get(reservedBunks.get(bunk[1]));
                    String bunkWithAssignment = bunk[0] + " | " + guest.get("FirstName") + " " + guest.get("LastName");
                    availableBunks.add(new String[]{bunkWithAssignment, RESERVED});
                }
            }
            bunkListType++;
        }

        addActionListeners();
        return availableBunks;
    }

    private static void setActiveGuestReservedBunkIndex(){
        removeListeners();
        if(activeGuestId != null){
            Database db = DBConnectorV2Singleton.getInstance().database;
            for(String guestId : db.guests.keySet()) {
                if(guestId.equals(activeGuestId)){
                    Map<String, String> guest = db.guests.get(guestId);
                    if (guest.get(RESERVED_BUNK) != null && guest.get(RESERVED_BUNK_SLOT) != null) {
                        int ABedSlot = (guest.get(RESERVED_BUNK_SLOT).equals("A")) ? 0 : 1;
                        bedSlot.setSelectedIndex(ABedSlot);
                        for(int i = 0; i<bunkReservationCombo.getItemCount(); i++){
                            if(((String[])bunkReservationCombo.getItemAt(i))[1].equals(guest.get(RESERVED_BUNK))){
                                bunkReservationCombo.setSelectedIndex(i);
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            bunkReservationCombo.setSelectedIndex(-1);
        }
        addActionListeners();
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

    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer<String[]> {

        private Color selectionBackgroundColor;
        ComboBoxRenderer(){
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            selectionBackgroundColor = Color.white;
            if(value != null){
            if(Arrays.stream(bunkHeaders).toList().contains(value[0])) {
                selectionBackgroundColor = Color.lightGray;
            } else if (value[1].equals(RESERVED)){
                selectionBackgroundColor = Color.red;
            }
            setText(value[0]);
            } else {
                setText("");
            }
            return this;
        }

        @Override
        public boolean isOpaque() {
            return true;
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Color getBackground() {
            return selectionBackgroundColor == null ? super.getBackground() : selectionBackgroundColor;
        }
    }
}
