package myapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;

import myapp.Section;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

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
	@FXML private TableColumn<Section, String> Conflicts1;
	@FXML private ProgressIndicator progress;
	@FXML final private FileChooser fileChooser = new FileChooser();


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
		Conflicts1.setCellValueFactory(new PropertyValueFactory<Section, String>("conflicts"));
	}

	public void handleRemoveFromClasses() {
		MyClasses.get().sections.remove(mySections.getSelectionModel().getSelectedItem());
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
		MyClasses.get().checkConflicts();
		mySections.setItems(MyClasses.get().sections);
	}

	public void updateFree(){
		System.out.println("Update Free Pressed");
		progress.setVisible(true);
		final Task<Void> updateTask = new Task<Void>(){
			public Void call() {
				updateProgress(0, MyClasses.get().sections.size());
				SectionGetter getter = new SectionGetter(MyClasses.get().driver);
				ObservableList<Section> workingArea=FXCollections.observableArrayList(new ArrayList<Section>());
				int i=0;
				for(Section s:MyClasses.get().sections){
					workingArea.add(getter.getSection(s.getCrn(), MyClasses.get().loggedIn));
					i++;
					updateProgress(i, MyClasses.get().sections.size());
				}
				MyClasses.get().sections=workingArea;
				updateTable();

				return null;
			}
		};
		progress.progressProperty().bind(updateTask.progressProperty());
		new Thread(updateTask).start();
		updateTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			public void handle(WorkerStateEvent t){
				progress.setVisible(false);
			}
		});
		updateTask.setOnFailed(new EventHandler<WorkerStateEvent>(){
			public void handle(WorkerStateEvent t){
				progress.setVisible(false);
			}
		});

	}

	public void handleSaveClasses(){
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("VT Class files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().addAll(extFilter);
		File file = fileChooser.showSaveDialog(mySections.getScene().getWindow());
		if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
		    //add csv if not present
			file = new File(file.toString() + ".csv");
		    file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".csv"); // ALTERNATIVELY: remove the extension (if any) and replace it with ".xml"
		}
        if (file != null) {
        	try {
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				for(Section s:MyClasses.get().sections){
					writer.append(s.crn+"\n");
				}
				writer.flush();
				writer.close();
				System.out.println("File Saved.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        }
	}

	public void handleLoadClasses(){
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("VT Class files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().addAll(extFilter);
		File file = fileChooser.showOpenDialog(mySections.getScene().getWindow());
		ArrayList<Character> chars= new ArrayList<Character>();
		boolean read=false;
		try {
			FileReader reader = new FileReader(file);
			int in = reader.read();
			while(in!=-1){
				chars.add((char)in);
				in=reader.read();
			}
			reader.close();
			read=true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}

		if(read){
			String currentcrn="";
			SectionGetter secget=new SectionGetter(MyClasses.get().driver);
			for(char c:chars){
				if(c=='\n'){
					System.out.println("Current Read CRN: "+currentcrn);
					Section sect = secget.getSection(currentcrn, MyClasses.get().loggedIn);
					MyClasses.get().sections.add(sect);
					updateTable();
					currentcrn="";
				}else{
					currentcrn=currentcrn+c;
				}

			}
		}


	}



}
