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

        JFrame dayStoragePanel = IssueDetailsPopup.getNoTrespassDetailsPopup();
        //JPanel lockerMediumPanel = createMedLockerPanel();
       // JPanel lockerSmallPanel = createSmLockerPanel();

        tabbedPane.addTab("Day Storage", dayStoragePanel);
      //  tabbedPane.addTab("Medium Lockers", lockerMediumPanel);
       // tabbedPane.addTab("Small Lockers", lockerSmallPanel);
       // tabbedPane.addTab("Cube Storage", cubeStoragePanel);
        return infoPanel;
    }
}