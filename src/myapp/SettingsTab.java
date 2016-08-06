package myapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

public class SettingsTab implements Initializable {

	@FXML private ComboBox<String> term;
	@FXML private Button debug;

	private ObservableList<String> availableTerms = FXCollections.observableArrayList(new ArrayList<String>());

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//Platform.setImplicitExit(false);

		//Platform.runLater(init);
	}

	public SettingsTab(){
	}

	public void saveSettings(){
		SettingsStore.get().selectedTerm=term.getValue();
		SettingsStore.get().writeSettings();
	}

	public void updateTerm(){
		availableTerms=SettingsStore.get().availableTerms;
		System.out.println("Selected: "+SettingsStore.get().selectedTerm);
		term.getItems().setAll(availableTerms);
		for(String s:term.getItems()){
			System.out.println(s);
		}
		System.out.println("Available Terms:");
		for(String s:availableTerms){
			System.out.print(s+", ");
			if(s.equals(SettingsStore.get().selectedTerm)){
				term.setValue(s);
			}
		}
		System.out.println("");

	}
}
