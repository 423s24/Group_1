package sssp.View.components;

import javax.swing.JCheckBox;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import java.util.Map;
import java.awt.event.ActionListener;

/**
 * This class extends the JCheckBox class to create a checkbox that is synced with the database.
 */
public class DBSyncedCheckBox extends JCheckBox {
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

    /**
     * Triggered when the checkbox is clicked.
     */
    ActionListener pressedListener = new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            onToggled();
        }
    };

    /**
     * A JCheckBox equivalent that is synchronized with the database.
     *
     * @param superTable The super table containing the field to be synchronized.
     * @param tableKey The key of the table containing the field to be synchronized.
     * @param objKey The key of the object containing the field to be synchronized.
     * @param fieldKey The key of the field to be synchronized.
     */
    public DBSyncedCheckBox(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JCheckBox equivalent that is synchronized with the database.
     *
     * @param table    The database table containing the checkbox data.
     * @param objKey   The key of the object in the table.
     * @param fieldKey The key of the field in the object.
     */
    public DBSyncedCheckBox(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super();

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }
    
    /**
     * A JCheckBox equivalent that is synchronized with the database.
     *
     * @param text The text to be displayed next to the checkbox.
     * @param table The database table containing the checkbox value.
     * @param objKey The key of the object in the database table.
     * @param fieldKey The key of the field in the database table.
     */
    public DBSyncedCheckBox(String text, Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super(text);

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JCheckBox equivalent that is synchronized with the database.
     *
     * @param text      The text to be displayed next to the checkbox.
     * @param superTable The super table containing the database information.
     * @param tableKey  The key to access the specific table in the super table.
     * @param objKey    The key to access the specific object in the table.
     * @param fieldKey  The key to access the specific field in the object.
     */
    public DBSyncedCheckBox(String text, Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super(text);

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * This method sets up the event subscriptions needed by the class.
     * 
     * @see DBConnectorV2
     */
    private void init() {
        // Triggered when the checkbox is clicked
        super.addActionListener(pressedListener);

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

        // Pull the current state from the database when this checkbox is assigned to a new object
        this.pullState();
    }

    /**
     * Sets the database field key. This changes what value in the database that the checkbox corresponds to.
     * @param fieldKey The new database field key.
     */
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;

        // Pull the current state from the database when this checkbox is assigned to a new field
        this.pullState();
    }

    /**
     * Updates the UI element when the DB is changed.
     */
    private void onDBUpdate()
    {
        // Pull the state from the database just incase it's changed
        this.pullState();
    }

    /**
     * Updates the DB when the checkbox is toggled.
     */
    private void onToggled()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        targetObj.put(fieldKey, String.valueOf(this.isSelected()));

        db.asyncPush();
    }

    /**
     * Pulls the checkbox state from the database.
     */
    private void pullState()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        // Necessary to avoid calling the action listener when setting the checkbox state
        super.removeActionListener(pressedListener);

        // Get our target object from the database
        Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        // Set the checkbox state based on the corresponding field in the database
        this.setSelected(Boolean.parseBoolean(targetObj.get(fieldKey)));

        // Re-add the action listener
        super.addActionListener(pressedListener);
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