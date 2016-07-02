package myapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import myapp.Section;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Handles MyClassListTab. Certain parts are exposed to other Tabs.
 * @author bgregos
 *
 */

public class MyClassListController implements Initializable{

	public MyClassListController get(){
		return this;
	}

	public void initialize(URL location, ResourceBundle resources) {
		setupTable();
		System.out.println("B");

		//ArrayList<Section> classlist = new ArrayList<Section>();
		//MyClassListController.get().setAddedSections(FXCollections.observableArrayList(classlist));
	}

	private ObservableList<Section> addedSections=FXCollections.observableArrayList(new ArrayList<Section>());
	@FXML private TableView<Section> mySections=new TableView<Section>();
	@FXML private Button removeSelected;
	@FXML private Button updateFreeSpace;
	@FXML private TextArea mySectionsComments;

	@FXML private TableColumn<Section, String> CRN1;
	@FXML private TableColumn<Section, String> CourseNumber1;
	@FXML private TableColumn<Section, String> Title1;
	@FXML private TableColumn<Section, String> Type1;
	@FXML private TableColumn<Section, String> CreditHours1;
	@FXML private TableColumn<Section, String> Capacity1;
	@FXML private TableColumn<Section, String> Available1;
	@FXML private TableColumn<Section, String> Instructor1;
	@FXML private TableColumn<Section, String> Days1;
	@FXML private TableColumn<Section, String> Start1;
	@FXML private TableColumn<Section, String> End1;
	@FXML private TableColumn<Section, String> Location1;


	public ObservableList<Section> getAddedSections(){
		return addedSections;
	}

	public void setAddedSections(ObservableList<Section> in){
		addedSections=in;
	}

	public TableView<Section> getmySections(){
		return mySections;
	}

	public void setupTable() {
		CRN1.setCellValueFactory(new PropertyValueFactory<Section, String>("crn"));
		CourseNumber1.setCellValueFactory(new PropertyValueFactory<Section, String>("course"));
		Title1.setCellValueFactory(new PropertyValueFactory<Section, String>("title"));
		Type1.setCellValueFactory(new PropertyValueFactory<Section, String>("type"));
		CreditHours1.setCellValueFactory(new PropertyValueFactory<Section, String>("hours"));
		Capacity1.setCellValueFactory(new PropertyValueFactory<Section, String>("capacity"));
		Available1.setCellValueFactory(new PropertyValueFactory<Section, String>("available"));
		Instructor1.setCellValueFactory(new PropertyValueFactory<Section, String>("instructor"));
		Days1.setCellValueFactory(new PropertyValueFactory<Section, String>("days"));
		Start1.setCellValueFactory(new PropertyValueFactory<Section, String>("begin"));
		End1.setCellValueFactory(new PropertyValueFactory<Section, String>("end"));
		Location1.setCellValueFactory(new PropertyValueFactory<Section, String>("location"));
	}

	public void handleRemoveFromClasses() {
		SectionList.get().sections.remove(mySections.getSelectionModel().getSelectedItem());
		updateTable();
	}

	public void handleMySectionsClick() {
		try {
			Section s = mySections.getSelectionModel().getSelectedItem();
			mySectionsComments.setText(s.getDescription());
		} catch (NullPointerException e) {
			/*This can be safely ignored. It just means the user didn't click on
			  a section before hitting remove */
			System.out.println("My Classes Table null interaction");
		}
	}

	public void updateTable(){
		setupTable();
		mySections.setItems(SectionList.get().sections);
	}

	public void updateFree(){
		System.out.println("Update Free Pressed");
		SectionGetter getter = new SectionGetter();
		ObservableList<Section> workingArea=FXCollections.observableArrayList(new ArrayList<Section>());
		for(Section s:SectionList.get().sections){
			workingArea.add(getter.getSection(s.getCrn(), SectionList.get().loggedIn));
		}
		SectionList.get().sections=workingArea;
		
		
		updateTable();
	}


}
