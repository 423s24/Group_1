package sssp.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IssueDetailsPopup {

    public static final JLabel noTrespassLabel = new JLabel("No Trespass Details");
    public static JFrame getNoTrespassDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new FlowLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(300, 300));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setPreferredSize(new Dimension(200, 200));
        warningDetailsPanel.add(noTrespassLabel);

        popupFrame.add(warningDetailsPanel);


        return popupFrame;
    }

    public static final JLabel suspensionsLabel = new JLabel("Suspensions Details");
    public static JFrame getSuspensionsDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new FlowLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(300, 300));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setPreferredSize(new Dimension(200, 200));
        warningDetailsPanel.add(suspensionsLabel);

        popupFrame.add(warningDetailsPanel);


        return popupFrame;
    }

    public static final JLabel warningsLabel = new JLabel("Warnings Details");
    public static JFrame getWarningDetailsPopup(){

        JFrame popupFrame = new JFrame();
        popupFrame.setLayout(new FlowLayout());
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setSize(new Dimension(300, 300));
        popupFrame.setVisible(true);
        popupFrame.setLocationRelativeTo(null);

        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());
        warningDetailsPanel.setPreferredSize(new Dimension(200, 200));
        warningDetailsPanel.add(warningsLabel);

        popupFrame.add(warningDetailsPanel);


        return popupFrame;
    }
}
