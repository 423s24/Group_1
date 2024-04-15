package sssp.View;

import javax.swing.*;
import java.awt.*;

public class SetDBSecretPopup {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 200;
    private static final int MENU_WIDTH = 200;
    private static final int SCROLL_HEIGHT = 100;

    public static void displaySetDBSecretPopup(){
        JFrame secretPopupFrame = new JFrame();
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

    public static JPanel getDBSecretPopup() {
        JPanel popup = new JPanel();
        // TODO add popup content
        JLabel secretExplanation1 = new JLabel("Use the box below to set the key for the database.");
        JLabel secretExplanation2 = new JLabel("Just input database key and press 'Enter'");
        //secretExplanation.setHorizontalAlignment(SwingConstants.LEFT);

        popup.add(secretExplanation1);
        popup.add(secretExplanation2);
        return popup;
    }
}