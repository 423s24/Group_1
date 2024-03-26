package sssp.View;

//import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Date;

public class DisciplinaryInfoPanel {
    public static JPanel getDisciplinaryInfoPanel(){
        JPanel infoPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel noTrespassDetailsPanel = new JPanel(new GridBagLayout());
        JPanel suspensionDetailsPanel = new JPanel(new GridBagLayout());
        JPanel warningDetailsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        noTrespassDetailsPanel.add(IssueDetailsPopup.getNoTrespassDetailsPanel(), c);
        suspensionDetailsPanel.add(IssueDetailsPopup.getSuspensionsDetailsPanel(), c);
        warningDetailsPanel.add(IssueDetailsPopup.getWarningDetailsPanel(), c);

        tabbedPane.addTab("No Trespass", noTrespassDetailsPanel);
        tabbedPane.addTab("Suspensions", suspensionDetailsPanel);
        tabbedPane.addTab("Warnings", warningDetailsPanel);

        infoPanel.add(tabbedPane);
        return infoPanel;
    }
}