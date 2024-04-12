package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BunkAssignmentPanel {

    public static JPanel getBunkAssignmentPanel(){
        JPanel scrollPanel = new JPanel(new GridBagLayout());
        ScrollPane scrollPane = new ScrollPane();
        JPanel bunkPanel = new JPanel(new GridBagLayout());

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

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 4;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(0,0,5,10);
        c.anchor = GridBagConstraints.EAST;
        bunkPanel.add(bunkEditPopup, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.ipady = 20;
        bunkPanel.add(GuestLabel, c);
        c.gridx = 1;
        bunkPanel.add(bedSlotLabel, c);
        c.gridx = 2;
        bunkPanel.add(BunkLabel, c);
        c.gridx = 3;
        bunkPanel.add(ReservationLabel, c);
        c.gridx = 4;
        bunkPanel.add(PreviouslyAssignedLabel, c);
        c.ipady = 0;

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        int rowNum = 2;
        for (Map.Entry<String, Map<String, String>> entry : db.database.guests.entrySet()) {
            Map<String, String> guest = entry.getValue();
            addGuestRow(bunkPanel, c, guest, rowNum);
            rowNum++;
        }

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

    private static void addGuestRow(JPanel panel, GridBagConstraints c, Map<String, String> guest, int rowNum){

        JLabel guestNameLabel = new JLabel(guest.get("FirstName") + " " + guest.get("LastName"));
        JLabel reservedLabel = new JLabel("None");
        JLabel lastAssignedLabel = new JLabel("Bunk 1 A");
        JLabel[] labels = {guestNameLabel, reservedLabel, lastAssignedLabel};
        JComboBox<String> bedSlot = new JComboBox<>(new String[]{"A", "B"});

        for (JLabel label : labels) {
            label.setFont(new Font("Serif", Font.PLAIN, 18));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        if(rowNum == 2){
            reservedLabel.setText("Bunk 2 B");
            reservedLabel.setOpaque(true);
            reservedLabel.setBackground(Color.YELLOW);
        }

        JComboBox<String[]> bunkAssignmentCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0][0])));
        bunkAssignmentCombo.setRenderer(new ComboBoxRenderer());
        bunkAssignmentCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String[] assignedBunk = (String[]) bunkAssignmentCombo.getSelectedItem();
                bunkAssignmentCombo.removeAllItems();
                for(String[] bunk : getAvailableBunks()){
                    bunkAssignmentCombo.addItem(bunk);
                }
                bunkAssignmentCombo.setSelectedItem(assignedBunk);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        bunkAssignmentCombo.setPreferredSize(new Dimension(225, 30));
        bunkAssignmentCombo.setSelectedItem(null);
        bunkAssignmentCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(bunkAssignmentCombo.getSelectedItem() != null){
                    if(Arrays.stream(bunkHeaders).toList().contains(((String[]) Objects.requireNonNull(bunkAssignmentCombo.getSelectedItem()))[0]) && e.getStateChange() == ItemEvent.SELECTED){
                        String[] item = bunkAssignmentCombo.getItemAt(bunkAssignmentCombo.getSelectedIndex() + 1);
                        bunkAssignmentCombo.setSelectedItem(item);
                    }
                }
            }
        });
        bunkAssignmentCombo.setSelectedIndex(-1);
        bunkComboBoxes.add(bunkAssignmentCombo);

        c.gridy = rowNum;
        c.gridx = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 20, 5, 0);
        panel.add(guestNameLabel, c);
        c.insets = new Insets(5, 0, 5, 0);
        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(bedSlot, c);
        c.gridx = 2;
        panel.add(bunkAssignmentCombo, c);
        c.gridx = 3;
        panel.add(reservedLabel, c);
        c.gridx = 4;
        panel.add(lastAssignedLabel, c);
    }

    private static final ArrayList<JComboBox<String[]>> bunkComboBoxes = new ArrayList<>();

    private static final ArrayList<String[]> mensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> womensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> observationBunkList = new ArrayList<>();
    private static final String[] bunkAreas = {"Mens", "Womens", "Observational"};
    private static final ArrayList<String[]>[] allBunkLists = new ArrayList[] {mensBunkList, womensBunkList, observationBunkList};
    private static final String[] bunkHeaders = new String[] {"Men's Bunks: ", "Women's Bunks: ", "Observation Area: "};

    private static ArrayList<String[]> getAvailableBunks(JComboBox<String> bedSlotCombo){
        Database db = DBConnectorV2Singleton.getInstance().database;
        Boolean topBunk = Objects.equals(bedSlotCombo.getSelectedItem(), "A");

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

        ArrayList<String> assignedBunks = new ArrayList<>();
        for(JComboBox<String[]> comboBox : bunkComboBoxes){
            if(comboBox.getSelectedItem() != null){
                assignedBunks.add(((String[])(comboBox.getSelectedItem()))[1]);
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
                if(!assignedBunks.contains(bunk[1])){
                    availableBunks.add(new String[]{bunk[0], bunk[1]});
                }
            }
            bunkListType++;
        }
        return availableBunks;
    }
    private static class ComboBoxRenderer extends JLabel implements ListCellRenderer<String[]> {

        private Color selectionBackgroundColor;
        private ComboBoxRenderer(){
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            selectionBackgroundColor = Color.white;
            if(value != null){
                if("Bunk 2 B".equals(value[0])){
                    selectionBackgroundColor = Color.YELLOW;
                } else if(Arrays.stream(bunkHeaders).toList().contains(value[0])) {
                    selectionBackgroundColor = Color.lightGray;
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
