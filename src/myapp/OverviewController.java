package myapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

/**
 * This class handles all tabs sans the Section List tab and the My Classes tab.
 * @author bgregos
 *
 */
public class OverviewController implements Initializable {

	// Schedule Tab
	@FXML private TreeView<Section> schedule; // TODO implement MTWRF and time sort

	@FXML private MyClassListController myClassListController;

	// About Tab
	//nothing here yet!

	//Runs at start, window does not appear until this method finishes.
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void onClickMyClasses(){
		System.out.println("Switched Tabs");
		myClassListController.updateTable();
	}
}