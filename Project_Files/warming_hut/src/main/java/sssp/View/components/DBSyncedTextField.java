package sssp.View.components;

import javax.swing.JTextField;
import sssp.Helper.DBConnectorV2;
import sssp.Helper.DBConnectorV2Singleton;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * This class extends the JTextField class to create a text field that is synced with the database.
 */
public class DBSyncedTextField extends JTextField {
    private String fieldKey;
    private String objKey;
    private String tableKey;
    private Map<String, Map<String, String>> table;
    private Map<String, Map<String, Map<String, String>>> superTable;    

    DBConnectorV2 db = DBConnectorV2Singleton.getInstance();

    DocumentListener allChangesListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
            onTextChanged();
        }
        public void removeUpdate(DocumentEvent e) {
            onTextChanged();
        }
        public void insertUpdate(DocumentEvent e) {
            onTextChanged();
        }
    };

    public DBSyncedTextField(int columns, Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super(columns);

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    public DBSyncedTextField(Map<String, Map<String, Map<String, String>>> superTable, String tableKey, String objKey, String fieldKey)
    {
        super();

        this.superTable = superTable;
        this.tableKey = tableKey;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    public DBSyncedTextField(int columns, Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super(columns);

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    public DBSyncedTextField(Map<String, Map<String, String>> table, String objKey, String fieldKey)
    {
        super();

        this.table = table;
        this.objKey = objKey;
        this.fieldKey = fieldKey;

        init();
    }

    private void init() {
        // Triggered when the text field is updated
        super.getDocument().addDocumentListener(allChangesListener);

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
        
        Map<String, String> targetObject = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        // Update the DB with the text field value
        targetObject.put(fieldKey, this.getText());

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

        super.getDocument().removeDocumentListener(allChangesListener);        

        Map<String, String> targetObject = table != null ? table.get(objKey) : superTable.get(tableKey).get(objKey);

        this.setText(targetObject.get(fieldKey));

        super.getDocument().addDocumentListener(allChangesListener);
    }    
    
    private boolean tableAssigned() {
        return table != null || (superTable != null && tableKey != null);
    }

    private boolean fieldAssigned() {
        return objKey != null && fieldKey != null;
    }
}