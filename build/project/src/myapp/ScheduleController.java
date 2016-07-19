package myapp;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentImpl;
import jfxtras.scene.control.agenda.Agenda.AppointmentGroupImpl;

public class ScheduleController implements Initializable{

	@FXML Agenda agenda;

	ArrayList<AppointmentImpl> appts = new ArrayList<AppointmentImpl>();

	public void initialize(URL location, ResourceBundle resources) {
		agenda=new Agenda();
		System.out.println(agenda.isVisible());
	       LocalDate lTodayLocalDate = LocalDate.now();
	       Agenda.Appointment[] lTestAppointments = new Agenda.Appointment[]{
	           new Agenda.AppointmentImplLocal()
	               .withStartLocalDateTime(lTodayLocalDate.atTime(4, 00))
	               .withEndLocalDateTime(lTodayLocalDate.atTime(5, 30))
	               .withSummary("A")
	               .withDescription("A much longer test description")
	               .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"))
	       };
	       agenda.appointments().addAll(lTestAppointments);
	}

	public void refresh(){
		/*System.out.println("Refreshing schedule...");
		appts.clear();
		agenda.appointments().clear();
		ArrayList<GregorianCalendar> startlist = new ArrayList<GregorianCalendar>();
		ArrayList<GregorianCalendar> endlist = new ArrayList<GregorianCalendar>();
		ArrayList<String> namelist = new ArrayList<String>();
		ArrayList<String> descriptionlist = new ArrayList<String>();
		for(Section s:MyClasses.get().sections){
			//handle starting time
			GregorianCalendar c=new GregorianCalendar();
			int hour = MyClasses.get().parseTime(s.getBegin())/60;
			int minute = MyClasses.get().parseTime(s.getBegin())%60;
			c.set(2016, 6, 11, hour, minute);
			startlist.add(c);
			namelist.add(s.getCourse());
			descriptionlist.add(s.getTitle());

			//handle ending time
			c=new GregorianCalendar();
			hour = MyClasses.get().parseTime(s.getEnd())/60;
			minute = MyClasses.get().parseTime(s.getEnd())%60;
			c.set(2016, 6, 11, hour, minute);
			endlist.add(c);

			if(s.extratimes==true){
				//handle starting time
				c=new GregorianCalendar();
				hour = MyClasses.get().parseTime(s.getExBegin())/60;
				minute = MyClasses.get().parseTime(s.getExBegin())%60;
				c.set(2016, 7, 11, hour, minute);
				startlist.add(c);

				//handle ending time
				c=new GregorianCalendar();
				hour = MyClasses.get().parseTime(s.getExEnd())/60;
				minute = MyClasses.get().parseTime(s.getExEnd())%60;
				c.set(2016, 7, 11, hour, minute);
				endlist.add(c);
				namelist.add(s.getCourse());
				descriptionlist.add(s.getTitle());
			}

		}

		//create appointments from extracted times and names

		for(int i=0; i<namelist.size(); i++){
			AppointmentImpl appt = new AppointmentImpl()
					.withSummary(namelist.get(i))
					.withDescription(descriptionlist.get(i))
					.withStartTime(startlist.get(i))
					.withEndTime(endlist.get(i))
					.withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group1"));

			agenda.appointments().add(appt);
		}

		agenda.appointments().addAll(appts);

		for(Appointment apt:agenda.appointments()){
			System.out.println(apt);
		}
		*/
	}

}
