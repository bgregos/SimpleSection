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

	public void checkConflicts(){
		//clear all conflicts
		for(Section s:sections){
			s.setConflicts("");
		}

		int conflictnumber=97; //start at confict a when assigning names
		for(int i=0; i<sections.size(); i++){
			boolean conflictfound=false;
			for(int j=0; j<sections.size(); j++){ //checks all others down the list
				if(i!=j){
					int istart=parseTime(sections.get(i).getBegin());
					int iend=parseTime(sections.get(i).getEnd());
					int jstart=parseTime(sections.get(j).getBegin());
					int jend=parseTime(sections.get(j).getEnd());

					if( (istart<jend && iend>jstart) ){ //check interference

						String iCurrentConf = sections.get(i).getConflicts();
						String jCurrentConf = sections.get(j).getConflicts();
						boolean potconflictfound=true;

						for(int z=0; z<iCurrentConf.length(); z++){ //check that this interference hasn't been flagged yet
							if(jCurrentConf.contains(iCurrentConf.substring(z, z+1))){
								potconflictfound=false;
							}
						}
						if(potconflictfound){ //assign new conflict number
							sections.get(i).setConflicts(iCurrentConf+" "+(char)conflictnumber);
							sections.get(j).setConflicts(jCurrentConf+" "+(char)conflictnumber);
							conflictfound=true;
						}
					}
				}

			}
			if(conflictfound){
				conflictnumber++;
			}
		}
	}

	/**
	 * Takes a String AM/PM time and returns a int representation of it in minutes since midnight
	 * @return
	 */
	private int parseTime(String time){
		int out=0;
		int idx=time.indexOf(":");
		if(time.contains("PM")){
			out+=720;
		}
		int hours=Integer.parseInt(time.substring(0, idx));
		int minutes=Integer.parseInt(time.substring(idx+1,idx+3));
		out=(60*hours)+minutes;
		return out;
	}
}
