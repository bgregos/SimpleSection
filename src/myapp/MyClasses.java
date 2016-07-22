package myapp;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This singleton facilitates communication between the Section List tab and the My Classes tab.
 * It contains the list of sections the user has saved and current login status.
 * It also has methods to check for conflicts between different user-selected sections.
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
	public WebDriver driver;


	public void checkConflicts(){ //TODO: Add checking for additional times
		//clear all conflicts
		for(Section s:sections){
			s.setConflicts("");
		}

		for(int i=0; i<sections.size(); i++){
			for(int j=0; j<sections.size(); j++){

				int buffer=7; //Half of time between classes. VT is 15 min. Round down.
				//To make each class "wider"

				String icrn=sections.get(i).getCrn().substring(0, 5);
				String jcrn=sections.get(j).getCrn().substring(0, 5);
				String iCurrentConf = sections.get(i).getConflicts();
				String jCurrentConf = sections.get(j).getConflicts();


				//if(i!=j && (istart<jend && iend>jstart) && checkDays(idays,jdays)){ //check if conflict occurred
				if(i!=j && check(i,j)){ //check if conflict occurred
					if(!(iCurrentConf.contains(jcrn) && jCurrentConf.contains(icrn))){ //make sure conflict is not already logged between the two
						sections.get(i).setConflicts(iCurrentConf+jcrn+" ");
						sections.get(j).setConflicts(jCurrentConf+icrn+" ");
					}
				}

			}
		}
		crnsToConflictNumbers();
	}

	/**
	 * Checks for conlicts
	 */
	private boolean check(int i, int j){
		int buffer=7; //Half of time between classes. VT is 15 min. Round down.

		int istart=parseTime(sections.get(i).getBegin())-buffer;
		int iend=parseTime(sections.get(i).getEnd())+buffer;
		int jstart=parseTime(sections.get(j).getBegin())-buffer;
		int jend=parseTime(sections.get(j).getEnd())+buffer;
		String idays=sections.get(i).getDays();
		String jdays=sections.get(j).getDays();

		boolean iextratimes=false;
		int iexstart=0;
		int iexend=0;
		if(sections.get(i).getExtratimes()){
			iextratimes=true;
			iexstart=parseTime(sections.get(i).getExBegin())-buffer;
			iexend=parseTime(sections.get(i).getExEnd())+buffer;
		}

		boolean jextratimes=false;
		int jexstart=0;
		int jexend=0;
		if(sections.get(j).getExtratimes()){
			jextratimes=true;
			jexstart=parseTime(sections.get(j).getExBegin())-buffer;
			jexend=parseTime(sections.get(j).getExEnd())+buffer;
		}

		String iexdays=sections.get(i).getExDays();
		String jexdays=sections.get(j).getExDays();

		if((istart<jend && iend>jstart) && checkDays(idays,jdays)){  //no extra times, keeps things simple
			return true;
		}
		if(iextratimes || jextratimes){ //are extra times are involved?
			System.out.println("Extra times detected for purposes of course conflict detection");
			if((jextratimes && istart<jexend && iend>jexstart) && checkDays(idays,jexdays)){  //check j's extra time against i's regular
				return true;
			}
			if((iextratimes && iexstart<jend && iexend>jstart) && checkDays(iexdays,jdays)){  //check i's extra time against j's regular
				return true;
			}
			if(((iextratimes && jextratimes) && iexstart<jexend && iexend>jexstart) && checkDays(iexdays,jexdays)){  //check each extra time against the other
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts CRN conflict notifications into A, B, C, etc.
	 */
	private void crnsToConflictNumbers(){
		System.out.println("Converting CRNs to conf numbers...");

		int confNumber=65; //start with A
		for(int i=0; i<sections.size(); i++){
			for(int j=0; j<sections.size(); j++){
				String icrn=sections.get(i).getCrn().substring(0,5);
				String jcrn=sections.get(j).getCrn().substring(0,5);
				String iconf=sections.get(i).getConflicts();
				String jconf=sections.get(j).getConflicts();
				//System.out.println(iconf);
				//System.out.println(jcrn);

				if(iconf.contains(jcrn)){

					//System.out.println(icrn+" "+jcrn);
					//System.out.println(iconf+" "+jconf);
					//System.out.println("xx"+sections.get(i).getCrn()+"xx");

					iconf=iconf.replaceAll(jcrn, ""+(char)confNumber);
					jconf=jconf.replaceAll(icrn, ""+(char)confNumber);

					//System.out.println("hello there".replace("there", "a"));
					//System.out.println(iconf+" "+jconf);

					sections.get(i).setConflicts(iconf);
					sections.get(j).setConflicts(jconf);
					confNumber++;
				}
			}
		}
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
		//System.out.println(a+" "+b);

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
	public int parseTime(String time){
		int out=0;
		int idx=time.indexOf(":");
		if(time.contains("PM")&&(!(time.contains("12:")))){
			out+=720;
			//System.out.print(" PM ");
		}else
		if(time.contains("AM")&&time.contains("12:")){
			//System.out.print(" AM ");
			out-=720;
		}
		int hours=Integer.parseInt(time.substring(0, idx));
		int minutes=Integer.parseInt(time.substring(idx+1,idx+3));
		out=(60*hours)+minutes+out;
		//System.out.println(out);
		return out;
	}
}
