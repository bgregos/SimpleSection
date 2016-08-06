package myapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Handles user-defined settings. This class is a singleton.
 * @author bgregos
 *
 */
public class SettingsStore {

	//Constructor and Singleton stuff
	private static SettingsStore instance = new SettingsStore();

	private SettingsStore(){
		System.out.println("Singleton Initialized");
	}
	public static SettingsStore get(){
		return instance;
	}

	//Actual data we're holding
	ObservableList<String> availableTerms = FXCollections.observableArrayList(new ArrayList<String>());
	public String selectedTerm="";
	public String year="2016";

	Properties prop = new Properties();
	OutputStream output = null;
	InputStream input = null;

	public void initSettings(){
		try{
			System.out.println(SettingsStore.get().year);
			//Make a new sectiongetter, but use the application's main WebDriver to reduce runtime.
			SectionGetter secget = new SectionGetter(MyClasses.get().driver);
			System.out.println(SettingsStore.get().year);
			availableTerms=FXCollections.observableArrayList(secget.getTerms());
			System.out.println(SettingsStore.get().year);
			//availableTerms.add("Fall 2016");
			SettingsStore.get().readSettings();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void writeSettings(){
		try{
			output = new FileOutputStream("res/settings.properties");
			prop.setProperty("Term", selectedTerm);
			prop.store(output, null);
			output.close();
		}catch(IOException e){
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Oh my!");
			alert.setContentText("Read error occurred.");
			alert.showAndWait();
		}
	}

	public void readSettings(){
		System.out.println("Loading Settings");
		try{

			input = new FileInputStream("res/settings.properties");
			prop.load(input);
			selectedTerm=prop.getProperty("Term");
			input.close();
		}catch(IOException e){
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Oh my!");
			alert.setContentText("Settings read error occurred.");
			alert.showAndWait();
		}
	}


}