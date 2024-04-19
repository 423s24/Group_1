package sssp.View;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import sssp.Helper.Database;
import sssp.Helper.UUIDGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class BunkEditorPopup {

    private static final int WIDTH = 750;
    private static final int HEIGHT = 750;
    private static final int MENU_WIDTH = 200;
    private static final int SCROLL_HEIGHT = 100;

    public static void showBunkEditorPopup(){
        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new GridBagLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);
        popupFrame.setTitle("Bunk Editor");

        GridBagConstraints frameC = new GridBagConstraints();
        frameC.fill = GridBagConstraints.HORIZONTAL;
        frameC.weightx = 1.0;
        frameC.weighty = 1.0;
        frameC.gridx = 0;
        frameC.gridy = 0;
        frameC.anchor = GridBagConstraints.NORTH;
        popupFrame.add(getBunkEditorPanel(), frameC);
    }

    private static final JList<String[]> mensBunkJList = new JList<>();
    private static final JList<String[]> womensBunkJList = new JList<>();
    private static final JList<String[]> observationBunkJList = new JList<>();
    private static final ArrayList<String[]> mensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> womensBunkList = new ArrayList<>();
    private static final ArrayList<String[]> observationBunkList = new ArrayList<>();
    private static void updateBunkList(){
        Database db = DBConnectorV2Singleton.getInstance().database;
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

        mensBunkList.sort(Comparator.comparing(l -> l[0]));
        womensBunkList.sort(Comparator.comparing(l -> l[0]));
        observationBunkList.sort(Comparator.comparing(l -> l[0]));
        mensBunkJList.setListData(mensBunkList.toArray(new String[0][0]));
        womensBunkJList.setListData(womensBunkList.toArray(new String[0][0]));
        observationBunkJList.setListData(observationBunkList.toArray(new String[0][0]));
    }
    private static final JButton deleteBtn = new JButton("Delete Bunk");
    public static JPanel getBunkEditorPanel(){
        JPanel popup = new JPanel(new GridBagLayout());

        JLabel mensAreaLabel = new JLabel("Men's Bunks");
        JLabel womensAreaLabel = new JLabel("Women's Bunks");
        JLabel observationAreaLabel = new JLabel("Observational Area");

        JLabel[] labels = {mensAreaLabel, womensAreaLabel, observationAreaLabel};
        for (JLabel label : labels) {
            //label.setFont(new Font("Serif", Font.PLAIN, 22));
        }

        ScrollPane mensScrollPane = new ScrollPane();
        ScrollPane womensScrollPane = new ScrollPane();
        ScrollPane observationScrollPane = new ScrollPane();

        updateBunkList();
        mensScrollPane.add(mensBunkJList);
        mensScrollPane.setPreferredSize(new Dimension(MENU_WIDTH, SCROLL_HEIGHT));

        womensScrollPane.add(womensBunkJList);
        womensScrollPane.setPreferredSize(new Dimension(MENU_WIDTH, SCROLL_HEIGHT));

        observationScrollPane.add(observationBunkJList);
        observationScrollPane.setPreferredSize(new Dimension(MENU_WIDTH, SCROLL_HEIGHT));

        JLabel title = new JLabel("Bunk Editor");
        //title.setFont(new Font("Serif", Font.PLAIN, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton newBtn = new JButton("New Bunk");
        //newBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        newBtn.setPreferredSize(new Dimension(MENU_WIDTH, 25));
        newBtn.addActionListener(e -> {
            createNewBunk();
        });

        //deleteBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        deleteBtn.setPreferredSize(new Dimension(MENU_WIDTH, 25));
        deleteBtn.setBackground(Color.red);
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> {
            deleteBunk();
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        popup.add(title, c);
        c.gridwidth = 1;
        c.gridy = 1;
        c.insets = new Insets(10,5,0, 5);
        popup.add(mensAreaLabel, c);
        c.gridy = 2;
        popup.add(mensScrollPane, c);
        c.gridy = 3;
        popup.add(womensAreaLabel, c);
        c.gridy = 4;
        popup.add(womensScrollPane, c);
        c.gridy = 5;
        popup.add(observationAreaLabel, c);
        c.gridy = 6;
        popup.add(observationScrollPane, c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 5;
        popup.add(getBunkEditMenuPanel(), c);
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 7;
        popup.add(newBtn, c);
        c.gridx = 1;
        popup.add(deleteBtn, c);
        return popup;
    }

    private static final JTextField bunkNumField = new JTextField();
    private static final String[] bunkAreas = {"Mens", "Womens", "Observational"};
    private static final JComboBox<String> bunkAreaField = new JComboBox<>(bunkAreas);

    private static void deleteBunk(){
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        Map<String, String> bunk = db.database.bunkList.get(selectedBunkList.getSelectedValue()[1]);
        int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Bunk " + bunk.get("BunkNum") + " " + bunk.get("BunkType") + "?", "Delete Bunk?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if(res == 0) {
            db.database.bunkList.put(selectedBunkList.getSelectedValue()[1], null);
            db.push();
            updateBunkList();
        }
    }
    private static void saveValues(){
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        Map<String, String> bunk = db.database.bunkList.get(selectedBunkList.getSelectedValue()[1]);
        bunk.put("BunkArea", Objects.requireNonNull(bunkAreaField.getSelectedItem()).toString());
        bunk.put("BunkNum", bunkNumField.getText());
        db.database.bunkList.put(selectedBunkList.getSelectedValue()[1], bunk);
        db.push();
        updateBunkList();
    }
    private static void setValues(String bunkNum, String bunkArea){
        bunkNumField.setText(bunkNum);
        int bunkAreaIndex = (bunkArea.equals("Mens")) ? 0 : (bunkArea.equals("Womens")) ? 1 : 2;
        bunkAreaField.setSelectedIndex(bunkAreaIndex);
    }
    private static void onBunkSelected(JList<String[]> selectedList){
        if(mensBunkJList != selectedList){
            mensBunkJList.clearSelection();
        }
        if(womensBunkJList != selectedList){
            womensBunkJList.clearSelection();
        }
        if(observationBunkJList != selectedList){
            observationBunkJList.clearSelection();
        }
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        Map<String, String> selectedBunk = db.database.bunkList.get(selectedList.getSelectedValue()[1]);
        setValues(selectedBunk.get("BunkNum"),selectedBunk.get("BunkArea"));
        selectedBunkList = selectedList;
    }

    private static JList<String[]> selectedBunkList;
    private static JPanel getBunkEditMenuPanel(){
        mensBunkJList.setCellRenderer(new BunkListRenderer());
        womensBunkJList.setCellRenderer(new BunkListRenderer());
        observationBunkJList.setCellRenderer(new BunkListRenderer());

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel bunkNumLabel = new JLabel("Bunk Number: ");
        JLabel bunkAreaLabel = new JLabel("Bunk Area");

        JButton saveChangesBtn = new JButton("Save Changes");
        //saveChangesBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        saveChangesBtn.setPreferredSize(new Dimension(MENU_WIDTH, 25));
        saveChangesBtn.setEnabled(false);
        saveChangesBtn.addActionListener(e -> {
            saveValues();
        });

        JLabel[] labels = {bunkNumLabel, bunkAreaLabel};
        for (JLabel label : labels) {
            //label.setFont(new Font("Serif", Font.PLAIN, 22));
        }

        bunkNumField.setPreferredSize(new Dimension(MENU_WIDTH, 25));
        bunkNumField.setMinimumSize(new Dimension(MENU_WIDTH, 25));

        mensBunkJList.addListSelectionListener(e -> {
                if(mensBunkJList.getSelectedIndex() >= 0){
                    onBunkSelected(mensBunkJList);
                    saveChangesBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                }
        });

        womensBunkJList.addListSelectionListener(e -> {
            if(womensBunkJList.getSelectedIndex() >= 0){
                onBunkSelected(womensBunkJList);
                saveChangesBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }
        });

        observationBunkJList.addListSelectionListener(e -> {
            if(observationBunkJList.getSelectedIndex() >= 0){
                onBunkSelected(observationBunkJList);
                saveChangesBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        panel.add(bunkNumLabel, c);
        c.gridy = 1;
        panel.add(bunkNumField, c);
        c.insets = new Insets(10,0,0,0);
        c.gridy = 2;
        panel.add(bunkAreaLabel, c);
        c.gridy = 3;
        panel.add(bunkAreaField, c);
        c.gridy = 4;
        panel.add(saveChangesBtn, c);

        return panel;
    }

    private static void createNewBunk(){
        DBConnectorV2 db = DBConnectorV2Singleton.getInstance();
        Map<String, String> newBunk = new HashMap<>();
        newBunk.put("BunkArea", "Mens");
        newBunk.put("BunkNum", "NEW");
        String bunkName = "Bunk_" + UUIDGenerator.getNewUUID();
        db.database.bunkList.put(bunkName, newBunk);
        db.push();
        updateBunkList();
    }

    private static class BunkListRenderer extends JLabel implements ListCellRenderer<String[]> {
        private Color selectionBackgroundColor;
        private BunkListRenderer(){
            setOpaque(true);
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value[0]);
            if(isSelected){
                selectionBackgroundColor = Color.lightGray;
            } else {
                selectionBackgroundColor = Color.white;
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
