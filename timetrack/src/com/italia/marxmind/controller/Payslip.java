package com.italia.marxmind.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.enm.Status;
import com.italia.marxmind.enm.TimeMode;
import com.italia.marxmind.utils.DateUtils;
import com.italia.marxmind.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */

public class Payslip {

	private long id;
	private String payDate;
	private String payCoveredDate;
	private double addedAmount;
	private double grossAmount;
	private double netAmount;
	private int isActive;
	
	private Employee employee;
	
	public static List<Payslip> retrieve(String sqlAdd, String[] params){
		List<Payslip> vrs = new ArrayList<Payslip>();
		
		String tabPay = "pay";
		String tabEmp = "emp";
		
		String sql = "SELECT * FROM payslip "+ tabPay +",employee "+ tabEmp +"  WHERE " + tabPay + ".isactivepay=1 AND " +
		tabPay + ".payid=" + tabEmp + ".payid ";
				
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
		
		LogU.openSave("Payslip SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Payslip p = new Payslip();
			p.setId(rs.getLong("payid"));
			p.setPayDate(rs.getString("paydate"));
			p.setPayCoveredDate(rs.getString("paycovereddate"));
			p.setAddedAmount(rs.getDouble("addedamount"));
			p.setGrossAmount(rs.getDouble("grossamount"));
			p.setNetAmount(rs.getDouble("netamount"));
			p.setIsActive(rs.getInt("isactivepay"));
			
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
			p.setEmployee(e);
			
			vrs.add(p);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static Payslip save(Payslip vr){
		if(vr!=null){
			LogU.open();
			long id = Payslip.getInfo(vr.getId() ==0? Payslip.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = Payslip.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = Payslip.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = Payslip.insertData(vr, "3");
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
	
	public static Payslip insertData(Payslip vr, String type){
		String sql = "INSERT INTO payslip ("
				+ "payid,"
				+ "paydate,"
				+ "paycovereddate,"
				+ "addedamount,"
				+ "grossamount,"
				+ "netamount,"
				+ "isactivepay,"
				+ "empid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table payslip");
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
		
		ps.setString(cnt++, vr.getPayDate());
		ps.setString(cnt++, vr.getPayCoveredDate());
		ps.setDouble(cnt++, vr.getAddedAmount());
		ps.setDouble(cnt++, vr.getGrossAmount());
		ps.setDouble(cnt++, vr.getNetAmount());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		
		LogU.add(vr.getPayDate());
		LogU.add(vr.getPayCoveredDate());
		LogU.add(vr.getAddedAmount());
		LogU.add(vr.getGrossAmount());
		LogU.add(vr.getNetAmount());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to payslip : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO payslip ("
				+ "payid,"
				+ "paydate,"
				+ "paycovereddate,"
				+ "addedamount,"
				+ "grossamount,"
				+ "netamount,"
				+ "isactivepay,"
				+ "empid)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table payslip");
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
		
		ps.setString(cnt++, getPayDate());
		ps.setString(cnt++, getPayCoveredDate());
		ps.setDouble(cnt++, getAddedAmount());
		ps.setDouble(cnt++, getGrossAmount());
		ps.setDouble(cnt++, getNetAmount());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		
		LogU.add(getPayDate());
		LogU.add(getPayCoveredDate());
		LogU.add(getAddedAmount());
		LogU.add(getGrossAmount());
		LogU.add(getNetAmount());
		LogU.add(getIsActive());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to payslip : " + s.getMessage());
		}
		LogU.close();
	
	}
	
	public static Payslip updateData(Payslip vr){
		String sql = "UPDATE payslip SET "
				+ "paydate=?,"
				+ "paycovereddate=?,"
				+ "addedamount=?,"
				+ "grossamount=?,"
				+ "netamount=?,"
				+ "empid=?" 
				+ " WHERE payid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table payslip");
		
		ps.setString(cnt++, vr.getPayDate());
		ps.setString(cnt++, vr.getPayCoveredDate());
		ps.setDouble(cnt++, vr.getAddedAmount());
		ps.setDouble(cnt++, vr.getGrossAmount());
		ps.setDouble(cnt++, vr.getNetAmount());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getPayDate());
		LogU.add(vr.getPayCoveredDate());
		LogU.add(vr.getAddedAmount());
		LogU.add(vr.getGrossAmount());
		LogU.add(vr.getNetAmount());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		LogU.add(vr.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to payslip : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE payslip SET "
				+ "paydate=?,"
				+ "paycovereddate=?,"
				+ "addedamount=?,"
				+ "grossamount=?,"
				+ "netamount=?,"
				+ "empid=?" 
				+ " WHERE payid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table payslip");
		
		ps.setString(cnt++, getPayDate());
		ps.setString(cnt++, getPayCoveredDate());
		ps.setDouble(cnt++, getAddedAmount());
		ps.setDouble(cnt++, getGrossAmount());
		ps.setDouble(cnt++, getNetAmount());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setLong(cnt++, getId());
		
		LogU.add(getPayDate());
		LogU.add(getPayCoveredDate());
		LogU.add(getAddedAmount());
		LogU.add(getGrossAmount());
		LogU.add(getNetAmount());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to payslip : " + s.getMessage());
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
		sql="SELECT payid FROM payslip  ORDER BY payid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("payid");
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
		ps = conn.prepareStatement("SELECT payid FROM payslip WHERE payid=?");
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
		String sql = "UPDATE payslip set isactivepay=0 WHERE payid=?";
		
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

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getPayCoveredDate() {
		return payCoveredDate;
	}

	public void setPayCoveredDate(String payCoveredDate) {
		this.payCoveredDate = payCoveredDate;
	}

	public double getAddedAmount() {
		return addedAmount;
	}

	public void setAddedAmount(double addedAmount) {
		this.addedAmount = addedAmount;
	}

	public double getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(double grossAmount) {
		this.grossAmount = grossAmount;
	}

	public double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
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
	
	public static void main(String[] args) {
		
		Employee e = new Employee();
		e.setId(1);
		
		Timesheet t = new Timesheet();
		t.setTimeDate(DateUtils.getCurrentDateYYYYMMDD());
		t.setTimeIn("06:00 AM");
		t.setTimeOut("11:00 AM");
		t.setTimeMode(TimeMode.MORNING.getId());
		t.setTimeStatus(Status.ACTIVE.getId());
		t.setIsActive(1);
		t.setEmployee(e);
		t = t.save(t);
		
		Payslip p = new Payslip();
		p.setPayDate(DateUtils.getCurrentDateYYYYMMDD());
		p.setPayCoveredDate(DateUtils.getCurrentDateYYYYMMDD());
		p.setAddedAmount(100);
		p.setGrossAmount(500);
		p.setNetAmount(500);
		p.setIsActive(1);
		
		
		p.setEmployee(e);
		p = p.save(p);
		t.setPayslip(p);
		t.save();
		
	}
	
}
