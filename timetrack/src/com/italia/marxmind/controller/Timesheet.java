package com.italia.marxmind.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.enm.TimeMode;
import com.italia.marxmind.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */

public class Timesheet {

	private long id;
	private String timeDate;
	private String timeIn;
	private String timeOut;
	private int timeMode;
	private int timeStatus;
	private int isActive;
	
	private Employee employee;
	private Payslip payslip;
	
	private String timeModeName;
	private boolean displayButtonIn;
	private boolean displayButtonOut;
	private double totalTime;
	private String employeeName;
	private String tmpDate;
	
	public static List<Timesheet> retrieve(String sqlAdd, String[] params){
		List<Timesheet> vrs = new ArrayList<Timesheet>();
		
		String tabSh = "sh";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM timesheet "+ tabSh +",employee "+ tabEmp +"  WHERE " + tabSh + ".isactivetime=1 AND " +
		tabSh + ".empid=" + tabEmp + ".empid ";
				
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		LogU.openSave("Timesheet SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Timesheet s = new Timesheet();
			s.setId(rs.getLong("timeid"));
			s.setTimeDate(rs.getString("timedate"));
			s.setTimeIn(rs.getString("timein"));
			s.setTimeOut(rs.getString("timeout"));
			s.setTimeMode(rs.getInt("modetime"));
			s.setTimeStatus(rs.getInt("timestatus"));
			s.setIsActive(rs.getInt("isactivetime"));
			
			Payslip p = new Payslip();
			p.setId(rs.getLong("payid"));
			s.setPayslip(p);
			
			Employee e = new Employee();
			e.setId(rs.getLong("empid"));
			e.setDateRegistered(rs.getString("dateRegistered"));
			e.setEmployeID(rs.getString("employeID"));
			e.setLabelName(rs.getString("labelName"));
			e.setFirstName(rs.getString("firstName"));
			e.setMiddleName(rs.getString("middleName"));
			e.setLastName(rs.getString("lastName"));
			e.setSuffixName(rs.getString("suffixName"));
			e.setFullName(rs.getString("fullName"));
			e.setGender(rs.getInt("gender"));
			e.setBirthDate(rs.getString("birthDate"));
			e.setPhotoid(rs.getString("photoid"));
			e.setIsActive(rs.getInt("isactiveemp"));
			s.setEmployee(e);
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static List<Timesheet> retrieveWithPaySlip(String sqlAdd, String[] params){
		List<Timesheet> vrs = new ArrayList<Timesheet>();
		
		String tabSh = "sh";
		String tabPay = "pay";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM timesheet "+ tabSh +" ,payslip "+ tabPay +",employee "+ tabEmp +"  WHERE " + tabSh + ".isactivetime=1 AND " +
		tabSh + ".payid=" + tabPay + ".payid AND " +
		tabSh + ".empid=" + tabEmp + ".empid ";
				
		sql = sql + sqlAdd;		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		LogU.openSave("Timesheet SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Timesheet s = new Timesheet();
			s.setId(rs.getLong("timeid"));
			s.setTimeDate(rs.getString("timedate"));
			s.setTimeIn(rs.getString("timein"));
			s.setTimeOut(rs.getString("timeout"));
			s.setTimeMode(rs.getInt("modetime"));
			s.setTimeStatus(rs.getInt("timestatus"));
			s.setIsActive(rs.getInt("isactivetime"));
			
			Payslip p = new Payslip();
			p.setId(rs.getLong("payid"));
			p.setPayDate(rs.getString("paydate"));
			p.setPayCoveredDate(rs.getString("paycovereddate"));
			p.setAddedAmount(rs.getDouble("addedamount"));
			p.setGrossAmount(rs.getDouble("grossamount"));
			p.setNetAmount(rs.getDouble("netamount"));
			p.setIsActive(rs.getInt("isactivepay"));
			s.setPayslip(p);
			
			Employee e = new Employee();
			e.setId(rs.getLong("empid"));
			e.setDateRegistered(rs.getString("dateRegistered"));
			e.setEmployeID(rs.getString("employeID"));
			e.setLabelName(rs.getString("labelName"));
			e.setFirstName(rs.getString("firstName"));
			e.setMiddleName(rs.getString("middleName"));
			e.setLastName(rs.getString("lastName"));
			e.setSuffixName(rs.getString("suffixName"));
			e.setFullName(rs.getString("fullName"));
			e.setGender(rs.getInt("gender"));
			e.setBirthDate(rs.getString("birthDate"));
			e.setPhotoid(rs.getString("photoid"));
			e.setIsActive(rs.getInt("isactiveemp"));
			s.setEmployee(e);
			
			vrs.add(s);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static Timesheet save(Timesheet vr){
		if(vr!=null){
			LogU.open();
			long id = Timesheet.getInfo(vr.getId() ==0? Timesheet.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = Timesheet.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = Timesheet.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = Timesheet.insertData(vr, "3");
			}
			
		}
		return vr;
	}
	
	
	public void save(){
			LogU.open();
			long id = getInfo(getId() ==0? getLatestId()+1 : getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				insertData("1");
			}else if(id==2){
				LogU.add("update Data ");
				updateData();
			}else if(id==3){
				LogU.add("added new Data ");
				insertData("3");
			}
			
	}
	
	public static Timesheet insertData(Timesheet vr, String type){
		String sql = "INSERT INTO timesheet ("
				+ "timeid,"
				+ "timedate,"
				+ "timein,"
				+ "timeout,"
				+ "modetime,"
				+ "timestatus,"
				+ "isactivetime,"
				+ "empid,"
				+ "payid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table timesheet");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			vr.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, vr.getTimeDate());
		ps.setString(cnt++, vr.getTimeIn());
		ps.setString(cnt++, vr.getTimeOut());
		ps.setInt(cnt++, vr.getTimeMode());
		ps.setInt(cnt++, vr.getTimeStatus());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		ps.setLong(cnt++, vr.getPayslip()==null? 0 : vr.getPayslip().getId());
		
		LogU.add(vr.getTimeDate());
		LogU.add(vr.getTimeIn());
		LogU.add(vr.getTimeOut());
		LogU.add(vr.getTimeMode());
		LogU.add(vr.getTimeStatus());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		LogU.add(vr.getPayslip()==null? 0 : vr.getPayslip().getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timesheet : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO timesheet ("
				+ "timeid,"
				+ "timedate,"
				+ "timein,"
				+ "timeout,"
				+ "modetime,"
				+ "timestatus,"
				+ "isactivetime,"
				+ "empid,"
				+ "payid)" 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table timesheet");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setString(cnt++, getTimeDate());
		ps.setString(cnt++, getTimeIn());
		ps.setString(cnt++, getTimeOut());
		ps.setInt(cnt++, getTimeMode());
		ps.setInt(cnt++, getTimeStatus());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setLong(cnt++, getPayslip()==null? 0 : getPayslip().getId());
		
		LogU.add(getTimeDate());
		LogU.add(getTimeIn());
		LogU.add(getTimeOut());
		LogU.add(getTimeMode());
		LogU.add(getTimeStatus());
		LogU.add(getIsActive());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getPayslip()==null? 0 : getPayslip().getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timesheet : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static Timesheet updateData(Timesheet vr){
		String sql = "UPDATE timesheet SET "
				+ "timedate=?,"
				+ "timein=?,"
				+ "timeout=?,"
				+ "modetime=?,"
				+ "timestatus=?,"
				+ "empid=?,"
				+ "payid=?" 
				+ " WHERE timeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table timesheet");
		
		ps.setString(cnt++, vr.getTimeDate());
		ps.setString(cnt++, vr.getTimeIn());
		ps.setString(cnt++, vr.getTimeOut());
		ps.setInt(cnt++, vr.getTimeMode());
		ps.setInt(cnt++, vr.getTimeStatus());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		ps.setLong(cnt++, vr.getPayslip()==null? 0 : vr.getPayslip().getId());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getTimeDate());
		LogU.add(vr.getTimeIn());
		LogU.add(vr.getTimeOut());
		LogU.add(vr.getTimeMode());
		LogU.add(vr.getTimeStatus());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		LogU.add(vr.getPayslip()==null? 0 : vr.getPayslip().getId());
		LogU.add(vr.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timesheet : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE timesheet SET "
				+ "timedate=?,"
				+ "timein=?,"
				+ "timeout=?,"
				+ "modetime=?,"
				+ "timestatus=?,"
				+ "empid=?,"
				+ "payid=?" 
				+ " WHERE timeid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table timesheet");
		
		ps.setString(cnt++, getTimeDate());
		ps.setString(cnt++, getTimeIn());
		ps.setString(cnt++, getTimeOut());
		ps.setInt(cnt++, getTimeMode());
		ps.setInt(cnt++, getTimeStatus());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setLong(cnt++, getPayslip()==null? 0 : getPayslip().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getTimeDate());
		LogU.add(getTimeIn());
		LogU.add(getTimeOut());
		LogU.add(getTimeMode());
		LogU.add(getTimeStatus());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getPayslip()==null? 0 : getPayslip().getId());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timesheet : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT timeid FROM timesheet  ORDER BY timeid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("timeid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT timeid FROM timesheet WHERE timeid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE timesheet set isactivetime=0 WHERE timeid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTimeDate() {
		return timeDate;
	}
	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
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
	public int getTimeMode() {
		return timeMode;
	}
	public void setTimeMode(int timeMode) {
		this.timeMode = timeMode;
	}
	public int getTimeStatus() {
		return timeStatus;
	}
	public void setTimeStatus(int timeStatus) {
		this.timeStatus = timeStatus;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Payslip getPayslip() {
		return payslip;
	}
	public void setPayslip(Payslip payslip) {
		this.payslip = payslip;
	}

	public String getTimeModeName() {
		return timeModeName;
	}

	public void setTimeModeName(String timeModeName) {
		this.timeModeName = timeModeName;
	}

	public boolean isDisplayButtonIn() {
		return displayButtonIn;
	}

	public void setDisplayButtonIn(boolean displayButtonIn) {
		this.displayButtonIn = displayButtonIn;
	}

	public boolean isDisplayButtonOut() {
		return displayButtonOut;
	}

	public void setDisplayButtonOut(boolean displayButtonOut) {
		this.displayButtonOut = displayButtonOut;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getTmpDate() {
		return tmpDate;
	}

	public void setTmpDate(String tmpDate) {
		this.tmpDate = tmpDate;
	}
	
}
