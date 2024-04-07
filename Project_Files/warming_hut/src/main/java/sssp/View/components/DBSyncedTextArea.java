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
    /**
     * The key of the field in the database that corresponds to the checkbox state.
     */
    private String fieldKey;

    /**
     * The key of the object in the database that contains the checkbox state.
     */
    private String objKey;

    /**
     * The key of the table in the database that corresponds to the checkbox state.
     * Can be null if the table is not being retrieved from a super-table.
     */
    private String tableKey;

    /**
     * The database table containing the checkbox state.
     * Can be null if the table is being retrieved from a super-table.
     */
    private Map<String, Map<String, String>> table;

    /**
     * The database table-of-tables containing the checkbox state.
     * Can be null if the table is not being retrieved from a super-table.
     */
    private Map<String, Map<String, Map<String, String>>> superTable;    

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    /**(milliseconds)
     */
    int delay = 2000;

    /** When this timer runs out, any changed text is committed to the DB.
     */
    Timer timer = new Timer(delay, new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            onTextChanged();
        }
    });

    /** Waits until the user stops editing for {@link #delay} seconds, then commits the text to the DB. */
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

    /**
     * Constructs a new DBSyncedTextArea with the specified number of rows and columns,
     * and initializes it with the provided table, object key, and field key.
     *
     * @param rows      the number of rows for the text area
     * @param columns   the number of columns for the text area
     * @param table     the table containing the data
     * @param objKey    the key for the object in the table
     * @param fieldKey  the key for the field in the object
     */
    public DBSyncedTextArea(Integer rows, Integer columns, Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        this(rows, columns, null, table, null, objKey, fieldKey);
    }

    /**
     * Constructs a new DBSyncedTextArea with the specified number of rows and columns,
     * and initializes it with the provided super table, table key, object key, and field key.
     *
     * @param table         the table containing the data
     * @param objKey        the key for the object in the table
     * @param fieldKey      the key for the field in the object
     */
    public DBSyncedTextArea(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        this(null, table, null, objKey, fieldKey);
    }

    /**
     * Constructs a new DBSyncedTextArea with the specified number of rows and columns,
     * and initializes it with the provided super table, table key, object key, and field key.
     *
     * @param rows          the number of rows for the text area
     * @param columns       the number of columns for the text area
     * @param superTable    the super table containing the data
     * @param tableKey      the key for the table in the super table
     * @param objKey        the key for the object in the table
     * @param fieldKey      the key for the field in the object
     */
    public DBSyncedTextArea(Integer rows, Integer columns, Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        this(rows, columns, superTable, null, tableKey, objKey, fieldKey);
    }

    /**
     * Constructs a new DBSyncedTextArea with the specified number of rows and columns,
     * and initializes it with the provided super table, table key, object key, and field key.
     *
     * @param superTable    the super table containing the data
     * @param tableKey      the key for the table in the super table
     * @param objKey        the key for the object in the table
     * @param fieldKey      the key for the field in the object
     */
    
    public DBSyncedTextArea(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        this(superTable, null, tableKey, objKey, fieldKey);
    }

    /**
     * This method sets up the event subscriptions needed by the class.
     * Additionally, it initializes {@link #timer}.
     * 
     * @see DBConnectorV2
     */
    private void init() {
        timer.setRepeats(false); // Make sure the timer only runs once

        // Add listeners for text area
        super.getDocument().addDocumentListener(timerListener);

        // Triggered when the DB updates
        db.subscribeRunnableToDBUpdate(this::onDBUpdate);
    }

    /**
     * Sets the database object this checkbox belongs to.
     * Useful when a single checkbox is used to represent different objects' properties at different times.
     * @param objKey The new database object.
     */
    public void setObjKey(String objKey) {
        this.objKey = objKey;
        this.pullState();
    }

    /**
     * Sets the database field key. This changes what value in the database that the checkbox corresponds to.
     * @param fieldKey The new database field key.
     */
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
        this.pullState();
    }

    /**
     * Updates the UI element when the DB is changed.
     */
    private void onDBUpdate()
    {
        this.pullState();
    }

    /**
     * Updates the DB when the text is changed
     */
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

    /**
     * Pulls the text area state from the database.
     */
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

    /**
     * Checks if a table is assigned for the checkbox.
     * @return True if a table is assigned, false otherwise.
     */
    private boolean tableAssigned() {
        return table != null || (superTable != null && tableKey != null);
    }

    /**
     * Checks if a field is assigned for the checkbox.
     * @return True if a field is assigned, false otherwise.
     */
    private boolean fieldAssigned() {
        return objKey != null && fieldKey != null;
    }
}