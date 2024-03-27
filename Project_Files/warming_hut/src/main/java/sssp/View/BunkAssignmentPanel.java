package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BunkAssignmentPanel {

    public static JPanel getBunkAssignmentPanel(){
        JPanel scrollPanel = new JPanel(new GridBagLayout());
        ScrollPane scrollPane = new ScrollPane();
        JPanel bunkPanel = new JPanel(new GridBagLayout());

        JLabel GuestLabel = new JLabel("Guest");
        JLabel BunkLabel = new JLabel("Bunk Assignment");
        JLabel ReservationLabel = new JLabel("Reservation");
        JLabel PreviouslyAssignedLabel = new JLabel("Previously Assigned");
        JLabel[] labels = {GuestLabel, BunkLabel, ReservationLabel, PreviouslyAssignedLabel};

        for (JLabel label : labels) {
            label.setFont(new Font("Serif", Font.BOLD, 24));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.ipady = 20;
        bunkPanel.add(GuestLabel, c);
        c.gridx = 1;
        bunkPanel.add(BunkLabel, c);
        c.gridx = 2;
        bunkPanel.add(ReservationLabel, c);
        c.gridx = 3;
        bunkPanel.add(PreviouslyAssignedLabel, c);
        c.ipady = 0;

        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        int rowNum = 1;
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
        JLabel lastAssignedLabel = new JLabel("Bunk 2");
        JLabel[] labels = {guestNameLabel, reservedLabel, lastAssignedLabel};

        for (JLabel label : labels) {
            label.setFont(new Font("Serif", Font.PLAIN, 18));
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        JComboBox<String> bunkAssignmentCombo = new JComboBox<>(new DefaultComboBoxModel<>(getAvailableBunks().toArray(new String[0])));
        bunkAssignmentCombo.setRenderer(new ComboBoxRenderer());
        bunkAssignmentCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String assignedBunk = (String) bunkAssignmentCombo.getSelectedItem();
                bunkAssignmentCombo.removeAllItems();
                for(String bunk : getAvailableBunks()){
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
                if(Arrays.stream(bunkHeaders).toList().contains((String)bunkAssignmentCombo.getSelectedItem()) && e.getStateChange() == ItemEvent.SELECTED){
                    String item = bunkAssignmentCombo.getItemAt(bunkAssignmentCombo.getSelectedIndex() + 1);
                    bunkAssignmentCombo.setSelectedItem(item);
                }
            }
        });
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
        panel.add(bunkAssignmentCombo, c);
        c.gridx = 2;
        panel.add(reservedLabel, c);
        c.gridx = 3;
        panel.add(lastAssignedLabel, c);
    }


    private static final ArrayList<JComboBox<String>> bunkComboBoxes = new ArrayList<>();

    private static final ArrayList<String> mensBunks = new ArrayList<>(List.of(new String[]{"Bunk 1 A", "Bunk 1 B", "Bunk 2 A", "Bunk 2 B", "Bunk 3 A", "Bunk 3 B"}));
    private static final ArrayList<String> womansBunks = new ArrayList<>(List.of(new String[]{"Bunk 4 A", "Bunk 4 B", "Bunk 5 A", "Bunk 5 B"}));
    private static final ArrayList<String> observationArea = new ArrayList<>(List.of(new String[]{"Bunk 6 A", "Bunk 6 B"}));
    private static final ArrayList<String>[] allBunkLists = new ArrayList[] {mensBunks, womansBunks, observationArea};
    private static final String[] bunkHeaders = new String[] {"Men's Bunks: ", "Woman's Bunks: ", "Observation Area: "};

    private static ArrayList<String> getAvailableBunks(){
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

        ArrayList<String> assignedBunks = new ArrayList<>();
        for(JComboBox<String> comboBox : bunkComboBoxes){
            assignedBunks.add((String)comboBox.getSelectedItem());
        }

        ArrayList<String> availableBunks = new ArrayList<>();
        int bunkListType = 0;
        for(List<String> bunkList : allBunkLists){
            switch (bunkListType) {
                case 0 -> availableBunks.add(bunkHeaders[0]);
                case 1 -> availableBunks.add(bunkHeaders[1]);
                default -> availableBunks.add(bunkHeaders[2]);
            }
            for(String bunk : bunkList){
                if(!assignedBunks.contains(bunk)){
                    availableBunks.add(bunk);
                }
            }
            bunkListType++;
        }
        return availableBunks;
    }
    private static class ComboBoxRenderer extends JLabel implements ListCellRenderer<String> {

        private Color selectionBackgroundColor;
        private ComboBoxRenderer(){
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            selectionBackgroundColor = Color.white;
            if("Bunk 3".equals(value)){
                selectionBackgroundColor = Color.red;
            } else {
                selectionBackgroundColor = Color.white;
            }
            setText(value);

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
