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

	/*public void checkConflicts(){
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
					String idays=sections.get(i).getDays();
					String jdays=sections.get(j).getDays();

					if( (istart<jend && iend>jstart) && checkDays(idays,jdays)){ //check for interference

						String iCurrentConf = sections.get(i).getConflicts();
						String jCurrentConf = sections.get(j).getConflicts();
						boolean potconflictfound=true; //potential conflict

						for(int z=0; z<iCurrentConf.length(); z++){ //check that this interference hasn't been flagged yet
							if(jCurrentConf.contains(iCurrentConf.substring(z, z+1))){
								potconflictfound=false;
							}
						}
						if(potconflictfound){ //assign new conflict number
							conflictnumber= checkConflictNumber(conflictnumber);
							sections.get(i).setConflicts(iCurrentConf+" "+(char)conflictnumber);
							sections.get(j).setConflicts(jCurrentConf+" "+(char)conflictnumber);
							conflictfound=true;
						}
					}
				}
				if(conflictfound){
					conflictnumber++;
				}
			}

		}
	}*/

	public void checkConflicts(){
		//clear all conflicts
		for(Section s:sections){
			s.setConflicts("");
		}

		int confnum=1;
		for(int i=0; i<sections.size(); i++){
			for(int j=0; j<sections.size(); j++){

				int buffer=7; //Half of time between classes. VT is 15 min. Round down.
				//To make each class "wider"
				int istart=parseTime(sections.get(i).getBegin())-buffer;
				int iend=parseTime(sections.get(i).getEnd())+buffer;
				int jstart=parseTime(sections.get(j).getBegin())-buffer;
				int jend=parseTime(sections.get(j).getEnd())+buffer;
				String idays=sections.get(i).getDays();
				String jdays=sections.get(j).getDays();
				String icrn=sections.get(i).getCrn();
				String jcrn=sections.get(j).getCrn();
				String iCurrentConf = sections.get(i).getConflicts();
				String jCurrentConf = sections.get(j).getConflicts();


				if(i!=j && (istart<jend && iend>jstart) && checkDays(idays,jdays)){ //check if conflict occurred
					if(!(iCurrentConf.contains(jcrn) && jCurrentConf.contains(icrn))){ //make sure conflict is not already logged between the two
						sections.get(i).setConflicts(iCurrentConf+jcrn+" ");
						sections.get(j).setConflicts(jCurrentConf+icrn+" ");
					}
				}

			}
		}

	}

	/**
	 * Prevents skipping conflict numbers
	 * @param int conflictnumber
	 */
	private int checkConflictNumber(int number){
		for(int i=0; i<sections.size(); i++){
			if(!sections.get(i).getConflicts().contains(""+(char)number)){
				return number--;
			}
		}
		return number;
	}

	/**
 	* Checks if any days are in common
 	* @param day string a
 	* @param day string b
 	* @return true if at least one day is the same
 	*/
	private boolean checkDays(String a, String b){
		//remove whitespace
		a=a.replaceAll("\\s", "");
		b=b.replaceAll("\\s", "");
		System.out.println(a+" "+b);

		for(int i=0; i<a.length(); i++){
			if(b.contains(a.substring(i, i+1))){
				return true;
			}
		}
		return false;
	}

	/**
	 * Takes a String AM/PM time and returns a int representation of it in minutes since midnight
	 * @return int minutes since midnight
	 */
	private int parseTime(String time){ //TODO: Fix 12am/pm handling
		int out=0;
		int idx=time.indexOf(":");
		if(time.contains("PM")&&(!(time.contains("12:")))){
			out+=720;
			System.out.print(" PM ");
		}else
		if(time.contains("AM")&&time.contains("12:")){
			System.out.print(" AM ");
			out-=720;
		}
		int hours=Integer.parseInt(time.substring(0, idx));
		int minutes=Integer.parseInt(time.substring(idx+1,idx+3));
		out=(60*hours)+minutes+out;
		System.out.println(out);
		return out;
	}
}
