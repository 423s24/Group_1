package sssp.View.components;

import com.toedter.calendar.JDateChooser;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;

import java.util.Map;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * This class extends the JDateChooser class to create a date chooser that is synced with the database.
 */
public class DBSyncedDateChooser extends JDateChooser {
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
     * Triggered when the date is picked.
     */
    PropertyChangeListener allChangesListener = e -> onDateChanged();

    /**
     * A JDateChooser equivalent that is synchronized with the database.
     *
     * @param superTable The super table containing the field to be synchronized.
     * @param tableKey The key of the table containing the field to be synchronized.
     * @param objKey The key of the object containing the field to be synchronized.
     * @param fieldKey The key of the field to be synchronized.
     */
    public DBSyncedDateChooser(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    /**
     * A JDateChooser equivalent that is synchronized with the database.
     *
     * @param table    The database table containing the checkbox data.
     * @param objKey   The key of the object in the table.
     * @param fieldKey The key of the field in the object.
     */
    public DBSyncedDateChooser(Map<String, Map<String, String>> table, String objKey, String fieldKey)
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
        this.getDateEditor().addPropertyChangeListener(allChangesListener);
        db.subscribeRunnableToDBUpdate(this::onDBUpdate);
    }

    /**
     * Sets the database object this date chooser belongs to.
     * Useful when a single date chooser is used to represent different objects' properties at different times.
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
     * Updates the DB when the date is set.
     */
    private void onDateChanged()
    {
        if(!fieldAssigned() || !tableAssigned())
        {
            return;
        }

        Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        // Update the DB with the date chooser value
        Date date = this.getDate();
        if (date != null) {
            // Convert the date to an Instant for easy serialization
            Instant instant = date.toInstant();
            targetObj.put(fieldKey, instant.toString());
        } else {
            targetObj.put(fieldKey, null);
        }

        db.asyncPush();
    }

    /**
     * Pulls the date chooser state from the database.
     */
    private void pullState() {
        if(!fieldAssigned() || !tableAssigned()) {
            return;
        }

        // Get an Instant from the string representation in the database
        Instant instant = null;
        try
        {
            Map<String, String> targetObj = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

            String instantStr = targetObj.get(fieldKey);

            if(instantStr != null)
            {
                instant = Instant.parse(instantStr);
            }
        }
        catch (DateTimeParseException e)
        {
            System.out.println("Could not parse date string to an Instant.");
            e.printStackTrace();
        }

        // remove the property listener when we set the date to avoid self-triggering
        this.getDateEditor().removePropertyChangeListener(allChangesListener);
        
        if (instant != null) {
            this.setDate(Date.from(instant));
        } else {
            this.setDate(null);
        }

        this.getDateEditor().addPropertyChangeListener(allChangesListener);
    }

    /**
     * Checks if a table is assigned for the date chooser.
     * @return True if a table is assigned, false otherwise.
     */
    private boolean tableAssigned() {
        return table != null || (superTable != null && tableKey != null);
    }
    
    /**
     * Checks if a field is assigned for the date chooser.
     * @return True if a field is assigned, false otherwise.
     */
    private boolean fieldAssigned() {
        return objKey != null && fieldKey != null;
    }
}