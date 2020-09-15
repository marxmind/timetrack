package com.italia.marxmind.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.italia.marxmind.application.Application;
import com.italia.marxmind.controller.Employee;
import com.italia.marxmind.controller.EmployeeTimeOff;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/12/2020
 *
 */
@Named
@ViewScoped
public class TimeOffBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 69684656996591L;

	private String morningTimeIn;
	private String morningTimeOut;
	private String afternoonIn;
	private String afternoonOut;
	private String searchName;
	private String employeeName;
	private Employee selectedEmployee;
	private int extentionTime;
	
	private EmployeeTimeOff selectedData;
	private List<EmployeeTimeOff> offs = new ArrayList<EmployeeTimeOff>();
	
	@PostConstruct
	public void init() {
		offs = new ArrayList<EmployeeTimeOff>();
		
		String[] params = new String[0];
		String sql = " ORDER BY em.fullName";
		
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql = " AND em.fullName like '%"+ getSearchName().replace("--", "") +"%'  ORDER BY em.fullName";
		}
		
		offs = EmployeeTimeOff.retrieve(sql, params);
		
	}
	
	public List<String> autoSelectName(String query){
		int size = query.length();
		if(size>=2){
			List<String> names = Employee.retrieve(query, "fullName"," limit 5");
			
			if(names!=null && names.size()==1) {
				setEmployeeName(names.get(0));
			}
			
			return names;
		}else{
			return new ArrayList<String>();
		}
	}
	
	public void loadEmployee() {
		List<Employee> emps = Employee.retrieve(" isactiveemp=1 AND fullName='"+getEmployeeName().replace("--", "")+"'", new String[0]);
		try{setSelectedEmployee(emps.get(0));}catch(Exception e) {}
	}
	
	public List<String> autoSearchName(String query){
		int size = query.length();
		if(size>=2){
			List<String> names = Employee.retrieve(query, "fullName"," limit 5");
			
			if(names!=null && names.size()==1) {
				setSearchName(names.get(0));
				init();
			}
			
			return names;
		}else{
			return new ArrayList<String>();
		}
	}
	
	public void clickItem(EmployeeTimeOff off) {
		setMorningTimeIn(off.getMorningTimeIn());
		setMorningTimeOut(off.getMorningTimeOut());
		setAfternoonIn(off.getAfternoonIn());
		setAfternoonOut(off.getAfternoonOut());
		setSelectedData(off);
		setSelectedEmployee(off.getEmployee());
		setEmployeeName(off.getEmployee().getFullName());
		setExtentionTime(off.getTimeExtension());
	}
	
	public void save() {
		EmployeeTimeOff off = new EmployeeTimeOff();
		if(getSelectedData()!=null) {
			off = getSelectedData();
		}else {
			off.setIsActive(1);
		}
		
		boolean isOk = true;
		if(getMorningTimeIn()==null || getMorningTimeIn().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide morning time in");
		}
		if(getMorningTimeOut()==null || getMorningTimeOut().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide morning time out");
		}
		if(getAfternoonIn()==null || getAfternoonIn().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide afternoon time in");
		}
		if(getAfternoonOut()==null || getAfternoonOut().isEmpty()) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide afternoon time out");
		}
		
		if(getSelectedEmployee()==null) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Employee name");
		}
		
		
		if(isOk) {
			off.setMorningTimeIn(getMorningTimeIn());
			off.setMorningTimeOut(getMorningTimeOut());
			off.setAfternoonIn(getAfternoonIn());
			off.setAfternoonOut(getAfternoonOut());
			off.setEmployee(getSelectedEmployee());
			off.setTimeExtension(getExtentionTime());
			off.save();
			init();
			clear();
			Application.addMessage(1, "Success", "Successfully saved.");
			PrimeFaces pf = PrimeFaces.current();
			pf.executeScript("$('#dlgAdd').hide(1000);");
		}
	}
	
	public void deleteRow(EmployeeTimeOff off) {
		off.delete();
		init();
		Application.addMessage(1, "Success", "Successfully deleted.");
	}
	
	public void clear() {
		setMorningTimeIn(null);
		setMorningTimeOut(null);
		setAfternoonIn(null);
		setAfternoonOut(null);
		setSelectedData(null);
		setSelectedEmployee(null);
		setEmployeeName(null);
		setExtentionTime(0);
	}
	
	public String getMorningTimeIn() {
		return morningTimeIn;
	}

	public void setMorningTimeIn(String morningTimeIn) {
		this.morningTimeIn = morningTimeIn;
	}

	public String getMorningTimeOut() {
		return morningTimeOut;
	}

	public void setMorningTimeOut(String morningTimeOut) {
		this.morningTimeOut = morningTimeOut;
	}

	public String getAfternoonIn() {
		return afternoonIn;
	}

	public void setAfternoonIn(String afternoonIn) {
		this.afternoonIn = afternoonIn;
	}

	public String getAfternoonOut() {
		return afternoonOut;
	}

	public void setAfternoonOut(String afternoonOut) {
		this.afternoonOut = afternoonOut;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public List<EmployeeTimeOff> getOffs() {
		return offs;
	}

	public void setOffs(List<EmployeeTimeOff> offs) {
		this.offs = offs;
	}

	public EmployeeTimeOff getSelectedData() {
		return selectedData;
	}

	public void setSelectedData(EmployeeTimeOff selectedData) {
		this.selectedData = selectedData;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public int getExtentionTime() {
		return extentionTime;
	}

	public void setExtentionTime(int extentionTime) {
		this.extentionTime = extentionTime;
	}
	
}
