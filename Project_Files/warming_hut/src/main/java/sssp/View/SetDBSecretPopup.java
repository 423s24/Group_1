package sssp.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import java.util.prefs.*;

public class SetDBSecretPopup {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 200;
    private static final int MENU_WIDTH = 200;
    private static final int SCROLL_HEIGHT = 100;

    public static void displaySetDBSecretPopup(){
        JFrame secretPopupFrame = new JFrame();
        secretPopupFrame.setTitle("Set Database Key");
        secretPopupFrame.setLayout(new GridBagLayout());
        secretPopupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        secretPopupFrame.setSize(new Dimension(WIDTH, HEIGHT));
        secretPopupFrame.setVisible(true);
        secretPopupFrame.setLocationRelativeTo(null);
        secretPopupFrame.setTitle("Set Database Key");

        GridBagConstraints frameC = new GridBagConstraints();
        frameC.fill = GridBagConstraints.HORIZONTAL;
        frameC.weightx = 1.0;
        frameC.weighty = 1.0;
        frameC.gridx = 0;
        frameC.gridy = 0;
        frameC.anchor = GridBagConstraints.NORTH;
        secretPopupFrame.add(getDBSecretPopup(), frameC);
    }

    static ActionListener setSecret = e -> {
        Preferences prefs = Preferences.userNodeForPackage(sssp.mainpage.class);
        JTextField field = (JTextField) e.getSource();
        String secret = field.getText();
        prefs.put("db_secret", secret);
    };

    public static JPanel getDBSecretPopup() {
        JPanel popup = new JPanel();
        JLabel secretExplanation = new JLabel("Use the box below to set the key for the database. Just input database key and press 'Enter'");
        popup.add(secretExplanation);

        JTextField secretField = new JTextField();
        secretField.setPreferredSize(new Dimension(MENU_WIDTH, SCROLL_HEIGHT));
        secretField.addActionListener(setSecret);

        popup.add(secretField);

        return popup;


    }
}
