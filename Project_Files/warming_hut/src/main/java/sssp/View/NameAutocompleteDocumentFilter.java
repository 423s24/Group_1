package sssp.View;

import javax.swing.*;
import javax.swing.text.*;

import java.util.List;
import java.util.stream.Collectors;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;


public class NameAutocompleteDocumentFilter extends DocumentFilter {
    private DBConnectorV2 db;
    private JTextField owner;

    private int selectedMenuItemIndex = -1;

    // Pops up with name suggestions
    JPopupMenu popupMenu = new JPopupMenu();

    public NameAutocompleteDocumentFilter(JTextField owner) {
        this.db = DBConnectorV2Singleton.getInstance();
        this.owner = owner;

        // Add key listener to the text field to handle up, down, and enter keys without losing focus on the field
        owner.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectNextMenuItem();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    selectPreviousMenuItem();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    replaceTextWithSelectedMenuItem();
                }
            }
        });

        // Makes it so the popup menu doesn't force the text field to be deselected
        popupMenu.setFocusable(false);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        super.insertString(fb, offset, string, attr);
        updateCompletion(fb);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
        updateCompletion(fb);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        super.replace(fb, offset, length, text, attrs);
        updateCompletion(fb);
    }

    private void updateCompletion(FilterBypass fb) throws BadLocationException {
        String text = fb.getDocument().getText(0, fb.getDocument().getLength());

        // If the text is empty, hide the popup menu
        if(text.equals(""))
        {
            popupMenu.setVisible(false);
            return;
        }

        // Get all guest names from the database
        List<Map<String, String>> guests = db.database.guests.values().stream()
            .collect(Collectors.toList());

        // Clear the popup menu
        resetPopupMenu();
        selectedMenuItemIndex = -1;

        // Add all names that start with the current text to the popup menu
        for (Map<String, String> guest : guests) {
            String firstName = guest.get("FirstName");
            String lastName = guest.get("LastName");
            String fullName = firstName + " " + lastName;

            if (autocompleteMatch(text, firstName, lastName)) {
                JMenuItem menuItem = new JMenuItem(fullName);
                menuItem.addActionListener(e -> {
                    try {
                        fb.replace(0, fb.getDocument().getLength(), menuItem.getText(), null);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                });
                popupMenu.add(menuItem);
            }
        }

        // Show the popup menu
        if (popupMenu.getComponentCount() > 0) {
            popupMenu.show(owner, 0, owner.getHeight());
            popupMenu.setVisible(true);
        }
        if(popupMenu.getComponentCount() == 0)
        {
            popupMenu.setVisible(false);
        }
    }

    private boolean autocompleteMatch(String text, String firstName, String lastName) {
        boolean matchesFirst = firstName.toLowerCase().startsWith(text.toLowerCase());
        boolean matchesLast = lastName.toLowerCase().startsWith(text.toLowerCase());
        boolean matchesFull = (firstName + " " + lastName).toLowerCase().startsWith(text.toLowerCase());
        return matchesFirst || matchesLast || matchesFull;
    }

    private void selectNextMenuItem() {
        if (popupMenu.getComponentCount() > 0) {
            selectedMenuItemIndex = Math.min(selectedMenuItemIndex + 1, popupMenu.getComponentCount() - 1);
            MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{popupMenu, (JMenuItem)popupMenu.getComponent(selectedMenuItemIndex)});
            
            ((JMenuItem) popupMenu.getComponent(selectedMenuItemIndex)).setSelected(true);
        }
    }

    private void selectPreviousMenuItem() {
        if (popupMenu.getComponentCount() > 0) {
            selectedMenuItemIndex = Math.max(selectedMenuItemIndex - 1, 0);
            ((JMenuItem) popupMenu.getComponent(selectedMenuItemIndex)).setSelected(true);
            MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{popupMenu, (JMenuItem)popupMenu.getComponent(selectedMenuItemIndex)});
            
            popupMenu.getSelectionModel().setSelectedIndex(selectedMenuItemIndex);
        }
    }
    
    private void replaceTextWithSelectedMenuItem() {

        if (selectedMenuItemIndex >= 0 && selectedMenuItemIndex < popupMenu.getComponentCount()) {
            JMenuItem selectedMenuItem = (JMenuItem) popupMenu.getComponent(selectedMenuItemIndex);
            owner.setText(selectedMenuItem.getText());

            popupMenu.setVisible(false);
        }
    }

    public void resetPopupMenu()
    {
        popupMenu.setVisible(false);
        popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);
        popupMenu.setVisible(true);
    }
}