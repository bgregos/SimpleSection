package myapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * This class handles the Section List Tab.
 * @author bgregos
 *
 */
public class SectionListController implements Initializable{
	// Section List Tab
		@FXML private Button getSectionsButton;
		@FXML private ComboBox<String> department;
		@FXML private TextField number;
		//@FXML private Label loading;
		@FXML private TextArea textarea;
		@FXML private Button LogInButton;
		@FXML private TextField PID;
		@FXML private PasswordField Password;
		@FXML private Button addToClasses;
		@FXML private TableView<Section> table;
		@FXML private TableColumn<Section, String> CRN;
		@FXML private TableColumn<Section, String> CourseNumber;
		@FXML private TableColumn<Section, String> Title;
		@FXML private TableColumn<Section, String> Type;
		@FXML private TableColumn<Section, String> CreditHours;
		@FXML private TableColumn<Section, String> Capacity;
		@FXML private TableColumn<Section, String> Available;
		@FXML private TableColumn<Section, String> Instructor;
		@FXML private TableColumn<Section, String> Days;
		@FXML private TableColumn<Section, String> Start;
		@FXML private TableColumn<Section, String> End;
		@FXML private TableColumn<Section, String> Location;
		@FXML private ProgressBar progress;
		@FXML private TextArea statusbox;

		@FXML private OverviewController overviewController;

		private ObservableList<Section> data;

		private ObservableList<Section> initializeTable() {
			ArrayList<Section> list = new ArrayList<Section>();
			data = FXCollections.observableArrayList(list);
			return data;
		}

		public void initialize(URL location, ResourceBundle resources) {
			System.out.println("Starting SimpleSection...");
			//loading.setVisible(false);
			initializeTable();
			MyClasses.get().driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest?term=09&year=2016");

			// Grab department list and put it into a dropdown
			Select dropdown = new Select(MyClasses.get().driver.findElement(By.name("subj_code")));
			ArrayList<WebElement> optionsListWebElements = (ArrayList<WebElement>) dropdown.getOptions();
			ArrayList<String> departmentOptions = new ArrayList<String>();
			for (WebElement e : optionsListWebElements) {
				departmentOptions.add(e.getText());
			}
			ObservableList<String> list = FXCollections.observableArrayList(departmentOptions);
			department.setItems(list);
		}

		public void handleGetSections() {
			progress.setVisible(true);
			progress.setProgress(-1); //indeterminate
			final Task<Void> task = new Task<Void>() {
				public Void call() { // This runs separate from the UI Thread to prevent hanging
					// The department list brings back a long string, this chops it down to CS, MATH, etc.
					String a = department.getValue().substring(0, department.getValue().indexOf("-") - 1);
					String b = number.getText(); // read course number
					data.clear();
					try {
						//loading.setVisible(true);
						SectionGetter secget = new SectionGetter(MyClasses.get().driver);
						ArrayList<Section> sectionlist = secget.getSections(a, Integer.parseInt(b), MyClasses.get().loggedIn); // gets section list
						for (int i = 0; i < sectionlist.size(); i++) { // add sections to table.
							data.add(sectionlist.get(i));
							if (sectionlist.get(i).extratimes) { // add extra time info if present
								data.get(i).days = data.get(i).days + "\n" + sectionlist.get(i).exDays;
								data.get(i).begin = data.get(i).begin + "\n" + sectionlist.get(i).exBegin;
								data.get(i).end = data.get(i).end + "\n" + sectionlist.get(i).exEnd;
								data.get(i).location = data.get(i).location + "\n" + sectionlist.get(i).exLocation;
							}
						}
						// add info to chart
						addInfoToChart();
						data = FXCollections.observableArrayList(data);
						table.setItems(data);
						sectionlist.clear();
					} catch (NumberFormatException e) { // if you don't enter numbers into the course field
						textarea.setText("Please enter a number into the course field.");
					}
					progress.setVisible(false);
					//loading.setVisible(false);
					return null;
				}
			};
			new Thread(task).start();
			task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
				public void handle(WorkerStateEvent t){
					progress.setVisible(false);
				}
			});


		}

		public void handleSort() {
			//TODO: add sorting capability
		}

		public void handleTableClick() {
			Section s = table.getSelectionModel().getSelectedItem();
			try {
				textarea.setText(s.getDescription());
			} catch (NullPointerException e) {

			}
		}

		public void handleLogIn() { // logs into VT HokieSpa.

			if (LogInButton.getText().equals("Log into HokieSpa")) {
				LogInButton.setText("Log in!");
				PID.setVisible(true);
				Password.setVisible(true);
			} else if (LogInButton.getText().equals("Log in!")) {
				progress.setVisible(true);
				progress.setProgress(0);
				LogInButton.setText("Authenticating...");
				PID.setVisible(false);
				Password.setVisible(false);
				System.out.println("Beginning login process...");

				final Task<Boolean> logInTask = new Task<Boolean>() {
					public Boolean call() {
						//Log in process
						String username = "";
						String pass = "";
						username = PID.getText();
						pass = Password.getText();
						MyClasses.get().driver.navigate().to("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_WWWLogin");
						updateProgress(1,7);
						System.out.println("1");
						MyClasses.get().driver.findElement(By.xpath("//body/div[2]/a")).click();
						updateProgress(2,7);
						System.out.println("2");
						MyClasses.get().driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys(username);
						System.out.println("3");
						updateProgress(3,7);
						MyClasses.get().driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(pass);
						System.out.println("4");
						updateProgress(4,7);
						MyClasses.get().driver.findElement(By.xpath("//*[@id=\"loginform\"]/fieldset/div[3]/button")).click();
						System.out.println("5");
						updateProgress(5,7);
						//correct: https://login.vt.edu/profile/cas/login?execution=e1s2
						//incorrect: https://login.vt.edu/profile/cas/login?execution=e2s2
						if(MyClasses.get().driver.getCurrentUrl().contains("=e2")){
							return false;
						}
						updateTextBox();
						MyClasses.get().driver.switchTo().frame("duo_iframe");
						System.out.println("6");
						updateProgress(6,7);
						try {
							Thread.sleep(3000); // add a wait to allow external
												// iframe to load.
						} catch (InterruptedException e) {
						}
						MyClasses.get().driver.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset[2]/div[1]/button")).click();
						// TODO: Will fail on very slow connectons, need try/catch.

						int wait = 0;
						// wait for user to do 2-factor auth. user has 3 minutes
						// until timeout.
						while (!MyClasses.get().driver.getCurrentUrl()
								.equals("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu")
								|| wait == 180) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
							wait++;
						}
						if(wait==180){
							return false;
						}

						System.out.println("7");
						updateProgress(7,7);
						MyClasses.get().driver.switchTo().defaultContent();
						return true;

					}
				};

				progress.progressProperty().bind(logInTask.progressProperty());
				new Thread(logInTask).start();

				logInTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
					public void handle(WorkerStateEvent t){
						//this runs when the login part finshes
						if(logInTask.getValue()==true){ //successful login
						progress.setVisible(false);
						LogInButton.setText("Logged in!");
						statusbox.setVisible(false);
						MyClasses.get().loggedIn = true;
						}else{ //wrong password
							statusbox.setVisible(true);
							statusbox.setText("It appears you've entered a wrong PID or password.");
							PID.setVisible(true);
							Password.setVisible(true);
							LogInButton.setText("Log in!");
						}
					}
				});

				logInTask.setOnFailed(new EventHandler<WorkerStateEvent>(){
					//Connection likely failed
					public void handle(WorkerStateEvent event) {
						statusbox.setVisible(true);
						statusbox.setText("Login process failed. Try checking your connection.");
						PID.setVisible(true);
						Password.setVisible(true);
						LogInButton.setText("Log in!");
					}

				});




				/*MyClasses.get().loggedIn = false;
				if (MyClasses.get().driver.getCurrentUrl()
						.equals("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu")) {
					MyClasses.get().loggedIn = true;
				}
				if (MyClasses.get().loggedIn == true) {
					LogInButton.setText("Logged in!");
				} else {
					PID.setVisible(true);
					Password.setVisible(true);
					LogInButton.setText("Log in!");
				}*/
			}
			if (LogInButton.getText().equals("Cancel")) {
				LogInButton.setText("Log in!");
			}
		}

		private void updateTextBox(){
			Platform.runLater(new Runnable(){
				public void run(){
					statusbox.setVisible(true);
					statusbox.setText("PID and password accepted. Finish login by accepting the Duo push sent to you.");
					LogInButton.setText("Waiting...");
				}
			});
		}

		private void addInfoToChart() {
			CRN.setCellValueFactory(new PropertyValueFactory<Section, String>("crn"));
			CourseNumber.setCellValueFactory(new PropertyValueFactory<Section, String>("course"));
			Title.setCellValueFactory(new PropertyValueFactory<Section, String>("title"));
			Type.setCellValueFactory(new PropertyValueFactory<Section, String>("type"));
			CreditHours.setCellValueFactory(new PropertyValueFactory<Section, String>("hours"));
			Capacity.setCellValueFactory(new PropertyValueFactory<Section, String>("capacity"));
			Available.setCellValueFactory(new PropertyValueFactory<Section, String>("available"));
			Instructor.setCellValueFactory(new PropertyValueFactory<Section, String>("instructor"));
			Days.setCellValueFactory(new PropertyValueFactory<Section, String>("days"));
			Start.setCellValueFactory(new PropertyValueFactory<Section, String>("begin"));
			End.setCellValueFactory(new PropertyValueFactory<Section, String>("end"));
			Location.setCellValueFactory(new PropertyValueFactory<Section, String>("location"));
		}

		public void handleAddToClasses(){
			try {
				Section s = table.getSelectionModel().getSelectedItem();
				//add to list
				MyClasses.get().sections.add(s);
				MyClasses.get().checkConflicts();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
