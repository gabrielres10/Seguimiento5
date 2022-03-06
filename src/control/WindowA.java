package control;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Transaction;
import model.TransactionData;
import model.Type;

public class WindowA implements Initializable {

	private String[] types = { "GASTO", "INGRESO" };
	// Buttons
	@FXML
	private Button addBTN;
	@FXML
	private Button deleteBTN;
	@FXML
	private Button applyFilterBTN;

	// Table View
	@FXML
	private TableView<Transaction> movesTable;
	@FXML
	private TableColumn<Transaction, String> valueCol;
	@FXML
	private TableColumn<Transaction, Type> typeCol;
	@FXML
	private TableColumn<Transaction, String> descriptionCol;
	@FXML
	private TableColumn<Transaction, LocalDate> dateCol;

	// Text Fields
	@FXML
	private TextField descTF;
	@FXML
	private TextField valueTF;
	@FXML
	private TextField balanceTF;
	@FXML
	private TextField expensesTF;
	@FXML
	private TextField incomesTF;

	// Choice Box
	@FXML
	private ChoiceBox<String> typeCB;

	// Date Picker
	@FXML
	private DatePicker higherDateDP;
	@FXML
	private DatePicker inferiorDateDP;
	@FXML
	private DatePicker dateDP;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		clear();
		deleteBTN.setDisable(true);
		// Filling the choice box of types
		fillTypeCB();

		// Initialization of the columns in the table view
		valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
	}

	/**
	 * Adds a new quantity with the info in the interface
	 * 
	 * @param event
	 * @throws ParseException
	 */
	@FXML
	void addQuantity(ActionEvent event) throws ParseException {
		// Initializes independent variables
		Type valueType = Type.GASTO;
		String value = "";
		Double doubleValue = 0.0;

		// This section validates if one of the attributes of the transaction has not
		// been filled
		if (dateDP.getValue() == null || descTF.getText().equals("") || valueTF.getText().equals("")
				|| typeCB.getValue() == null) {

			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Debe completar todos los datos");
			alert.setContentText("Uno o m�s campos de la transacci�n est�n sin llenar");
			Optional<ButtonType> result = alert.showAndWait();

		} else {

			// Gets the values from the scene builder interface
			String description = descTF.getText();
			String type = typeCB.getValue();
			LocalDate date = dateDP.getValue();
			// sets the type according the enumeration class "Type"
			if (type.equals("INGRESO")) {
				valueType = Type.INGRESO;
			}
			// Validations of the date types and classes
			if (confirmDouble(valueTF.getText())) {
				value = valueTF.getText();
				doubleValue = Double.parseDouble(value);
				TransactionData.data.add(new Transaction(doubleValue, description, valueType, date));
				movesTable.setItems(TransactionData.data);
				calculateBalance(TransactionData.data);
				clear();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error de tipo de dato");
				alert.setContentText(
						"El n�mero ingresado no cuenta con el formato de tipo decimal (Use \".\" como separador)");
				Optional<ButtonType> result = alert.showAndWait();
				clear();
			}

		}
	}

	/**
	 * Filters the list according an inferior and superior date
	 * 
	 * @param event
	 */
	@FXML
	void applyFilter(ActionEvent event) {
		if (higherDateDP.getValue() == null || inferiorDateDP.getValue() == null) {

			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Elija ambos filtros");
			alert.setContentText("Debe seleccionar una fecha inicial y una fecha final para el filtrado ");
			Optional<ButtonType> result = alert.showAndWait();

		} else {

			ObservableList<Transaction> filteredListPerDate = FXCollections.observableArrayList();

			LocalDate lowerDate = inferiorDateDP.getValue();

			LocalDate higherDate = higherDateDP.getValue();

			for (Transaction t : TransactionData.data) {
				if ((t.getDate().isAfter(lowerDate) || t.getDate().equals(lowerDate))
						&& (t.getDate().isBefore(higherDate) || t.getDate().equals(higherDate))) {

					filteredListPerDate.add(t);

				}
			}

			movesTable.setItems(filteredListPerDate);
			calculateBalance(filteredListPerDate);
		}

	}

	/**
	 * This method disables the add button when an item of the table is selected
	 * 
	 * @param event
	 */
	@FXML
	void disableAddBtn(MouseEvent event) {
		if (movesTable != null) {
			List<Transaction> table = movesTable.getSelectionModel().getSelectedItems();
			if (table.size() == 1) {
				addBTN.setDisable(true);
				deleteBTN.setDisable(false);
			}
		}
	}

	/**
	 * This method enables the add button and disables the delete button when any
	 * item of the table is selected
	 * 
	 * @param event
	 */
	@FXML
	void enableAddBtn(MouseEvent event) {
		addBTN.setDisable(false);
		deleteBTN.setDisable(true);
	}

	/**
	 * Deletes an item of the table
	 * 
	 * @param event
	 */
	@FXML
	void deleteItemBTN(ActionEvent event) {
		Transaction p = (Transaction) movesTable.getSelectionModel().getSelectedItem();
		TransactionData.data.remove(p);
		if (higherDateDP.getValue() != null && inferiorDateDP.getValue() != null) {
			applyFilter(event);
		} else {
			movesTable.setItems(TransactionData.data);
			calculateBalance(TransactionData.data);
		}
		deleteBTN.setDisable(true);
		addBTN.setDisable(false);

	}

	/**
	 * Shows the list without filters
	 * 
	 * @param event
	 */
	@FXML
	void undoFilter(ActionEvent event) {
		movesTable.setItems(TransactionData.data);
		calculateBalance(TransactionData.data);
		clearDateLimits();
	}

	/**
	 * Fills the choice box of the type of Transaction
	 */
	public void fillTypeCB() {
		typeCB.getItems().addAll(types);
	}

	/**
	 * Calculates the total balance by the incomes and expenses additions, it also
	 * sets the Text Fields that contain this information
	 */
	public void calculateBalance(ObservableList<Transaction> currentData) {
		Type income = Type.INGRESO;
		double incomes = 0.0;
		double expenses = 0.0;

		for (Transaction value : currentData) {
			if (value.getType().equals(income)) {
				incomes += value.getValue();
			} else {
				expenses += value.getValue();
			}

		}

		double balance = incomes - expenses;
		incomesTF.setText(incomes + " $");
		expensesTF.setText(expenses + " $");
		balanceTF.setText(balance + " $");

	}

	/**
	 * Verifies that a string can be passed to type double
	 * 
	 * @param value, String, string being checked
	 * @return out, boolean, true true if string can be passed to double, false
	 *         otherwise
	 */
	public boolean confirmDouble(String value) {

		boolean out = true;
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) != '.' && value.charAt(i) != '1' && value.charAt(i) != '2' && value.charAt(i) != '3'
					&& value.charAt(i) != '4' && value.charAt(i) != '5' && value.charAt(i) != '6'
					&& value.charAt(i) != '7' && value.charAt(i) != '8' && value.charAt(i) != '9'
					&& value.charAt(i) != '0') {
				out = false;
				break;
			}
		}
		return out;
	}

	/**
	 * Clears both date limiters
	 */
	public void clearDateLimits() {
		if (inferiorDateDP != null) {
			inferiorDateDP.setValue(null);
		}

		if (higherDateDP != null) {
			higherDateDP.setValue(null);
		}
	}

	/**
	 * Clears all the text fields
	 */
	public void clear() {

		if (dateDP != null) {
			dateDP.setValue(null);
		}

		if (descTF != null) {
			descTF.clear();
		}

		if (valueTF != null) {
			valueTF.clear();
		}

		if (typeCB != null) {
			typeCB.setValue(null);
		}
	}

}