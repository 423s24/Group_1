package sssp.Control;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.prefs.*;
import javax.swing.*;

public class SecretManager {
    static Preferences prefs = Preferences.userNodeForPackage(sssp.mainpage.class);

    public static String getDBSecret() {
        if(prefs.get("db_secret", null) != null)
            return prefs.get("db_secret", null);
        else
            return showSecretInputDialog("Dataview Setup", "Enter your database secret.");
    }

    public static void invalidateSecret() {
        prefs.remove("db_secret");
    }

    public static void requireNewDBSecretEntry()
    {
        showSecretInputDialog("Dataview Setup", "Invalid database secret.");
    }

    public static void voluntaryDBSecretUpdate()
    {
        showSecretInputDialog("Dataview Setup", "Enter your new database secret.");
    }

    private static String showSecretInputDialog(String title, String message)
    {
        JLabel messageLabel = new JLabel(message);

        JLabel linkLabel = new JLabel("<html><a href=''>Help</a></html>");
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://docs.google.com/document/d/1NFg2_1X0lsAy_qRDhNyk6lcxtxBhPvKlntXFOtWWhFw/edit"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        JEditorPane editorPane = new JEditorPane();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(messageLabel, gbc);
        panel.add(linkLabel, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(new JScrollPane(editorPane), gbc);


        int option = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (option == JOptionPane.OK_OPTION) {
            String secret = editorPane.getText();
            prefs.put("db_secret", secret);
            return secret;
        }
        if ((option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) && prefs.get("db_secret", null) == null) {
            System.exit(0);
        }
        return prefs.get("db_secret", null);
    }
}
