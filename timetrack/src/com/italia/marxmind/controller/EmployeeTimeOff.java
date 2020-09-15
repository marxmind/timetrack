package com.italia.marxmind.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */
public class EmployeeTimeOff {

	private int id;
	private String morningTimeIn;
	private String morningTimeOut;
	private String afternoonIn;
	private String afternoonOut;
	private int isActive;
	private int timeExtension;
	
	private Employee employee;
	
	public static List<EmployeeTimeOff> retrieve(String sqlAdd, String[] params){
		List<EmployeeTimeOff> vrs = new ArrayList<EmployeeTimeOff>();
		
		String tabOff = "of";
		String tabEmp = "em";
		
		String sql = "SELECT * FROM  employeetimeoff "+ tabOff +", employee "+ tabEmp +" WHERE " + tabOff + ".isactiveoff=1 AND " +
		tabOff + ".empid=" + tabEmp + ".empid ";
				
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
		
		LogU.openSave("Employee SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			EmployeeTimeOff off = new EmployeeTimeOff();
			off.setId(rs.getInt("offid"));
			off.setMorningTimeIn(rs.getString("morningtimein"));
			off.setMorningTimeOut(rs.getString("morningtimeout"));
			off.setAfternoonIn(rs.getString("afternoontimein"));
			off.setAfternoonOut(rs.getString("afternoontimeout"));
			off.setIsActive(rs.getInt("isactiveoff"));
			off.setTimeExtension(rs.getInt("timeextension"));
			
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
			off.setEmployee(e);
			
			vrs.add(off);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	/**
	 * 
	 * @param if no employee recorded return the default configuration
	 * @return
	 */
	public static EmployeeTimeOff retrieve(long id){
		EmployeeTimeOff off = new EmployeeTimeOff();
		String sql = "SELECT * FROM  employeetimeoff  WHERE empid="+id +" OR empid=0";		
				
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.openSave("Employee SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			off.setId(rs.getInt("offid"));
			off.setMorningTimeIn(rs.getString("morningtimein"));
			off.setMorningTimeOut(rs.getString("morningtimeout"));
			off.setAfternoonIn(rs.getString("afternoontimein"));
			off.setAfternoonOut(rs.getString("afternoontimeout"));
			off.setIsActive(rs.getInt("isactiveoff"));
			off.setTimeExtension(rs.getInt("timeextension"));
			
			Employee e = new Employee();
			e.setId(rs.getLong("empid"));
			off.setEmployee(e);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		
		return off;
	}
	
	public static EmployeeTimeOff save(EmployeeTimeOff vr){
		if(vr!=null){
			LogU.open();
			long id = EmployeeTimeOff.getInfo(vr.getId() ==0? EmployeeTimeOff.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = EmployeeTimeOff.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = EmployeeTimeOff.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = EmployeeTimeOff.insertData(vr, "3");
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
	
	public static EmployeeTimeOff insertData(EmployeeTimeOff vr, String type){
		String sql = "INSERT INTO employeetimeoff ("
				+ "offid,"
				+ "morningtimein,"
				+ "morningtimeout,"
				+ "afternoontimein,"
				+ "afternoontimeout,"
				+ "isactiveoff,"
				+ "empid,"
				+ "timeextension)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employeetimeoff");
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
		
		ps.setString(cnt++, vr.getMorningTimeIn());
		ps.setString(cnt++, vr.getMorningTimeOut());
		ps.setString(cnt++, vr.getAfternoonIn());
		ps.setString(cnt++, vr.getAfternoonOut());
		ps.setInt(cnt++, vr.getIsActive());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		ps.setInt(cnt++, vr.getTimeExtension());
		
		LogU.add(vr.getMorningTimeIn());
		LogU.add(vr.getMorningTimeOut());
		LogU.add(vr.getAfternoonIn());
		LogU.add(vr.getAfternoonOut());
		LogU.add(vr.getIsActive());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		LogU.add(vr.getTimeExtension());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeetimeoff : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO employeetimeoff ("
				+ "offid,"
				+ "morningtimein,"
				+ "morningtimeout,"
				+ "afternoontimein,"
				+ "afternoontimeout,"
				+ "isactiveoff,"
				+ "empid,"
				+ "timeextension)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employeetimeoff");
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
		
		ps.setString(cnt++, getMorningTimeIn());
		ps.setString(cnt++, getMorningTimeOut());
		ps.setString(cnt++, getAfternoonIn());
		ps.setString(cnt++, getAfternoonOut());
		ps.setInt(cnt++, getIsActive());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setInt(cnt++, getTimeExtension());
		
		LogU.add(getMorningTimeIn());
		LogU.add(getMorningTimeOut());
		LogU.add(getAfternoonIn());
		LogU.add(getAfternoonOut());
		LogU.add(getIsActive());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getTimeExtension());		
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employeetimeoff : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static EmployeeTimeOff updateData(EmployeeTimeOff vr){
		String sql = "UPDATE employeetimeoff SET "
				+ "morningtimein=?,"
				+ "morningtimeout=?,"
				+ "afternoontimein=?,"
				+ "afternoontimeout=?,"
				+ "empid=?,"
				+ "timeextension=?" 
				+ " WHERE offid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		ps.setString(cnt++, vr.getMorningTimeIn());
		ps.setString(cnt++, vr.getMorningTimeOut());
		ps.setString(cnt++, vr.getAfternoonIn());
		ps.setString(cnt++, vr.getAfternoonOut());
		ps.setLong(cnt++, vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		ps.setInt(cnt++, vr.getTimeExtension());
		ps.setInt(cnt++, vr.getId());
		
		LogU.add(vr.getMorningTimeIn());
		LogU.add(vr.getMorningTimeOut());
		LogU.add(vr.getAfternoonIn());
		LogU.add(vr.getAfternoonOut());
		LogU.add(vr.getEmployee()==null? 0 : vr.getEmployee().getId());
		LogU.add(vr.getTimeExtension());
		LogU.add(vr.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeetimeoff : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE employeetimeoff SET "
				+ "morningtimein=?,"
				+ "morningtimeout=?,"
				+ "afternoontimein=?,"
				+ "afternoontimeout=?,"
				+ "empid=?,"
				+ "timeextension=?" 
				+ " WHERE offid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		ps.setString(cnt++, getMorningTimeIn());
		ps.setString(cnt++, getMorningTimeOut());
		ps.setString(cnt++, getAfternoonIn());
		ps.setString(cnt++, getAfternoonOut());
		ps.setLong(cnt++, getEmployee()==null? 0 : getEmployee().getId());
		ps.setInt(cnt++, getTimeExtension());
		ps.setInt(cnt++, getId());
		
		LogU.add(getMorningTimeIn());
		LogU.add(getMorningTimeOut());
		LogU.add(getAfternoonIn());
		LogU.add(getAfternoonOut());
		LogU.add(getEmployee()==null? 0 : getEmployee().getId());
		LogU.add(getTimeExtension());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employeetimeoff : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static int getLatestId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT offid FROM employeetimeoff  ORDER BY offid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("offid");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static int getInfo(int id){
		boolean isNotNull=false;
		int result =0;
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
	public static boolean isIdNoExist(int id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT offid FROM employeetimeoff WHERE offid=?");
		ps.setInt(1, id);
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
		String sql = "UPDATE employeetimeoff set isactiveoff=0 WHERE offid=?";
		
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getTimeExtension() {
		return timeExtension;
	}

	public void setTimeExtension(int timeExtension) {
		this.timeExtension = timeExtension;
	}
	
}
