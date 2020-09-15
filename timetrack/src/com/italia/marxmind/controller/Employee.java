package com.italia.marxmind.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.utils.DateUtils;
import com.italia.marxmind.utils.LogU;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 2/8/2020
 *
 */

public class Employee {

	private long id;
	private String dateRegistered;
	private String employeID;
	private String labelName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String suffixName;
	private String fullName;
	private int gender;
	private String birthDate;
	private String photoid;
	private int isActive;
	
	/**
	 * 
	 * @param code
	 * @return not in use
	 */
	public static String employeeNumber(String code){
		String cardNum = "0000000000";
		int id = 0;
		try{id = Integer.valueOf(getLatestEmployeeNo().split(" ")[1]);}catch(Exception e){}
		String newCardNumber = (id+1) + "";
		
		int num = newCardNumber.length();
		
		switch(num){
			case 1: cardNum = code+"00000"+newCardNumber;  break;
			case 2: cardNum = code+"0000"+newCardNumber;  break;
			case 3: cardNum = code+"000"+newCardNumber;  break;
			case 4: cardNum = code+"00"+newCardNumber;  break;
			case 5: cardNum = code+"0"+newCardNumber;  break;
			case 6: cardNum = code+newCardNumber;  break;
		}
		
		return cardNum;
	}
	
	public static List<Employee> retrieve(String sqlAdd, String[] params){
		List<Employee> vrs = new ArrayList<Employee>();
		
		String sql = "SELECT * FROM employee  WHERE ";
				
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
			
			vrs.add(e);
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return vrs;
	}
	
	public static List<String> retrieve(String query,String fieldName, String limit){
		
		String sql = "SELECT DISTINCT " + fieldName + " FROM employee WHERE " + fieldName +" like '%" + query + "%' " + limit;	
		List<String> result = new ArrayList<>();		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		LogU.openSave("Employee SQL " + ps.toString());
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			result.add(rs.getString(fieldName));
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return result;
	}
	
	
	public static Employee save(Employee vr){
		if(vr!=null){
			LogU.open();
			long id = Employee.getInfo(vr.getId() ==0? Employee.getLatestId()+1 : vr.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				vr = Employee.insertData(vr, "1");
			}else if(id==2){
				LogU.add("update Data ");
				vr = Employee.updateData(vr);
			}else if(id==3){
				LogU.add("added new Data ");
				vr = Employee.insertData(vr, "3");
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
	
	public static Employee insertData(Employee vr, String type){
		String sql = "INSERT INTO employee ("
				+ "empid,"
				+ "dateRegistered,"
				+ "employeID,"
				+ "labelName,"
				+ "firstName,"
				+ "middleName,"
				+ "lastName,"
				+ "suffixName,"
				+ "fullName,"
				+ "gender,"
				+ "birthDate,"
				+ "photoid,"
				+ "isactiveemp)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employee");
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
		
		ps.setString(cnt++, vr.getDateRegistered());
		ps.setString(cnt++, vr.getEmployeID());
		ps.setString(cnt++, vr.getLabelName());
		ps.setString(cnt++, vr.getFirstName());
		ps.setString(cnt++, vr.getMiddleName());
		ps.setString(cnt++, vr.getLastName());
		ps.setString(cnt++, vr.getSuffixName());
		ps.setString(cnt++, vr.getFullName());
		ps.setInt(cnt++, vr.getGender());
		ps.setString(cnt++, vr.getBirthDate());
		ps.setString(cnt++, vr.getPhotoid());
		ps.setInt(cnt++, vr.getIsActive());
		
		LogU.add(vr.getDateRegistered());
		LogU.add(vr.getEmployeID());
		LogU.add(vr.getLabelName());
		LogU.add(vr.getFirstName());
		LogU.add(vr.getMiddleName());
		LogU.add(vr.getLastName());
		LogU.add(vr.getSuffixName());
		LogU.add(vr.getFullName());
		LogU.add(vr.getGender());
		LogU.add(vr.getBirthDate());
		LogU.add(vr.getPhotoid());
		LogU.add(vr.getIsActive());
		
		
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employee : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO employee ("
				+ "empid,"
				+ "dateRegistered,"
				+ "employeID,"
				+ "labelName,"
				+ "firstName,"
				+ "middleName,"
				+ "lastName,"
				+ "suffixName,"
				+ "fullName,"
				+ "gender,"
				+ "birthDate,"
				+ "photoid,"
				+ "isactiveemp)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		
		LogU.add("inserting data into table employee");
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
		
		ps.setString(cnt++, getDateRegistered());
		ps.setString(cnt++, getEmployeID());
		ps.setString(cnt++, getLabelName());
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setString(cnt++, getSuffixName());
		ps.setString(cnt++, getFullName());
		ps.setInt(cnt++, getGender());
		ps.setString(cnt++, getBirthDate());
		ps.setString(cnt++, getPhotoid());
		ps.setInt(cnt++, getIsActive());
		
		LogU.add(getDateRegistered());
		LogU.add(getEmployeID());
		LogU.add(getLabelName());
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getSuffixName());
		LogU.add(getFullName());
		LogU.add(getGender());
		LogU.add(getBirthDate());
		LogU.add(getPhotoid());
		LogU.add(getIsActive());
		
		
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to employee : " + s.getMessage());
		}
		LogU.close();
		
	}
	
	public static Employee updateData(Employee vr){
		String sql = "UPDATE employee SET "
				+ "dateRegistered=?,"
				+ "employeID=?,"
				+ "labelName=?,"
				+ "firstName=?,"
				+ "middleName=?,"
				+ "lastName=?,"
				+ "suffixName=?,"
				+ "fullName=?,"
				+ "gender=?,"
				+ "birthDate=?,"
				+ "photoid=?" 
				+ " WHERE empid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
	
		int cnt = 1;
		
		LogU.add("updating data into table employee");
		
		ps.setString(cnt++, vr.getDateRegistered());
		ps.setString(cnt++, vr.getEmployeID());
		ps.setString(cnt++, vr.getLabelName());
		ps.setString(cnt++, vr.getFirstName());
		ps.setString(cnt++, vr.getMiddleName());
		ps.setString(cnt++, vr.getLastName());
		ps.setString(cnt++, vr.getSuffixName());
		ps.setString(cnt++, vr.getFullName());
		ps.setInt(cnt++, vr.getGender());
		ps.setString(cnt++, vr.getBirthDate());
		ps.setString(cnt++, vr.getPhotoid());
		ps.setLong(cnt++, vr.getId());
		
		LogU.add(vr.getDateRegistered());
		LogU.add(vr.getEmployeID());
		LogU.add(vr.getLabelName());
		LogU.add(vr.getFirstName());
		LogU.add(vr.getMiddleName());
		LogU.add(vr.getLastName());
		LogU.add(vr.getSuffixName());
		LogU.add(vr.getFullName());
		LogU.add(vr.getGender());
		LogU.add(vr.getBirthDate());
		LogU.add(vr.getPhotoid());
		LogU.add(vr.getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employee : " + s.getMessage());
		}
		LogU.close();
		return vr;
	}
	
	public void updateData(){
		String sql = "UPDATE employee SET "
				+ "dateRegistered=?,"
				+ "employeID=?,"
				+ "labelName=?,"
				+ "firstName=?,"
				+ "middleName=?,"
				+ "lastName=?,"
				+ "suffixName=?,"
				+ "fullName=?,"
				+ "gender=?,"
				+ "birthDate=?,"
				+ "photoid=?" 
				+ " WHERE empid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt = 1;
		
		LogU.add("updating data into table employee");
		
		ps.setString(cnt++, getDateRegistered());
		ps.setString(cnt++, getEmployeID());
		ps.setString(cnt++, getLabelName());
		ps.setString(cnt++, getFirstName());
		ps.setString(cnt++, getMiddleName());
		ps.setString(cnt++, getLastName());
		ps.setString(cnt++, getSuffixName());
		ps.setString(cnt++, getFullName());
		ps.setInt(cnt++, getGender());
		ps.setString(cnt++, getBirthDate());
		ps.setString(cnt++, getPhotoid());
		ps.setLong(cnt++, getId());
		
		LogU.add(getDateRegistered());
		LogU.add(getEmployeID());
		LogU.add(getLabelName());
		LogU.add(getFirstName());
		LogU.add(getMiddleName());
		LogU.add(getLastName());
		LogU.add(getSuffixName());
		LogU.add(getFullName());
		LogU.add(getGender());
		LogU.add(getBirthDate());
		LogU.add(getPhotoid());
		LogU.add(getId());
				
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to employee : " + s.getMessage());
		}
		LogU.close();
	}
	
	
	
	public static String getLatestEmployeeNo(){
		String id ="0";
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT employeID FROM employee WHERE isactiveemp=1 ORDER BY empid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getString("employeID");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT empid FROM employee  ORDER BY empid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("empid");
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
		ps = conn.prepareStatement("SELECT empid FROM employee WHERE empid=?");
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
	
	public static int getRecordedEmployee(String sqlAdd) {
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int result = 0;
		try{
		String sql = "SELECT count(*) as count FROM employee WHERE isactiveemp=1 " + sqlAdd;
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			result = rs.getInt("count");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE employee set isactiveemp=0 WHERE empid=?";
		
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
	public String getDateRegistered() {
		return dateRegistered;
	}
	public void setDateRegistered(String dateRegistered) {
		this.dateRegistered = dateRegistered;
	}
	public String getEmployeID() {
		return employeID;
	}
	public void setEmployeID(String employeID) {
		this.employeID = employeID;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSuffixName() {
		return suffixName;
	}
	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getPhotoid() {
		return photoid;
	}
	public void setPhotoid(String photoid) {
		this.photoid = photoid;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public static void main(String[] args) {
		
		/*
		 * Employee e = new Employee(); e.setId(1);
		 * e.setDateRegistered(DateUtils.getCurrentDateYYYYMMDD());
		 * e.setEmployeID("LGU-Lake Sebu-001"); e.setLabelName("DR.");
		 * e.setFirstName("Mark"); e.setMiddleName("Rivera"); e.setLastName("Italia");
		 * e.setSuffixName("Jr."); e.setFullName(e.getLabelName() + " " +
		 * e.getFirstName() + " " + e.getLastName() + " " + e.getSuffixName());
		 * e.setGender(1); e.setBirthDate("1986-10-18"); e.setPhotoid("1234567890");
		 * e.setIsActive(1); e.save();
		 */
		
		Employee.retrieve(" isactiveemp=1", new String[0]).stream().forEachOrdered(e -> System.out.println(e.getFullName()));
		
	}
	
}
