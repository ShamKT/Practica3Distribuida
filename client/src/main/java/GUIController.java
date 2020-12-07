import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class GUIController implements Initializable {
    
    @FXML
    private TextField txtWord;

    @FXML
    private Button btnSearch;

    @FXML
    private TextArea txtDefinition;

    @FXML
    private Button btnAddEdit;

    @FXML
    private ListView<String> lstContent;

    @FXML
    private Button btnUpdate;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        btnSearch.setDisable(true);
        btnAddEdit.setDisable(true);

        txtWord.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 32 && change.getControlNewText().matches("[a-zA-Z]*") ? change : null));

        txtDefinition.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 256 ? change : null));
        
        btnSearch.setOnAction(e -> searchWord());
        
        btnAddEdit.setOnAction(e -> addEditWord());
        
        btnUpdate.setOnAction(e -> updateList());

        txtWord.setOnKeyReleased(e -> verifyLength());
    }

    private void searchWord(){
        txtDefinition.setText(Connection.getInstance().sendGet(new String[] {txtWord.getText(), txtDefinition.getText()}));
    }
    
    private void addEditWord(){
        Connection.getInstance().sendAddEdit(new String[] {txtWord.getText(), txtDefinition.getText()});
    }
    
    private void updateList(){
        lstContent.setItems(FXCollections.observableList(Connection.getInstance().sendGetList()));

    }

    private void verifyLength(){
        if (txtWord.getText().length() == 0) {
            btnSearch.setDisable(true);
            btnAddEdit.setDisable(true);
        } else {
            btnSearch.setDisable(false);
            btnAddEdit.setDisable(false);
        }
    }
    
}
