package myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * This class handles all tabs sans the Section List tab and the My Classes tab.
 * @author bgregos
 *
 */
public class OverviewController implements Initializable {

	// Schedule Tab
	@FXML private MyClassListController myClassListController;
	@FXML private ScheduleController scheduleTabController;
	@FXML private WebView webview;
	@FXML private Button backButton;
	@FXML private Button forwardButton;
	@FXML private Button koofers;
	@FXML private TextArea aboutText;
	private WebEngine webEngine;

	// About Tab
	//nothing here yet!

	//Runs at start, window does not appear until this method finishes.
	public void initialize(URL location, ResourceBundle resources) {
		webEngine=webview.getEngine();
		webEngine.load("https://www.koofers.com/virginia-tech-vt/professors");
		File file = new File("README.txt");
		try {
	        Scanner s = new Scanner(file);
	        while (s.hasNext()) {
	            aboutText.appendText(s.nextLine()+"\n");
	        }
	        s.close();
	    } catch (FileNotFoundException ex) {
	        System.err.println(ex);
	    }

	}



	public void onClickMyClasses(){
		System.out.println("Switched Tabs");
		myClassListController.updateTable();
	}

	public void onClickSchedule(){
		System.out.println("Switched Tabs");
		scheduleTabController.refresh();

	}

	public void onBack(){
		webEngine.getHistory().go(-1);
	}

	public void onForward(){
		webEngine.getHistory().go(1);
	}

	public void onKoofers(){
		webEngine.load("https://www.koofers.com/virginia-tech-vt/professors");
	}
}