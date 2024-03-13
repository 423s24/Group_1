package sssp.View;

import javax.swing.*;

public class SidebarTesting {
    private JPanel panel1;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SidebarTesting();
            }
        });

    }

    public SidebarTesting() {
        JFrame frame = new JFrame("SidebarTesting");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 400);
    }

    private void createUIComponents() {

    }
}
