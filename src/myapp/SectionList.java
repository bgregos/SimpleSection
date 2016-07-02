package myapp;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This singleton facilitates communication between the Section List tab and the My Classes tab.
 * It contains the list of sections the user has saved and current login status.
 * @author bgregos
 *
 */
public class SectionList {

	//Constructor and Singleton stuff
	private static SectionList instance = new SectionList();
	public SectionList(){

	}
	public static SectionList get(){
		return instance;
	}

	//Actual data we're holding
	public ObservableList<Section> sections=FXCollections.observableArrayList(new ArrayList<Section>());
	public boolean loggedIn=false;
}
