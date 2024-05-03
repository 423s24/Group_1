package sssp.View.GuestDetails.Panels;

import javax.swing.*;

import java.awt.*;
import java.util.List;
import java.util.Map;



public class DisciplinaryInfoPanel extends JPanel {
    NoTrespassPanel noTrespassDetailsPanel;
    SuspensionPanel suspensionDetailsPanel;
    WarningsPanel warningDetailsPanel;

    public DisciplinaryInfoPanel()
    {
        this.setLayout(new BorderLayout());


        JTabbedPane tabbedPane = new JTabbedPane();

        noTrespassDetailsPanel = new NoTrespassPanel();
        suspensionDetailsPanel = new SuspensionPanel();
        warningDetailsPanel = new WarningsPanel();

        tabbedPane.addTab("No Trespass", new JScrollPane(noTrespassDetailsPanel));
        tabbedPane.addTab("Suspensions", new JScrollPane(suspensionDetailsPanel));
        tabbedPane.addTab("Warnings", new JScrollPane(warningDetailsPanel));

        this.add(tabbedPane);
    }

    private String activeGuestID;

    List<Map<String,String>> warningData;

    public boolean setActiveGuestID(String guestID) {
        this.activeGuestID = guestID;

        onActiveGuestChanged();

        return true;
    }

    private void onActiveGuestChanged() {
        noTrespassDetailsPanel.setActiveGuestID(activeGuestID);
        suspensionDetailsPanel.setActiveGuestID(activeGuestID);
        warningDetailsPanel.setActiveGuestID(activeGuestID);
    }
}