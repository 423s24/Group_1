package sssp.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuMockup {
    private JButton AddUserButton;
    private JButton RemoveUserButton;
    private JButton SampleButton1;
    private JButton SampleButton2;
    private JTable GuestList;
    private JPanel TopBar;
    private JPanel ListContainer;
    private JPanel ButtonBar;
    private JPanel MainPanel;
    private JButton HorzBut1;
    private JButton HorzBut2;
    private JButton HorzBut3;
    private JPanel HorizontalButtonBar;

    private DefaultTableModel tableModel;

    public MainMenuMockup() {
        // Initialize JFrame
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create MockForm
        MockForm mockForm = new MockForm(this);

        // Create table
        tableModel = new DefaultTableModel(
                new Object[][]{{"First name", "Last name", "Date"}}, // Initial row
                new String[]{"First name", "Last name", "Date"}
        );

        // Stuff for MockForm usage
        AddUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new JFrame and add the MockForm panel to it
                JFrame mockFrame = new JFrame("Add User");
                mockFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                mockFrame.setSize(400, 300);
                mockFrame.add(mockForm.getMockPanel());

                centerFrame(mockFrame);

                mockFrame.setVisible(true);
            }
        });

        // lala
        SampleButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] newEntry = {"John", "Doe", "lala"};
                tableModel.addRow(newEntry);
            }
        });

        GuestList.setModel(tableModel);

        frame.add(MainPanel);

        centerFrame(frame);

        frame.setVisible(true);
    }

    private void centerFrame(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }

    public void addRowToTable(String firstName, String lastName, String date) {
        Object[] newEntry = {firstName, lastName, date};
        tableModel.addRow(newEntry);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenuMockup();
            }
        });
    }
}