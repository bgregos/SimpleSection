package myapp;

import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

/**Gets course sections from Virginia Tech's website.
 * Requires usage of a WebDriver instance from Selenium.
 * @author bgregos
 *
 */
public class SectionGetter {
	private ArrayList<Section> list;
	Section s = new Section();
	private WebDriver driver;
	WebElement element;

	public SectionGetter(){
		this(new JBrowserDriver());
	}

	/**
	 * Constructor that allows passing in a WebDriver that you can preconfigure settings for
	 * (an example of this is logging in to HokieSpa before using this class)
	 * @param WebDriver driver
	 */
	public SectionGetter (WebDriver d){
		list = new ArrayList<Section>();
		this.driver=d;
	}

	private void setTerm(){
		String termval = null;
		String name=SettingsStore.get().selectedTerm;
		int year=Integer.parseInt(name.substring(name.length()-4, name.length()));
		String s = name.substring(0,name.length()-5);
		if(s.equals("Fall")){
			termval="09";
		}
		if(s.equals("Winter")){
			year--; termval="12";
		}
		if(s.equals("Spring")){
			termval="01"; //unsure
		}
		if(s.equals("Summer I")){
			termval="05"; //unsure
		}
		if(s.equals("Summer II")){
			termval="07";
		}

		System.out.println(termval +""+ year);

		try{
			/*Select dropdown = new Select(driver.findElement(By.name("TERMYEAR")));
			System.out.println(SettingsStore.get().selectedTerm);
			dropdown.selectByValue(termval+"");
			*/
			driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest?term="+termval+"&year="+year);
			System.out.println("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest?term="+termval+"&year="+year);
			//dropdown.selectByVisibleText(SettingsStore.get().selectedTerm);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("3");
	}

	/**
	 * Gets an ArrayList of sections from the VT servers.
	 * This version requires passing in a WebDriver instance (from the Selenium library), so as to cut down on load times associated with making a new WebDriver.
	 * Another version of this function omits the webdriver requirement, and handles it internally.
	 * **Warning: This method takes a long time to run. If using a GUI, running this outside the GUI thread is highly recommended to prevent hanging.
	 *
	 * @param WebDriver driver
	 * @param String department
	 * @param int coursenumber
	 * @param boolean loggedin
	 * @return ArrayList<Section>
	 */
	public ArrayList<Section> getSections(String department, int coursenumber, boolean loggedin){
		System.out.println("Getting Sections");
		driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest"); //go to course list site
		setTerm();
		Select dropdown = new Select(driver.findElement(By.name("subj_code")));
		try{ //handles department selection failure
			dropdown.selectByValue(department);
			element = driver.findElement(By.name("CRSE_NUMBER"));
			element.sendKeys(Integer.toString(coursenumber));
			element.submit();
			parseSections(loggedin);

		}
		catch(NoSuchElementException e){ //on bad section, return blank
			list=new ArrayList<Section>();
		}
			return list;

	}

	/**
	 * Gets an ArrayList of sections from the VT servers.
	 * This version does not require passing in a Selenium WebDriver instance, one is created for you.
	 * If you wish to pass in a WebDriver to reduce load times/mantain cached data, another version of this method is available.
	 * **Warning: This method takes a long time to run. If using a GUI, running this outside the GUI thread is highly recommended to prevent hanging.
	 *
	 * @param String department
	 * @param int coursenumber
	 * @param boolean loggedin
	 * @return ArrayList<Section>
	 */


	/**
	 * Get a single section by crn
	 *
	 * @param String crn
	 * @return Section
	 */
	public Section getSection(String crn, boolean loggedin){
		//WebDriver driver1=new JBrowserDriver(Settings.builder().headless(false).cache(true).build());
		//driver=driver1;
		driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest"); //go to course list site
		setTerm();
		element = driver.findElement(By.name("crn"));
		element.sendKeys(crn);
		element.submit();
		parseSections(loggedin);
		try{
			return list.get(0);
		}catch(Exception e){
			return null;
		}
	}

	public ArrayList<String> getTerms(){
		System.out.println("Getting terms from HokieSPA");
		ArrayList<String> out = new ArrayList<String>();
		driver.get("https://banweb.banner.vt.edu/ssb/prod/HZSKVTSC.P_DispRequest");
		System.out.println("1");
		int i=1;
		element = driver.findElement(By.xpath("//*[@id=\"ttform\"]/table/tbody/tr[2]/td[2]/select/option["+i+"]"));
		System.out.println("2");
		while(element!=null){
			try{
				element = driver.findElement(By.xpath("//*[@id=\"ttform\"]/table/tbody/tr[2]/td[2]/select/option["+i+"]"));
				i++;
				out.add(element.getText());
				System.out.println("New Term: "+element.getText());
			}catch(NoSuchElementException e){
				element=null;
			}

		}
		out.remove("Select Term");
		return out;
	}

	private void parseSections(boolean loggedin){
		boolean caughtEndOfSections=false;
		int i=2;
		list.clear();
		Section oldsection = new Section();
		System.out.println("Logged in: "+loggedin);
		while(caughtEndOfSections==false){ //parse sections line-by-line until out of sections
			try{
				//Loads first cell of row
				element = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[1]"));
				String text=element.getText();
				if(text.contains("Comments for")){ //checks for comment line and adds to Section if present
					oldsection.description = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[2]")).getText();
				}
				else{
					//loads fifth cell of row (where additional times are located on)
					element = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[5]"));
					text=element.getText();
					if(text.contains("* Additional Times *")){ //checks for and adds additional times if present
						oldsection.extratimes=true;
						oldsection.exDays = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[6]")).getText();
						oldsection.exBegin = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[7]")).getText();
						oldsection.exEnd = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[8]")).getText();
						oldsection.exLocation = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[9]")).getText();
					}
					else{
						//this is a normal row, not a comment or extra time row. Parse info into a new Section.
						s = new Section();
						s.crn = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[1]")).getText();
						s.course = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[2]")).getText();
						s.title = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[3]")).getText();
						s.type = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[4]")).getText();
						s.hours = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[5]")).getText();
						if(loggedin){
							s.available = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[6]")).getText();
							s.capacity = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[7]")).getText();
							s.instructor = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[8]")).getText();
							s.days = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[9]")).getText();
							s.begin = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[10]")).getText();
							s.end = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[11]")).getText();
							s.location = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[12]")).getText();
							//s.exam = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[13]")).getText();
						}
						else{
							s.capacity = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[6]")).getText();
							s.instructor = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[7]")).getText();
							s.days = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[8]")).getText();
							s.begin = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[9]")).getText();
							s.end = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[10]")).getText();
							s.location = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[11]")).getText();
							//s.exam = driver.findElement(By.xpath("//table[@class=\"dataentrytable\"]/tbody/tr["+i+"]/td[12]")).getText();
						}
						list.add(s);
						System.out.println(s.toString());
						//System.out.println(s.available);
						oldsection=s;
					}
				}
			}
			catch(NoSuchElementException e){ //this runs when there are no more sections to parse
				caughtEndOfSections=true;
				System.out.println("Lines read: "+i);
			}
			i++;
		}
	}

}
