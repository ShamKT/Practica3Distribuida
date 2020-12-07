import java.net.URL;
import java.util.LinkedList;
import java.util.ListIterator;
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
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 32 && change.getControlNewText().matches("[a-zñÑA-Z]*") ? change : null));

        txtDefinition.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 256 ? change : null));
        
        btnSearch.setOnAction(e -> searchWord());
        
        btnAddEdit.setOnAction(e -> addEditWord());
        
        btnUpdate.setOnAction(e -> updateList());

        txtWord.setOnKeyReleased(e -> verifyLength());
    }

    private void searchWord(){
        String word = txtWord.getText().replaceAll("ñ","&ntilde");
        word = word.replaceAll("Ñ","&Ntilde");
        txtDefinition.setText(Connection.getInstance().sendGet(new String[] {word, txtDefinition.getText()}));
    }
    
    private void addEditWord(){
        String word = txtWord.getText().replaceAll("ñ","&ntilde");
        word = word.replaceAll("Ñ","&Ntilde");
        Connection.getInstance().sendAddEdit(new String[] {word, txtDefinition.getText()});
    }
    
    private void updateList(){
        LinkedList<String> list = Connection.getInstance().sendGetList();

        
        for (ListIterator<String> i = list.listIterator(); i.hasNext(); )

        {
            String word = i.next();
            word = word.replaceAll("&ntilde","ñ");
            word = word.replaceAll("&Ntilde","Ñ");
            i.set(word);;
        }
        
        lstContent.setItems(FXCollections.observableList(list));

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
