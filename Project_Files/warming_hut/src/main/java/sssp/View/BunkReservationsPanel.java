package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class BunkReservationsPanel {
    // data fields in the Bunk Info panel

    public static final String RESERVED = "RESERVED";
    public static final String RESERVED_BUNK = "ReservedBunk";
    public static final String RESERVED_BUNK_SLOT = "ReservedBunkSlot";
    private static final JLabel bunkReservationTitle = new JLabel("Bunk Reservation:");
    private static final JLabel bunkReservationLabel = new JLabel("Bunk:");
    private static final JLabel bedReservationLabel = new JLabel("Bed:");
    private static final JLabel bunkReservedSinceLabel = new JLabel("Reserved Since: ");
    private static final JLabel lastAssignedLabel = new JLabel("Last Assigned Bunk: ");
    private static final JComboBox<String> bedSlot = new JComboBox<>(new String[]{"A", "B"});
    private static JComboBox<String[]> bunkReservationCombo = new JComboBox<>();
    private static String activeGuestId;

    public static void setObjKey(String guestId){
        if(activeGuestId != guestId){
            activeGuestId = guestId;
            removeListeners();
            Map<String, String> guest = DBConnectorV2Singleton.getInstance().database.guests.get(activeGuestId);
            if (guest.get(RESERVED_BUNK_SLOT) != null) {
                int ABedSlot = (guest.get(RESERVED_BUNK_SLOT).equals("A")) ? 0 : 1;
                bedSlot.setSelectedIndex(ABedSlot);
            } else {
                bedSlot.setSelectedIndex(0);
            }
            bunkReservationCombo.setModel(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
            setActiveGuestReservedBunkIndex();
            addActionListeners();

            Database db = DBConnectorV2Singleton.getInstance().database;
            lastAssigned.setText(BunkAssignmentPanel.getLastAssignedBunk(activeGuestId, false));
        }

    }

    private static final JLabel lastAssigned = new JLabel(BunkAssignmentPanel.getLastAssignedBunk(activeGuestId, false));
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

        bunkReservationCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
        bunkReservationCombo.setRenderer(new ComboBoxRenderer());
        bunkReservationCombo.setPreferredSize(new Dimension(225, 30));
        bunkReservationCombo.setSelectedItem(null);

        addActionListeners();

        JLabel[] rowLabels = {bedReservationLabel, bunkReservationLabel, lastAssignedLabel};
        for(JLabel label : rowLabels){
            label.setFont(new Font("serif", Font.PLAIN, 18));
        }
        bunkReservationTitle.setFont(new Font("serif", Font.BOLD, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 10, 0 ,10);
        c.anchor = GridBagConstraints.WEST;
        panel.add(bunkReservationTitle, c);
        c.gridy = 1;
        panel.add(bedReservationLabel, c);
        c.gridx = 1;
        panel.add(bedSlot, c);
        c.gridy = 2;
        c.gridx = 0;
        panel.add(bunkReservationLabel, c);
        c.gridx = 1;
        panel.add(bunkReservationCombo, c);
        c.gridy = 3;
        c.gridx = 0;
        c.insets = new Insets(15, 10, 0 ,10);
        panel.add(lastAssignedLabel, c);
        c.gridx = 1;
        panel.add(lastAssigned, c);

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
