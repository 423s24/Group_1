package sssp.View.components;

import javax.swing.JTextField;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * This class extends the JTextField class to create a text field that is synced with the database.
 */
public class DBSyncedTextField extends JTextField {
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
     */
    private String tableKey;
    private Map<String, Map<String, String>> table;
    private Map<String, Map<String, Map<String, String>>> superTable;    

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    /**
     * Triggered when the text field is set- this can be by:
     * * The user pressing the enter key after selecting the field.
     * * The user clicking away from the field.
     */
    ActionListener textSubmittedListener = e -> onTextSubmitted();

    /**
     * A JTextField equivalent that is synchronized with the database.
     *
     * @param columns the number of columns for the text field
     * @param superTable the super table containing the data
     * @param tableKey the key for the table in the super table
     * @param objKey the key for the object in the table
     * @param fieldKey the key for the field in the object
     */
    public DBSyncedTextField(int columns, Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super(columns);

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JTextField equivalent that is synchronized with the database.
     *
     * @param superTable the super table containing the data
     * @param tableKey the key of the table in the super table
     * @param objKey the key of the object in the table
     * @param fieldKey the key of the field in the object
     */
    public DBSyncedTextField(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JTextField equivalent that is synchronized with the database.
     *
     * @param columns The number of columns to display in the text field.
     * @param table The database table containing the data to be synchronized.
     * @param objKey The key of the object in the table.
     * @param fieldKey The key of the field in the object.
     */
    public DBSyncedTextField(int columns, Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super(columns);

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JTextField equivalent that is synchronized with the database.
     * 
     * @param table The database table containing the data to be synchronized.
     * @param objKey The key of the object in the table.
     * @param fieldKey The key of the field in the object.
     */
    public DBSyncedTextField(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super();

        this.table = table;
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
        // Triggered when the text field is updated
        super.addActionListener(textSubmittedListener);

        // Triggered when the DB updates
        db.subscribeRunnableToDBUpdate(this::onDBUpdate);
    }

    /**
     * Sets the database object this text field belongs to.
     * Useful when a single text field is used to represent different objects' properties at different times.
     * @param objKey The new database object.
     */
    public void setObjKey(String objKey) {
        this.objKey = objKey;
        this.pullState();
    }

    /**
     * Sets the database field key. This changes what value in the database that the text field corresponds to.
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
     * Updates the DB when the text is changed.
     */
    private void onTextSubmitted()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }        
        
        Map<String, String> targetObject = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        // Update the DB with the text field value
        targetObject.put(fieldKey, this.getText());

        db.push();
    }

    /**
     * Pulls the textbox state from the database.
     */
    private void pullState()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        super.removeActionListener(textSubmittedListener);

        Map<String, String> targetObject = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        this.setText(targetObject.get(fieldKey));

        super.addActionListener(textSubmittedListener);
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