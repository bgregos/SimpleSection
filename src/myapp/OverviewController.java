package myapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class OverviewController implements Initializable{


	@FXML private Button getSectionsButton;
	@FXML private ChoiceBox<String> department;
	@FXML private TextField number;
	@FXML private Label loading;
	@FXML private TextArea textarea;
	@FXML private Button LogInButton;
	@FXML private TextField PID;
	@FXML private PasswordField Password;
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

	private boolean loggedIn=false;
	private ObservableList<Section> data;
	private SectionGetter secget = new SectionGetter();
	private WebDriver driver= new JBrowserDriver(Settings.builder().headless(true).cache(true).build());

	private ObservableList<Section> initializeTable() {
		ArrayList<Section> list = new ArrayList<Section>();
		data = FXCollections.observableArrayList(list);
		return data;
		}

	public void initialize(URL location, ResourceBundle resources) { //runs at start, window does not appear until the this method finishes.
		System.out.println("Starting SimpleSection...");
		loading.setVisible(false);
		initializeTable();
		driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest?term=09&year=2016");

		//Grab department list and put it into a dropdown
		Select dropdown = new Select(driver.findElement(By.name("subj_code")));
		ArrayList<WebElement> optionsListWebElements=(ArrayList<WebElement>) dropdown.getOptions();
		ArrayList<String> departmentOptions = new ArrayList<String>();
		for(WebElement e:optionsListWebElements){
			departmentOptions.add(e.getText());
		}
		ObservableList<String> list = FXCollections.observableArrayList(departmentOptions);
		department.setItems(list);


	}

	public void handleGetSections(){
		data.clear();
		Thread thread = new Thread(){
			public void run(){ //This runs separate from the UI Thread to prevent hanging
				String a=department.getValue().substring(0, department.getValue().indexOf("-")-1); //The department list brings back a long string, this chops it down to CS, MATH, etc.
				String b=number.getText(); //read course number
				try{
					loading.setVisible(true);
					ArrayList<Section> sectionlist = secget.getSections(driver, a, Integer.parseInt(b), loggedIn); //gets section list
					for(int i=0; i<sectionlist.size(); i++){ //add sections to table.
						data.add(sectionlist.get(i));
						if(sectionlist.get(i).extratimes){ //add extra time info if present
							data.get(i).days=data.get(i).days+"\n"+sectionlist.get(i).exDays;
							data.get(i).begin=data.get(i).begin+"\n"+sectionlist.get(i).exBegin;
							data.get(i).end=data.get(i).end+"\n"+sectionlist.get(i).exEnd;
							data.get(i).location=data.get(i).location+"\n"+sectionlist.get(i).exLocation;
						}
					}
					//add info to chart
					addInfoToChart();
					data = FXCollections.observableArrayList(data);
					table.setItems(data);
					sectionlist.clear();
				}
				catch(NumberFormatException e){ //if you don't enter numbers into the course field
					textarea.setText("Please enter a number into the course field.");
				}

				loading.setVisible(false);
				return;
			}
		};
		thread.start();

	}

	public void handleSort(){
		//TODO
	}

	public void handleTableClick(){
		Section s = table.getSelectionModel().getSelectedItem();
		try{
			textarea.setText(s.getDescription());
		}
		catch(NullPointerException e){

		}
	}

	public void handleLogIn(){ //logs into VT HokieSpa. Probably needs to stay in the controller due to webdriver limitations (unless all logic is moved out to another class)

		if(LogInButton.getText().equals("Log into HokieSpa")){
			LogInButton.setText("Log in!");
			PID.setVisible(true);
			Password.setVisible(true);
		}else if(LogInButton.getText().equals("Log in!")){
			Thread thread = new Thread(){
				public void run(){
					PID.setVisible(false);
					Password.setVisible(false);
					String username="";
					String pass="";
					username=PID.getText();
					pass=Password.getText();
					driver.navigate().to("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_WWWLogin");;
					System.out.println("1");
					driver.findElement(By.xpath("//body/div[2]/a")).click();
					System.out.println("2");
					driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys(username);
					System.out.println("3");
					driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(pass);
					System.out.println("4");
					driver.findElement(By.xpath("//*[@id=\"loginform\"]/fieldset/div[3]/button")).click();
					System.out.println("5");
					driver.switchTo().frame("duo_iframe");
					System.out.println("6");
					try {
						Thread.sleep(3000); //add a wait to allow external page to load.
					} catch (InterruptedException e) {}
					driver.findElement(By.xpath("//*[@id=\"login-form\"]/fieldset[2]/div[1]/button")).click(); //TODO: Will fail on very slow connections; need try/catch.
					int wait=0;
					//wait for user to do 2-factor auth. user has 3 minutes until timeout.
					while(!driver.getCurrentUrl().equals("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu")||wait==180){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
						wait++;
					}

					System.out.println("7");
					driver.switchTo().defaultContent();

				}
			};

			LogInButton.setText("Authenticating..."); //TODO: This doesn't happen and the main UI freezes. Replace join below with a better solution.
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				loggedIn=false;
			if(driver.getCurrentUrl().equals("https://banweb.banner.vt.edu/ssb/prod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu")){
				loggedIn=true;
			}
			if(loggedIn==true){
				LogInButton.setText("Logged in!");
			}else{
				PID.setVisible(true);
				Password.setVisible(true);
				LogInButton.setText("Log in!");
			}
		}if(LogInButton.getText().equals("Cancel")){
			LogInButton.setText("Log in!");
		}

	}

	private void addInfoToChart(){
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

}