package sssp.View.BunkAssignment.Panels;

import com.toedter.calendar.JDateChooser;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;
import sssp.Helper.DateHelper;
import sssp.View.BunkAssignment.BunkEditorPopup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class BunkAssignmentPanel {

    public static JPanel mainBunkPanel;
    private static JPanel bunkPanel = new JPanel(new GridBagLayout());
    private static GridBagConstraints bpc = new GridBagConstraints();
    public static final JDateChooser dateChooser = new JDateChooser(new Date());

    public static JPanel getBunkAssignmentPanel(){
        JPanel scrollPanel = new JPanel(new GridBagLayout());
        ScrollPane scrollPane = new ScrollPane();

        bunkPanel = new JPanel(new GridBagLayout());
        bpc = new GridBagConstraints();
        reservedBunksA = new ArrayList<>();
        reservedBunksB = new ArrayList<>();
        guestsAdded = new ArrayList<>();
        guestsAdded = new ArrayList<>();
        checkedInGuests = new ArrayList<>();
        bunkAssignmentRows = new ArrayList<>();
        rowNum = 2;

        JLabel GuestLabel = new JLabel("Guest");
        JLabel BunkLabel = new JLabel("Bunk Assignment");
        JLabel bedSlotLabel = new JLabel("Bed Slot");
        JLabel ReservationLabel = new JLabel("Reservation");
        JLabel PreviouslyAssignedLabel = new JLabel("Previously Assigned");
        JLabel[] labels = {GuestLabel, BunkLabel, ReservationLabel, PreviouslyAssignedLabel, bedSlotLabel};
        JButton bunkEditPopup = new JButton("Edit Bunks");

        bunkEditPopup.addActionListener(e -> {
            BunkEditorPopup.showBunkEditorPopup();
        });

        for (JLabel label : labels) {
            label.setFont(new Font("Serif", Font.BOLD, 24));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Format of the selected date (Month/Day/Year)

        JLabel dateLabel = new JLabel("Date: ");
        dateLabel.setFont(new Font("serif", Font.BOLD, 16));

        JPanel datePanel = new JPanel(new GridBagLayout());
        GridBagConstraints dateC = new GridBagConstraints();
        dateC.gridx = 0;
        datePanel.add(dateLabel, dateC);
        dateC.insets = new Insets(0,10,0,0);
        dateC.gridx = 1;
        datePanel.add(dateChooser, dateC);

        bpc.gridx = 0;
        bpc.gridy = 0;
        bpc.weightx = 1;
        bpc.anchor = GridBagConstraints.WEST;
        bpc.insets = new Insets(0,15,5,15);
        bunkPanel.add(datePanel, bpc);
        bpc.anchor = GridBagConstraints.EAST;
        bpc.gridx = 4;
        bunkPanel.add(bunkEditPopup, bpc);
        bpc.gridx = 0;
        bpc.gridy = 1;
        bpc.weightx = 1;
        bpc.anchor = GridBagConstraints.NORTH;
        bpc.ipady = 20;
        bunkPanel.add(GuestLabel, bpc);
        bpc.gridx = 1;
        bunkPanel.add(bedSlotLabel, bpc);
        bpc.gridx = 2;
        bunkPanel.add(BunkLabel, bpc);
        bpc.gridx = 3;
        bunkPanel.add(ReservationLabel, bpc);
        bpc.gridx = 4;
        bunkPanel.add(PreviouslyAssignedLabel, bpc);
        bpc.ipady = 0;

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        db.subscribeRunnableToDBUpdate(BunkAssignmentPanel::buildGuestRows);
        buildGuestRows();

        scrollPane.add(bunkPanel);

        GridBagConstraints scrollC = new GridBagConstraints();
        scrollC.fill = GridBagConstraints.BOTH;
        scrollC.gridx = 0;
        scrollC.gridy = 0;
        scrollC.weightx = 1;
        scrollC.weighty = 1;
        scrollC.anchor = GridBagConstraints.NORTH;
        scrollPanel.add(scrollPane, scrollC);
        return scrollPanel;
    }

    private static ArrayList<String> reservedBunksA = new ArrayList<>();
    private static ArrayList<String> reservedBunksB = new ArrayList<>();
    private static void addGuestRow(Map<String, String> guest, int rowNum){

        Database db = DBConnectorV2Singleton.getInstance().database;
        JLabel guestNameLabel = new JLabel(guest.get("FirstName") + " " + guest.get("LastName"));
        JLabel reservedLabel = new JLabel("None");
        if(guest.get(BunkReservationsPanel.RESERVED_BUNK) != null && !guest.get(BunkReservationsPanel.RESERVED_BUNK).equals("NONE")){
            String reservedBunkId = guest.get(BunkReservationsPanel.RESERVED_BUNK);
            String reservedBunkStr = "Bunk " + db.bunkList.get(reservedBunkId).get("BunkNum") + " " + guest.get(BunkReservationsPanel.RESERVED_BUNK_SLOT);
            if(guest.get(BunkReservationsPanel.RESERVED_BUNK_SLOT).equals("A")){
                reservedBunksA.add("Bunk " + db.bunkList.get(reservedBunkId).get("BunkNum"));
            } else {
                reservedBunksB.add("Bunk " + db.bunkList.get(reservedBunkId).get("BunkNum"));
            }
            reservedLabel.setText(reservedBunkStr);
            reservedLabel.setOpaque(true);
            reservedLabel.setBackground(Color.YELLOW);
        }
        String guestId = "Guest_" + guest.get("GuestId");
        JLabel lastAssignedLabel = new JLabel(getLastAssignedBunk(guestId, false));
        JLabel[] labels = {guestNameLabel, reservedLabel, lastAssignedLabel};
        JComboBox<String> bedSlot = new JComboBox<>(new String[]{"A", "B"});

        for (JLabel label : labels) {
            label.setFont(new Font("Serif", Font.PLAIN, 18));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        if(!lastAssignedLabel.getText().equals("None")){
            lastAssignedLabel.setFont(new Font("Serif", Font.BOLD, 18));
        }

        setSelectedBedSlot(bedSlot, guest);
        JComboBox<String[]> bunkAssignmentCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks(bedSlot).toArray(new String[0][0])));
        bunkAssignmentCombo.setRenderer(new ComboBoxRenderer(bedSlot));
        bunkAssignmentCombo.addPopupMenuListener(new MyPopupMenuListener(bedSlot, bunkAssignmentCombo));
        bunkAssignmentCombo.setPreferredSize(new Dimension(225, 30));
        bunkAssignmentCombo.setSelectedItem(null);
        bunkAssignmentCombo.setSelectedIndex(-1);
        setSelectedBunk(bunkAssignmentCombo, guest);
        bunkAssignmentCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(bunkAssignmentCombo.getSelectedItem() != null){
                    if(Arrays.stream(bunkHeaders).toList().contains(((String[]) Objects.requireNonNull(bunkAssignmentCombo.getSelectedItem()))[0])){
                        String[] item = bunkAssignmentCombo.getItemAt(bunkAssignmentCombo.getSelectedIndex() + 1);
                        bunkAssignmentCombo.setSelectedItem(item);
                    }
                    if(bedSlot.getSelectedItem() != null && bunkAssignmentCombo.getSelectedItem() != null){
                        String[] selectedBunk = ((String[])bunkAssignmentCombo.getSelectedItem());
                        String selectedBedSlot = (String)bedSlot.getSelectedItem();
                        updateSelectedBunk(guest, selectedBunk[1], selectedBedSlot);
                    }
                }
            }
        });
        bedSlot.addPopupMenuListener( new MyPopupMenuListener(bedSlot, bunkAssignmentCombo));

        bunkAssignmentRows.add(new BunkAssignmentRow(bedSlot, bunkAssignmentCombo));

        bpc.gridy = rowNum;
        bpc.gridx = 0;
        bpc.weightx = 1;
        bpc.anchor = GridBagConstraints.WEST;
        bpc.insets = new Insets(5, 20, 5, 0);
        bunkPanel.add(guestNameLabel, bpc);
        bpc.insets = new Insets(5, 0, 5, 0);
        bpc.gridx = 1;
        bpc.anchor = GridBagConstraints.CENTER;
        bunkPanel.add(bedSlot, bpc);
        bpc.gridx = 2;
        bunkPanel.add(bunkAssignmentCombo, bpc);
        bpc.gridx = 3;
        bunkPanel.add(reservedLabel, bpc);
        bpc.gridx = 4;
        bunkPanel.add(lastAssignedLabel, bpc);
    }
    private static void setSelectedBedSlot(JComboBox<String> bedSlot, Map<String, String> guest) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d = DateHelper.truncateToDay(dateChooser.getDate());
        String formattedDate = dateFormat.format(d);

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

        for (String checkInID : db.database.attributes.get("Checkins").keySet()) {
            if(db.database.attributes.get("Checkins").get(checkInID).containsKey("Warning"))
                continue;

            String checkInDateFormatted = "";
            try {
                Date checkInDate = Date.from(Instant.parse(db.database.attributes.get("Checkins").get(checkInID).get("Date")));
                checkInDate = DateHelper.truncateToDay(checkInDate);
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }
            String checkInGuest = db.database.attributes.get("Checkins").get(checkInID).get("GuestId");
            if (checkInGuest.equals("Guest_" + guest.get("GuestId"))) {
                if (checkInDateFormatted.equals(formattedDate)) {
                    String bunkId = db.database.attributes.get("Checkins").get(checkInID).get("AssignedBunk");
                    String bunkSlot = db.database.attributes.get("Checkins").get(checkInID).get("AssignedBunkSlot");
                    if (bunkId != null && bunkSlot != null) {
                        if (db.database.bunkList.get(bunkId) != null) {
                            int bedIndex = (bunkSlot.equals("A")) ? 0 : 1;
                            bedSlot.setSelectedIndex(bedIndex);
                        }
                    }
                }
            }
        }
    }
    private static void setSelectedBunk(JComboBox<String[]> bunkCombo, Map<String, String> guest) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(dateChooser.getDate());

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

        for(String checkinID : db.database.attributes.get("Checkins").keySet()){
            if(db.database.attributes.get("Checkins").get(checkinID).containsKey("Warning"))
                continue;

            String checkInDateFormatted = "";
            try {
                Date checkInDate = Date.from(Instant.parse(db.database.attributes.get("Checkins").get(checkinID).get("Date")));
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }
            String checkInGuest = db.database.attributes.get("Checkins").get(checkinID).get("GuestId");
            if(checkInGuest.equals("Guest_" + guest.get("GuestId"))) {
                if (checkInDateFormatted.equals(formattedDate)) {
                    String bunkId = db.database.attributes.get("Checkins").get(checkinID).get("AssignedBunk");
                    String bunkSlot = db.database.attributes.get("Checkins").get(checkinID).get("AssignedBunkSlot");
                    if(bunkId != null && bunkSlot != null) {
                        if(db.database.bunkList.get(bunkId) != null) {
                            for(int i = 0; i < bunkCombo.getItemCount(); i++){
                                if(((String[])bunkCombo.getItemAt(i))[1].equals(bunkId)){
                                    bunkCombo.setSelectedIndex(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void updateSelectedBunk(Map<String, String> guest, String bunkId, String bedSlot){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(dateChooser.getDate());

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        for(String checkIn : db.database.attributes.get("Checkins").keySet()){
            String checkInDateFormatted = "";
            try {
                Date checkInDate = Date.from(Instant.parse(db.database.attributes.get("Checkins").get(checkIn).get("Date")));
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }
            String checkInGuest = db.database.attributes.get("Checkins").get(checkIn).get("GuestId");
            if(checkInGuest.equals("Guest_" + guest.get("GuestId"))){
                if(checkInDateFormatted.equals(formattedDate)){
                    db.database.attributes.get("Checkins").get(checkIn).put("AssignedBunk", bunkId);
                    db.database.attributes.get("Checkins").get(checkIn).put("AssignedBunkSlot", bedSlot);
                    // TODO add the bunk list  bunk num here
                    //db.database.bunkList.get(bunkId).put("BunkNum", );
                    db.asyncPush();
                    return;
                }
            }
        }
    }

    private static ArrayList<String> guestsAdded = new ArrayList<>();
    private static ArrayList<String> checkedInGuests = new ArrayList<>();
    private static int rowNum = 2;
    private static void buildGuestRows(){
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDateSelected = dateFormat.format(dateChooser.getDate());

        for(String checkInID : db.database.attributes.get("Checkins").keySet()) {
            String checkInDateFormatted = "";
            try {
                Date checkInDate = Date.from(Instant.parse(db.database.attributes.get("Checkins").get(checkInID).get("Date")));
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }
            String checkInGuest = db.database.attributes.get("Checkins").get(checkInID).get("GuestId");
            if(checkInDateFormatted.equals(formattedDateSelected)){
                checkedInGuests.add(checkInGuest);
            }
        }

        for (String guestId : checkedInGuests) {
            if(!guestsAdded.contains(guestId)) {
                guestsAdded.add(guestId);
                Map<String, String> guest = db.database.guests.get(guestId);
                addGuestRow(guest, rowNum);
                rowNum++;
            }
        }
    }

    private static ArrayList<BunkAssignmentRow> bunkAssignmentRows = new ArrayList<>();
    private static final ArrayList<String[]> mensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> womensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> observationBunkList = new ArrayList<>();
    private static final String[] bunkAreas = {"Mens", "Womens", "Observational"};
    private static final ArrayList<String[]>[] allBunkLists = new ArrayList[] {mensBunkList, womensBunkList, observationBunkList};
    private static final String[] bunkHeaders = new String[] {"Men's Bunks: ", "Women's Bunks: ", "Observation Area: "};

    public static ArrayList<String[]> getAvailableBunks(JComboBox<String> bedSlotCombo){
        Database db = DBConnectorV2Singleton.getInstance().database;
        boolean bunkSlotA = Objects.equals(bedSlotCombo.getSelectedItem(), "A");

        mensBunkList.clear();
        womensBunkList.clear();
        observationBunkList.clear();

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

        ArrayList<String> assignedBunksA = new ArrayList<>();
        ArrayList<String> assignedBunksB = new ArrayList<>();

        for(BunkAssignmentRow bunkRow : bunkAssignmentRows){
            if(bunkRow.getBunkAssignment().getSelectedItem() != null){
                if(bunkRow.getBunkSlot().getSelectedItem().equals("A")){
                    assignedBunksA.add(((String[])(bunkRow.getBunkAssignment().getSelectedItem()))[1]);
                } else {
                    assignedBunksB.add(((String[])(bunkRow.getBunkAssignment().getSelectedItem()))[1]);
                }
            }
        }

        ArrayList<String[]> availableBunks = new ArrayList<>();
        int bunkListType = 0;
        for(List<String[]> bunkList : allBunkLists){
            switch (bunkListType) {
                case 0 -> availableBunks.add(new String[]{bunkHeaders[0], ""});
                case 1 -> availableBunks.add(new String[]{bunkHeaders[1], ""});
                default -> availableBunks.add(new String[]{bunkHeaders[2], ""});
            }
            for(String[] bunk : bunkList){
                if(bunkSlotA){
                    if(!assignedBunksA.contains(bunk[1])){
                        availableBunks.add(new String[]{bunk[0], bunk[1]});
                    }
                } else {
                    if(!assignedBunksB.contains(bunk[1])){
                        availableBunks.add(new String[]{bunk[0], bunk[1]});
                    }
                }
            }
            bunkListType++;
        }
        return availableBunks;
    }
    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer<String[]> {
        private Color selectionBackgroundColor;
        private JComboBox<String> bedSlot;
        ComboBoxRenderer(JComboBox<String> bedSlot){
            setOpaque(true);
            this.bedSlot = bedSlot;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            selectionBackgroundColor = Color.white;
            if(value != null){
                if(Arrays.stream(bunkHeaders).toList().contains(value[0])) {
                    selectionBackgroundColor = Color.lightGray;
                }
                if(bedSlot.getSelectedItem().equals("A")) {
                    if(reservedBunksA.contains(value[0])){
                        selectionBackgroundColor = Color.YELLOW;
                    }
                } else {
                    if(reservedBunksB.contains(value[0])){
                        selectionBackgroundColor = Color.YELLOW;
                    }
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

    private static class MyPopupMenuListener implements PopupMenuListener {

        private final JComboBox<String[]> bunkAssignmentCombo;
        private final JComboBox<String> bedSlotCombo;
        public MyPopupMenuListener(JComboBox<String> bedSlotCombo, JComboBox<String[]> bunkAssignmentCombo){
            this.bedSlotCombo = bedSlotCombo;
            this.bunkAssignmentCombo = bunkAssignmentCombo;
        }
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            String[] assignedBunk = (String[]) bunkAssignmentCombo.getSelectedItem();
            bunkAssignmentCombo.removeAllItems();
            for(String[] bunk : getAvailableBunks(bedSlotCombo)){
                bunkAssignmentCombo.addItem(bunk);
            }
            bunkAssignmentCombo.setSelectedItem(assignedBunk);
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {}
    }

    private static class BunkAssignmentRow {
        private JComboBox<String> bunkSlot;
        private JComboBox<String[]> bunkAssignment;
        public JComboBox<String> getBunkSlot() {
            return bunkSlot;
        }
        public JComboBox<String[]> getBunkAssignment() {
            return bunkAssignment;
        }
        public BunkAssignmentRow(JComboBox<String> bunkSlot, JComboBox<String[]> bunkAssignment){
            this.bunkSlot = bunkSlot;
            this.bunkAssignment = bunkAssignment;
        }
    }

    public static String getLastAssignedBunk(String guestId, boolean onlyCheckCurrentDate){
        Database db = DBConnectorV2Singleton.getInstance().database;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDateSelected = dateFormat.format(dateChooser.getDate());

        Date nearestFoundDate = null;

        String assignedBunk = "None";

        for(String checkinId : db.attributes.get("Checkins").keySet()) { // TODO checkins as string seems strange here

            if(db.attributes.get("Checkins").get(checkinId).containsKey("Warning"))
                continue;

            String checkInDateFormatted = "";
            Date checkInDate = new Date();
            try {
                checkInDate = Date.from(Instant.parse(db.attributes.get("Checkins").get(checkinId).get("Date")));
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }

            boolean currentDateCheck = (onlyCheckCurrentDate && checkInDateFormatted.equals(formattedDateSelected));
            boolean skipCurrentDateCheck = (!onlyCheckCurrentDate && !checkInDateFormatted.equals(formattedDateSelected));
            if(currentDateCheck || skipCurrentDateCheck){
                if(nearestFoundDate == null || checkInDate.after(nearestFoundDate)){
                    if(db.attributes.get("Checkins").get(checkinId).get("GuestId").equals(guestId)){
                        String type = db.attributes.get("Checkins").get(checkinId).get("AssignedBunkSlot");
                        String bunkId = db.attributes.get("Checkins").get(checkinId).get("AssignedBunk");

                        if(bunkId == null || type == null) {
                            assignedBunk = "No bunk";
                        }
                        else {
                            String bunkNum = db.bunkList.get(bunkId).get("BunkNum");
                            assignedBunk = "Bunk " + bunkNum + " " + type;
                        }

                        nearestFoundDate = checkInDate;
                    }
                }
            }
        }
        return assignedBunk;
    }

    public static String getLastAssignedBunk(String guestId, boolean onlyCheckCurrentDate, Date currentDate){
        Database db = DBConnectorV2Singleton.getInstance().database;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDateSelected = dateFormat.format(currentDate);

        Date nearestFoundDate = null;

        String assignedBunk = "None";

        for(String checkinId : db.attributes.get("Checkins").keySet()) { // TODO checkins as string seems strange here

            if(db.attributes.get("Checkins").get(checkinId).containsKey("Warning"))
                continue;

            String checkInDateFormatted = "";
            Date checkInDate = new Date();
            try {
                checkInDate = Date.from(Instant.parse(db.attributes.get("Checkins").get(checkinId).get("Date")));
                checkInDateFormatted = dateFormat.format(checkInDate);
            } catch (Exception ignored) {
                System.out.println("Check In Date Parse Exception");
            }

            boolean currentDateCheck = (onlyCheckCurrentDate && checkInDateFormatted.equals(formattedDateSelected));
            boolean skipCurrentDateCheck = (!onlyCheckCurrentDate && !checkInDateFormatted.equals(formattedDateSelected));
            if(currentDateCheck || skipCurrentDateCheck){
                if(nearestFoundDate == null || checkInDate.after(nearestFoundDate)){
                    if(db.attributes.get("Checkins").get(checkinId).get("GuestId").equals(guestId)){
                        String type = db.attributes.get("Checkins").get(checkinId).get("AssignedBunkSlot");
                        String bunkId = db.attributes.get("Checkins").get(checkinId).get("AssignedBunk");

                        if(bunkId == null || type == null) {
                            assignedBunk = "No bunk";
                        }
                        else {
                            String bunkNum = db.bunkList.get(bunkId).get("BunkNum");
                            assignedBunk = "Bunk " + bunkNum + " " + type;
                        }

                        nearestFoundDate = checkInDate;
                    }
                }
            }
        }
        return assignedBunk;
    }
}
