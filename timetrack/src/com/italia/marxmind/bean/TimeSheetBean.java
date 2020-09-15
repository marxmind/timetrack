package com.italia.marxmind.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.italia.marxmind.controller.Employee;
import com.italia.marxmind.controller.EmployeeTimeOff;
import com.italia.marxmind.controller.Timesheet;
import com.italia.marxmind.enm.Status;
import com.italia.marxmind.enm.TimeMode;
import com.italia.marxmind.utils.DateUtils;
import com.italia.marxmind.utils.Numbers;
import com.italia.marxmind.utils.TimeUtils;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/09/2020
 *
 */
@Named
@ViewScoped
public class TimeSheetBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 156586876643353L;

	private String searchName;
	private List<Timesheet> sheets;
	private String displayName;
	private Employee employeeSelected;
	private String keyPress="searchId";
	
	@PostConstruct
	public void init() {
		
	}
	
	public List<String> autoSearchName(String query){
		int size = query.length();
		if(size>=2){
			List<String> names = Employee.retrieve(query, "fullName"," limit 5");
			
			if(names!=null && names.size()==1) {
				setSearchName(names.get(0));
				loadEmployee();
			}
			
			return names;
		}else{
			return new ArrayList<String>();
		}
	}
	
	public void loadEmployee() {
		PrimeFaces pf = PrimeFaces.current();
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
		List<Employee> emps = Employee.retrieve(" isactiveemp=1 AND fullName like '%"+getSearchName().replace("--", "")+"%' OR employeID='"+ getSearchName().replace("--", "") +"'", new String[0]);
		
		if(emps!=null && emps.size()>0) {
			Employee e = emps.get(0);
			setEmployeeSelected(e);
			String[] params = new String[0];
			String sql = " AND sh.isactivetime=1 AND sh.payid=0 AND emp.empid=" + e.getId() + " AND sh.timedate='" + DateUtils.getCurrentDateYYYYMMDD() +"'";
			
			List<Timesheet> times = Timesheet.retrieve(sql, params);
			
			sheets = new ArrayList<Timesheet>();
			
			if(times!=null && times.size()>0) {
				
				Map<Integer, Timesheet> tmap = new HashMap<Integer, Timesheet>();
				tmap.put(TimeMode.STRAIGHT_TIME.getId(), timeDetails(e, TimeMode.STRAIGHT_TIME, true, new Timesheet()));
				tmap.put(TimeMode.MORNING.getId(), timeDetails(e, TimeMode.MORNING, true, new Timesheet()));
				tmap.put(TimeMode.AFTERNOON.getId(), timeDetails(e, TimeMode.AFTERNOON, true, new Timesheet()));
				tmap.put(TimeMode.LUNCH_BREAK.getId(), timeDetails(e, TimeMode.LUNCH_BREAK, true, new Timesheet()));
				tmap.put(TimeMode.BREAK_TIME.getId(), timeDetails(e, TimeMode.BREAK_TIME, true, new Timesheet()));
				tmap.put(TimeMode.OVERTIME.getId(), timeDetails(e, TimeMode.OVERTIME, true, new Timesheet()));
				tmap.put(TimeMode.OPEN_TIME.getId(), timeDetails(e, TimeMode.OPEN_TIME, true, new Timesheet()));
				
				boolean detectStraightTime = false;
				boolean overtimeFound = false;
				boolean opentimeFound = false;
				boolean opentimeAgain = true;
				for(Timesheet t : times) {
					tmap.remove(t.getTimeMode());
					sheets.add(timeDetails(e, TimeMode.typeModeName(t.getTimeMode()), false, t));
					if(TimeMode.STRAIGHT_TIME.getId()==t.getTimeMode()) {
						detectStraightTime = true;
					}
					if(TimeMode.OVERTIME.getId()==t.getTimeMode()) {
						overtimeFound = true;
					}
					if(TimeMode.OPEN_TIME.getId()==t.getTimeMode()) {
						opentimeFound = true;
						System.out.println("open time >> " + t.getTimeOut());
						if("OUT".equalsIgnoreCase(t.getTimeOut())) {
							opentimeAgain = false;
						}
					}
				}
				
				//remove morning and afternoon selection
				if(detectStraightTime) {
					tmap.remove(TimeMode.MORNING.getId());
					tmap.remove(TimeMode.AFTERNOON.getId());
				}
				
				if(!detectStraightTime) {
					tmap.remove(TimeMode.STRAIGHT_TIME.getId());
				}	
				
				//add if need for overtime
				if(overtimeFound) {
					tmap = new HashMap<Integer, Timesheet>();//clear other selection
				}else {
					if(opentimeFound) {
						tmap = new HashMap<Integer, Timesheet>();//clear other selection
						if(opentimeAgain) {
							tmap.put(TimeMode.OPEN_TIME.getId(), timeDetails(e, TimeMode.OPEN_TIME, true, new Timesheet()));
						}
					}else {
						if(TimeUtils.isForOvertime(EmployeeTimeOff.retrieve(e.getId()))) {
							tmap = new HashMap<Integer, Timesheet>();//clear other selection
							tmap.put(TimeMode.OVERTIME.getId(), timeDetails(e, TimeMode.OVERTIME, true, new Timesheet()));
						}else {
							tmap.remove(TimeMode.OVERTIME.getId());
						}
					}
				}
				
				
				//load the remaining not yet recorded mode
				for(Timesheet t : tmap.values()) {
					sheets.add(timeDetails(e, TimeMode.typeModeName(t.getTimeMode()), true, t));
				}
				
				
			}else {
				
				if(TimeUtils.isForOvertime(EmployeeTimeOff.retrieve(e.getId()))) {
					
					sheets.add(timeDetails(e, TimeMode.OPEN_TIME, true, new Timesheet()));
					
				}else {
				
					sheets.add(timeDetails(e, TimeMode.STRAIGHT_TIME, true, new Timesheet()));
					sheets.add(timeDetails(e, TimeMode.MORNING, true, new Timesheet()));
					sheets.add(timeDetails(e, TimeMode.AFTERNOON, true, new Timesheet()));
					sheets.add(timeDetails(e, TimeMode.LUNCH_BREAK, true, new Timesheet()));
					sheets.add(timeDetails(e, TimeMode.BREAK_TIME, true, new Timesheet()));
				
				}
			}
			
			pf.executeScript("$('#tableDtlsId').fadeIn();");
			setDisplayName(e.getFullName());
		}else {
			sheets = new ArrayList<Timesheet>();
			setDisplayName("");
			pf.executeScript("$('#tableDtlsId').fadeOut();");
		}
		}else {
			sheets = new ArrayList<Timesheet>();
			setDisplayName("");
			pf.executeScript("$('#tableDtlsId').fadeOut();");
		}
	}
	
	//inout 1=in 2=out
	//isNew = true if not yet save in database
	public Timesheet timeDetails(Employee e, TimeMode timeMode, boolean isNew, Timesheet time) {
				
				time.setTimeDate(DateUtils.getCurrentDateYYYYMMDD());
				time.setEmployee(e);
				time.setTimeModeName(timeMode.getName());
				time.setTimeMode(timeMode.getId());
				time.setIsActive(1);
				time.setTimeStatus(Status.ACTIVE.getId());
				
				if(isNew) {
					//time.setTimeIn(TimeUtils.getTime12Format());
					time.setTimeIn("IN");
					time.setDisplayButtonIn(true);
					time.setDisplayButtonOut(false);
				}else {
					if(time.getTimeOut()!=null && !time.getTimeOut().isEmpty()) {
						time.setDisplayButtonOut(false);
					}else {
						//time.setTimeOut(TimeUtils.getTime12Format());
						time.setTimeOut("OUT");
						time.setDisplayButtonOut(true);
					}
					time.setDisplayButtonIn(false);
				}
				
				double total = 0;//TimeUtils.calculateTime(time.getTimeIn(), time.getTimeOut());
				total = TimeUtils.calculateTime(EmployeeTimeOff.retrieve(e.getId()), timeMode.getId(), time.getTimeIn(), time.getTimeOut());
				time.setTotalTime(total);
				
		return time;
	}
	
	public void timeSet(Timesheet st, String type) {
		
		if("IN".equalsIgnoreCase(type)) {
			st.setTimeIn(TimeUtils.getTime12Format());
		}else {
			st.setTimeOut(TimeUtils.getTime12Format());
		}
		
		st.save();
		loadEmployee();
	}
	
	

	public String getSearchName() {
		return searchName;
	}


	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<Timesheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<Timesheet> sheets) {
		this.sheets = sheets;
	}

	public Employee getEmployeeSelected() {
		return employeeSelected;
	}

	public void setEmployeeSelected(Employee employeeSelected) {
		this.employeeSelected = employeeSelected;
	}

	public String getKeyPress() {
		return keyPress;
	}

	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}
	
}
