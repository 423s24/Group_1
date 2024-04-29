package sssp.View.listeners;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class DeselectOtherTableListener implements ListSelectionListener {
    private JTable other;
    private boolean isProcessing = false;

    public DeselectOtherTableListener(JTable other) {
        this.other = other;
    }

    private int lastSelectedIndex = -1;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting())
            return;

        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        int selectedIndex = lsm.getMinSelectionIndex();

        if (selectedIndex == -1) {
            // deselection
        } else if (selectedIndex != lastSelectedIndex) {
            // fresh selection
            other.clearSelection();
        }

        lastSelectedIndex = selectedIndex;
    }
}
