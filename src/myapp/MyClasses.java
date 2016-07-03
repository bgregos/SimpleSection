package myapp;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This singleton facilitates communication between the Section List tab and the My Classes tab.
 * It contains the list of sections the user has saved and current login status.
 * @author bgregos
 *
 */
public class MyClasses {

	//Constructor and Singleton stuff
	private static MyClasses instance = new MyClasses();
	public MyClasses(){

	}
	public static MyClasses get(){
		return instance;
	}

	//Actual data we're holding
	public ObservableList<Section> sections=FXCollections.observableArrayList(new ArrayList<Section>());
	public boolean loggedIn=false;
	public WebDriver driver = new JBrowserDriver(Settings.builder().headless(true).cache(true).build());
}
