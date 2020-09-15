package com.italia.marxmind.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

import com.italia.marxmind.application.Application;
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
 * @since 02/12/2020
 *
 */
@Named
@ViewScoped
public class TimeSheetDtlsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5465767576443641L;

	
	private String searchName;
	private List<Timesheet> sheets = new ArrayList<Timesheet>();
	
	private List<Timesheet> selectedSheets = new ArrayList<Timesheet>();
	
	private Date dateTans;
	private List modeTimes;
	private int modeId;
	private String inputtedName;
	private Employee selectedEmployee;
	private String timeIn;
	private String timeOut;
	
	@PostConstruct
	public void init() {
		sheets = new ArrayList<Timesheet>();
		String[] params = new String[0];
		String sql = "";
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql = " AND sh.isactivetime=1 AND emp.fullName like '%"+ getSearchName().replace("--", "") +"%' AND sh.payid=0 AND sh.timeout is not NULL AND sh.timestatus="+Status.ACTIVE.getId();
		}else {
			sql = " AND sh.isactivetime=1 AND sh.payid=0 AND sh.timeout is not NULL AND sh.timestatus="+Status.ACTIVE.getId();
		}
		
		//Name - date - sheets
		List<Timesheet> times = new ArrayList<Timesheet>();
		Map<String, List<Timesheet>> timeMap = new HashMap<String, List<Timesheet>>();
		Map<Long, Map<String,List<Timesheet>>> maps = new HashMap<Long, Map<String,List<Timesheet>>>();
		Map<Long, Employee> empMap = new HashMap<Long, Employee>();
		for(Timesheet t : Timesheet.retrieve(sql, params)) {
			
			long key = t.getEmployee().getId();
			String dk = t.getTimeDate();
			empMap.put(key, t.getEmployee());
			
			if(maps!=null && maps.size()>0) {
				
				if(maps.containsKey(key)) {
					
					if(maps.get(key).containsKey(dk)) {
						maps.get(key).get(dk).add(t);
					}else {
						times = new ArrayList<Timesheet>();
						times.add(t);
						maps.get(key).put(dk, times);
					}
					
				}else {
					
					times = new ArrayList<Timesheet>();
					timeMap = new HashMap<String, List<Timesheet>>();
					
					times.add(t);
					timeMap.put(t.getTimeDate(), times);
					maps.put(key, timeMap);
				}
				
			}else {
				times.add(t);
				timeMap.put(t.getTimeDate(), times);
				maps.put(key, timeMap);
			}
			
			
		}
		
		Map<Long, Map<String,List<Timesheet>>> mapData = new TreeMap<Long, Map<String,List<Timesheet>>>(maps);
		
		for(Long key : mapData.keySet()) {
			int detCnt = 1;
			double grandTotal = 0d;
			for(String dk : mapData.get(key).keySet()) {
				int inc = 1;
				for(Timesheet t : mapData.get(key).get(dk)) {
					t.setTmpDate(dk);
					if(detCnt==1 && inc==1) {
						t.setEmployeeName(t.getEmployee().getFullName());
					}
					
					if(inc>1) {
						t.setTmpDate("");
					}
					
					t.setTimeModeName(TimeMode.typeName(t.getTimeMode()));
					double total = 0;//TimeUtils.calculateTime(t.getTimeIn(), t.getTimeOut());
					total = TimeUtils.calculateTime(EmployeeTimeOff.retrieve(t.getEmployee().getId()), t.getTimeMode(), t.getTimeIn(), t.getTimeOut());
					t.setTotalTime(total);
					
					sheets.add(t);
					
					if(TimeMode.STRAIGHT_TIME.getId()==t.getTimeMode() || 
							TimeMode.MORNING.getId()==t.getTimeMode() || 
								TimeMode.AFTERNOON.getId()==t.getTimeMode() ||
									TimeMode.OPEN_TIME.getId()==t.getTimeMode() ||
										TimeMode.OVERTIME.getId()==t.getTimeMode()) {
						grandTotal += total;
					}
					
					inc++;
				}
				detCnt++;
			}
			
			Timesheet tm = new Timesheet();
			tm.setEmployeeName("Total");
			tm.setTotalTime(Numbers.formatDouble(grandTotal));
			sheets.add(tm);
		}
		
		
		
	}
	
	public void approvedTime() {
		if(getSelectedSheets()!=null && getSelectedSheets().size()>0 && getSelectedSheets().get(0).getId()>0) {
			String name = getSelectedSheets().get(0).getEmployee().getFullName();
			int cnt = 0;
			for(Timesheet t : getSelectedSheets()) {
					t.setTimeStatus(Status.APPROVED.getId());
					t.save();
					cnt++;
			}
			init();
			clear();
			Application.addMessage(1, "Success", name + " total of " + cnt + " timesheets has been approved.");
		}else {
			Application.addMessage(3, "Error", "Please check time before approving.");
		}
	}
	
	public void deleteTime() {
		if(getSelectedSheets()!=null && getSelectedSheets().size()>0 && getSelectedSheets().get(0).getId()>0) {
			String name = getSelectedSheets().get(0).getEmployee().getFullName();
			int cnt = 0;
			for(Timesheet t : getSelectedSheets()) {
					t.delete();
					cnt++;
			}
			init();
			clear();
			Application.addMessage(1, "Success", name + " total of " + cnt + " timesheets has been deleted.");
		}else {
			Application.addMessage(3, "Error", "Please check time before deleting.");
		}
	}
	
	public void clear() {
		setSelectedSheets(null);
		setDateTans(null);
		setInputtedName(null);
		setSelectedEmployee(null);
		setModeId(0);
		setTimeIn(null);
		setTimeOut(null);
	}
	
	
	public void onCellEdit(CellEditEvent event) {
		 try{
	        Object oldValue = event.getOldValue();
	        Object newValue = event.getNewValue();
	        
	        System.out.println("old Value: " + oldValue);
	        System.out.println("new Value: " + newValue);
	        
	        int index = event.getRowIndex();
	        Timesheet t = getSheets().get(index);
	        t.save();
	        init();
	        //double total = TimeUtils.calculateTime(t.getTimeIn(), t.getTimeOut());
	        //getSheets().get(index).setTotalTime(total);
	        
	        
		 }catch(Exception e) {}
	}	 
	
	public void saveTime() {
		if(getSelectedEmployee()!=null) {
			System.out.println("time in " + getTimeIn() + " time out " + getTimeOut());
			Timesheet t = new Timesheet();
			t.setIsActive(1);
			t.setEmployee(getSelectedEmployee());
			t.setTimeDate(DateUtils.convertDate(getDateTans(), "yyyy-MM-dd"));
			t.setTimeIn(getTimeIn());
			t.setTimeOut(getTimeOut());
			t.setTimeStatus(Status.ACTIVE.getId());
			t.setTimeMode(getModeId());
			t.save();
			init();
			clear();
			Application.addMessage(1, "Success", "Successfully saved.");
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("$('#dlgAdd').hide(1000)");
		}
	}
	
	public List<String> autoSearchName(String query){
		int size = query.length();
		if(size>=2){
			List<String> names = Employee.retrieve(query, "fullName"," limit 5");
			
			if(names!=null && names.size()==1) {
				setInputtedName(names.get(0));
			}
			
			return names;
		}else{
			return new ArrayList<String>();
		}
	}
	
	public void loadEmployee() {
		if(getInputtedName()!=null && !getInputtedName().isEmpty()) {
			try{Employee e = Employee.retrieve(" isactiveemp=1 AND fullName='"+getInputtedName()+"'", new String[0]).get(0); setSelectedEmployee(e);}catch(Exception e) {}
		}
	}
	
	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<Timesheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<Timesheet> sheets) {
		this.sheets = sheets;
	}

	public List<Timesheet> getSelectedSheets() {
		return selectedSheets;
	}

	public void setSelectedSheets(List<Timesheet> selectedSheets) {
		this.selectedSheets = selectedSheets;
	}

	public Date getDateTans() {
		if(dateTans==null) {
			dateTans = DateUtils.getDateToday();
		}
		return dateTans;
	}

	public void setDateTans(Date dateTans) {
		this.dateTans = dateTans;
	}

	public List getModeTimes() {
		modeTimes = new ArrayList<>();
		for(TimeMode t : TimeMode.values()) {
			modeTimes.add(new SelectItem(t.getId(), t.getName()));
		}
		return modeTimes;
	}

	public void setModeTimes(List modeTimes) {
		this.modeTimes = modeTimes;
	}

	public int getModeId() {
		if(modeId==0) {
			modeId = 1;
		}
		return modeId;
	}

	public void setModeId(int modeId) {
		this.modeId = modeId;
	}

	public String getInputtedName() {
		return inputtedName;
	}

	public void setInputtedName(String inputtedName) {
		this.inputtedName = inputtedName;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public String getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	
	
}
