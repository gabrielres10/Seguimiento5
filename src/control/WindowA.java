package control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class WindowA {
	
	 @FXML
	    private Button addBTN;

	    @FXML
	    private TextField balanceTF;

	    @FXML
	    private TableColumn<?, ?> codeCol;

	    @FXML
	    private TableColumn<?, ?> nameCol;

	    @FXML
	    private TableColumn<?, ?> natIDCol;

	    @FXML
	    private TableView<?> studentTable;

	    @FXML
	    void registerWindowBTN(ActionEvent event) {

	    }
}
