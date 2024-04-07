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
    private String fieldKey;
    private String objKey;
    private String tableKey;
    private Map<String, Map<String, String>> table;
    private Map<String, Map<String, Map<String, String>>> superTable;    

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();    
    
    PropertyChangeListener allChangesListener = e -> onDateChanged();

    public DBSyncedDateChooser(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    public DBSyncedDateChooser(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super();

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    private void init() {
        this.getDateEditor().addPropertyChangeListener(allChangesListener);
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

    private void onDBUpdate()
    {
        this.pullState();
    }

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

        db.push();
    }

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

        this.getDateEditor().removePropertyChangeListener(allChangesListener);
        
        if (instant != null) {
            this.setDate(Date.from(instant));
        } else {
            this.setDate(null);
        }

        this.getDateEditor().addPropertyChangeListener(allChangesListener);
    }

    private boolean tableAssigned() {
        return table != null || (superTable != null && tableKey != null);
    }

    private boolean fieldAssigned() {
        return objKey != null && fieldKey != null;
    }
}