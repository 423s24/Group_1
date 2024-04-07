package sssp.View.components;

import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionListener;
import java.util.Map;

public class DBSyncedTextArea extends JTextArea {
    private String fieldKey;
    private String objKey;
    private String tableKey;
    private Map<String, Map<String, String>> table;
    private Map<String, Map<String, Map<String, String>>> superTable;    

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    // delay (milliseconds)
    int delay = 2000;

    // when the timer runs out, the DB should be updated
    Timer timer = new Timer(delay, new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            onTextChanged();
        }
    });

    // when the user makes a change, the timer resets
    DocumentListener timerListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
            resetTimer();
        }
        public void removeUpdate(DocumentEvent e) {
            resetTimer();
        }
        public void insertUpdate(DocumentEvent e) {
            resetTimer();
        }

        private void resetTimer() {
            if (timer.isRunning()) {
                timer.restart();
            } else {
                timer.start();
            }
        }
    };

    private DBSyncedTextArea(Map<String, Map<String, Map<String, String>>> superTable, Map<String, Map<String, String>> table, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.table = table;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    private DBSyncedTextArea(Integer rows, Integer columns, Map<String, Map<String, Map<String, String>>> superTable, Map<String, Map<String, String>> table, String tableKey, String objKey, String fieldKey)
    {
        super(rows, columns);

        this.superTable = superTable;
        this.table = table;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    public DBSyncedTextArea(Integer rows, Integer columns, Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        this(rows, columns, null, table, null, objKey, fieldKey);
    }

    public DBSyncedTextArea(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        this(null, table, null, objKey, fieldKey);
    }

    public DBSyncedTextArea(Integer rows, Integer columns, Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        this(rows, columns, superTable, null, tableKey, objKey, fieldKey);
    }

    public DBSyncedTextArea(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        this(superTable, null, tableKey, objKey, fieldKey);
    }


    private void init() {
        timer.setRepeats(false); // Make sure the timer only runs once

        // Add listeners for text area
        super.getDocument().addDocumentListener(timerListener);

        // Triggered when the DB updates
        db.subscribeRunnableToDBUpdate(this::onDBUpdate);
    }

    public void setObjKey(String objKey) {
        this.objKey = objKey;
        this.pullState();
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
        this.pullState();
    }

    private void onTextChanged()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        targetObj.put(fieldKey, this.getText());

        db.push();
    }

    private void onDBUpdate()
    {
        this.pullState();
    }

    private void pullState()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        super.getDocument().removeDocumentListener(timerListener);

        Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        this.setText(targetObj.get(fieldKey));

        super.getDocument().addDocumentListener(timerListener);
    }

    private boolean tableAssigned() {
        return table != null || (superTable != null && tableKey != null);
    }

    private boolean fieldAssigned() {
        return objKey != null && fieldKey != null;
    }
}