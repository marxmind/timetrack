package com.italia.marxmind.controller;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.sessions.SessionBean;
import com.italia.marxmind.utils.LogU;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 * @version 1.0
 *
 */
public class UserDtls {

	private long id;
	private String regdate;
	private String firstname;
	private String middlename;
	private String lastname;
	private String address;
	private String contactno;
	private int age;
	private int gender;
	private Login login;
	private EmployeePosition positon;
	private Department department;
	private UserDtls addedBy;
	private Timestamp timestamp;
	private int isActive;
	
	public UserDtls(){}
	
	public static List<UserDtls> retrieve(String sqlAdd, String[] params){
		List<UserDtls> users = Collections.synchronizedList(new ArrayList<UserDtls>());
		
		String tabUser = "usr";
		String tabIn = "lg";
		String tabos = "po";
		String tabDep = "dp";
		
		String sql = "SELECT * FROM userdtls " + tabUser + ", empposition " + tabos + ", login " + tabIn + ", department " + tabDep + " WHERE " + tabUser + ".isactive=1 AND" +
		tabUser + ".logid=" + tabIn + ".logid AND " +
		tabUser + ".jobtitleid=" + tabos + ".posid AND " +
		tabUser + ".departmentid=" + tabDep + ".departmentid ";
		
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			UserDtls user = new UserDtls();
			try{user.setId(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			
			UserDtls u = new UserDtls();
			u.setId(rs.getLong("addedby"));
			user.setAddedBy(u);
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
			EmployeePosition pos = new EmployeePosition();
			try{pos.setId(rs.getInt("posid"));}catch(NullPointerException e){}
			try{pos.setName(rs.getString("posname"));}catch(NullPointerException e){}
			try{pos.setIsAcativePosition(rs.getInt("isactivepos"));}catch(NullPointerException e){}
			user.setPositon(pos);
			
			Department dep = new Department();
			try{dep.setDepid(rs.getInt("departmentid"));}catch(NullPointerException e){}
			try{dep.setDepartmentName(rs.getString("departmentname"));}catch(NullPointerException e){}
			user.setDepartment(dep);
			
			Login in = new Login();
			try{in.setId(rs.getLong("logid"));}catch(NullPointerException e){}
			try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
			try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
			try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
			try{in.setAccessLevel(UserAccessLevel.userAccessLevel(rs.getString("useraccesslevelid")));}catch(NullPointerException e){}
			try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
			try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
			try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			user.setLogin(in);
			
			
			users.add(user);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return users;
	}
	
	public static UserDtls retrievePosition(int postionId){
		UserDtls user = new UserDtls();
		String sql = "SELECT * FROM userdtls WHERE isactive=1 AND jobtitleid=?";
		String[] params = new String[1];
		params[0] = postionId+"";
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
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			
			try{user.setId(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
			try{user.setRegdate(rs.getString("regdate"));;}catch(NullPointerException e){}
			try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
			try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
			try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
			try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
			try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
			try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
			try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return user;
	}
	
	public static UserDtls addedby(String userdtlsid){
		UserDtls user = new UserDtls();
		String sql = "SELECT * FROM userdtls WHERE isactive=1 AND userdtlsid=?";
		String[] params = new String[1];
		params[0] = userdtlsid;
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
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				try{user.setId(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
				try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
				try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
				try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
				try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
				try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
				try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
				try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
				try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
				try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			}
			
			
		}catch(Exception e){}
		return user;
	}
	
	public static UserDtls getUser(String userdtlsid){
		UserDtls user = new UserDtls();
		String sql = "SELECT * FROM userdtls WHERE isactive=1 AND userdtlsid=?";
		String[] params = new String[1];
		params[0] = userdtlsid;
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
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				try{user.setId(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
				try{user.setFirstname(rs.getString("firstname"));}catch(NullPointerException e){}
				try{user.setMiddlename(rs.getString("middlename"));}catch(NullPointerException e){}
				try{user.setLastname(rs.getString("lastname"));}catch(NullPointerException e){}
				try{user.setAddress(rs.getString("address"));}catch(NullPointerException e){}
				try{user.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
				try{user.setAge(rs.getInt("age"));}catch(NullPointerException e){}
				try{user.setGender(rs.getInt("gender"));}catch(NullPointerException e){}
				try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
				try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			}
			
			
		}catch(Exception e){}
		return user;
	}
	
	
	
	public static UserDtls save(UserDtls user){
		if(user!=null){
			LogU.open();
			long id = UserDtls.getInfo(user.getId() ==0? UserDtls.getLatestId()+1 : user.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				user= UserDtls.insertData(user, "1");
			}else if(id==2){
				LogU.add("update Data ");
				user =UserDtls.updateData(user);
			}else if(id==3){
				LogU.add("added new Data ");
				user = UserDtls.insertData(user, "3");
			}
			
		}
		return user;
	}
	
	public void save(){
			LogU.open();
			long id = UserDtls.getInfo(getId() == 0? getLatestId()+1 : getId());
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
	
	public static UserDtls insertData(UserDtls user, String type){
		String sql = "INSERT INTO userdtls ("
				+ "userdtlsid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "address,"
				+ "contactno,"
				+ "age,"
				+ "gender,"
				+ "logid,"
				+ "jobtitleid,"
				+ "departmentid,"
				+ "addedby,"
				+ "isactive,"
				+ "regdate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table userdtls");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			user.setId(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			user.setId(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, user.getFirstname());
		ps.setString(3, user.getMiddlename());
		ps.setString(4, user.getLastname());
		ps.setString(5, user.getAddress());
		ps.setString(6, user.getContactno());
		ps.setInt(7, user.getAge());
		ps.setInt(8, user.getGender());
		ps.setLong(9, user.getLogin()==null? 0l : (user.getLogin().getId()==0? 0l : user.getLogin().getId()));
		ps.setInt(10, user.getPositon()==null? 0 : (user.getPositon().getId()==0? 0 : user.getPositon().getId()));
		ps.setInt(11, user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		ps.setLong(12, user.getAddedBy()==null? 0 : (user.getAddedBy().getId()==0? 0l : user.getAddedBy().getId()));
		ps.setInt(13, user.getIsActive());
		ps.setString(14, user.getRegdate());
		
		LogU.add( user.getFirstname());
		LogU.add( user.getMiddlename());
		LogU.add(user.getLastname());
		LogU.add(user.getAddress());
		LogU.add(user.getContactno());
		LogU.add(user.getAge());
		LogU.add(user.getGender());
		LogU.add(user.getLogin()==null? 0l : (user.getLogin().getId()==0? 0l : user.getLogin().getId()));
		LogU.add(user.getPositon()==null? 0 : (user.getPositon().getId()==0? 0 : user.getPositon().getId()));
		LogU.add(user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		LogU.add(user.getAddedBy()==null? 0 : (user.getAddedBy().getId()==0? 0l : user.getAddedBy().getId()));
		LogU.add(user.getIsActive());
		LogU.add(user.getRegdate());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to userdtls : " + s.getMessage());
		}
		LogU.close();
		return user;
	}

	public void insertData(String type){
		String sql = "INSERT INTO userdtls ("
				+ "userdtlsid,"
				+ "firstname,"
				+ "middlename,"
				+ "lastname,"
				+ "address,"
				+ "contactno,"
				+ "age,"
				+ "gender,"
				+ "logid,"
				+ "jobtitleid,"
				+ "departmentid,"
				+ "addedby,"
				+ "isactive,"
				+ "regdate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table userdtls");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			LogU.add("id: " + id);
		}
		
		ps.setString(2, getFirstname());
		ps.setString(3, getMiddlename());
		ps.setString(4, getLastname());
		ps.setString(5, getAddress());
		ps.setString(6, getContactno());
		ps.setInt(7, getAge());
		ps.setInt(8, getGender());
		ps.setLong(9, getLogin()==null? 0l : (getLogin().getId()==0? 0l : getLogin().getId()));
		ps.setInt(10, getPositon()==null? 0 : (getPositon().getId()==0? 0 : getPositon().getId()));
		ps.setInt(11, getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		ps.setLong(12, getAddedBy()==null? 0 : (getAddedBy().getId()==0? 0l : getAddedBy().getId()));
		ps.setInt(13, getIsActive());
		ps.setString(14, getRegdate());
		
		LogU.add( getFirstname());
		LogU.add( getMiddlename());
		LogU.add(getLastname());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getLogin()==null? 0l : (getLogin().getId()==0? 0l : getLogin().getId()));
		LogU.add(getPositon()==null? 0 : (getPositon().getId()==0? 0 : getPositon().getId()));
		LogU.add(getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		LogU.add(getAddedBy()==null? 0 : (getAddedBy().getId()==0? 0l : getAddedBy().getId()));
		LogU.add(getIsActive());
		LogU.add(getRegdate());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to userdtls : " + s.getMessage());
		}
		LogU.close();
		
	}
	
	public static UserDtls updateData(UserDtls user){
		String sql = "UPDATE userdtls SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "logid=?,"
				+ "jobtitleid=?,"
				+ "departmentid=?,"
				+ "addedby=?,"
				+ "isactive=?" 
				+ " WHERE userdtlsid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table userdtls");
		
		ps.setString(1, user.getFirstname());
		ps.setString(2, user.getMiddlename());
		ps.setString(3, user.getLastname());
		ps.setString(4, user.getAddress());
		ps.setString(5, user.getContactno());
		ps.setInt(6, user.getAge());
		ps.setInt(7, user.getGender());
		ps.setLong(8, user.getLogin()==null? 0l : (user.getLogin().getId()==0? 0l : user.getLogin().getId()));
		ps.setInt(9, user.getPositon()==null? 0 : (user.getPositon().getId()==0? 0 : user.getPositon().getId()));
		ps.setInt(10, user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		ps.setLong(11, user.getAddedBy()==null? 0 : (user.getAddedBy().getId()==0? 0l : user.getAddedBy().getId()));
		ps.setInt(12, user.getIsActive());
		ps.setLong(13, user.getId());
		
		LogU.add(user.getFirstname());
		LogU.add(user.getMiddlename());
		LogU.add(user.getLastname());
		LogU.add(user.getAddress());
		LogU.add(user.getContactno());
		LogU.add(user.getAge());
		LogU.add(user.getGender());
		LogU.add(user.getLogin()==null? 0l : (user.getLogin().getId()==0? 0l : user.getLogin().getId()));
		LogU.add(user.getPositon()==null? 0 : (user.getPositon().getId()==0? 0 : user.getPositon().getId()));
		LogU.add(user.getDepartment()==null? 0 : (user.getDepartment().getDepid()==0? 0 : user.getDepartment().getDepid()));
		LogU.add(user.getAddedBy()==null? 0 : (user.getAddedBy().getId()==0? 0l : user.getAddedBy().getId()));
		LogU.add(user.getIsActive());
		LogU.add(user.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to userdtls : " + s.getMessage());
		}
		LogU.close();
		return user;
	}
	
	public void updateData(){
		String sql = "UPDATE userdtls SET "
				+ "firstname=?,"
				+ "middlename=?,"
				+ "lastname=?,"
				+ "address=?,"
				+ "contactno=?,"
				+ "age=?,"
				+ "gender=?,"
				+ "logid=?,"
				+ "jobtitleid=?,"
				+ "departmentid=?,"
				+ "addedby=?,"
				+ "isactive=?" 
				+ " WHERE userdtlsid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table userdtls");
		
		ps.setString(1, getFirstname());
		ps.setString(2, getMiddlename());
		ps.setString(3, getLastname());
		ps.setString(4, getAddress());
		ps.setString(5, getContactno());
		ps.setInt(6, getAge());
		ps.setInt(7, getGender());
		ps.setLong(8, getLogin()==null? 0l : (getLogin().getId()==0? 0l : getLogin().getId()));
		ps.setInt(9, getPositon()==null? 0 : (getPositon().getId()==0? 0 : getPositon().getId()));
		ps.setInt(10, getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		ps.setLong(11, getAddedBy()==null? 0 : (getAddedBy().getId()==0? 0l : getAddedBy().getId()));
		ps.setInt(12, getIsActive());
		ps.setLong(13, getId());
		
		LogU.add(getFirstname());
		LogU.add(getMiddlename());
		LogU.add(getLastname());
		LogU.add(getAddress());
		LogU.add(getContactno());
		LogU.add(getAge());
		LogU.add(getGender());
		LogU.add(getLogin()==null? 0l : (getLogin().getId()==0? 0l : getLogin().getId()));
		LogU.add(getPositon()==null? 0 : (getPositon().getId()==0? 0 : getPositon().getId()));
		LogU.add(getDepartment()==null? 0 : (getDepartment().getDepid()==0? 0 : getDepartment().getDepid()));
		LogU.add(getAddedBy()==null? 0 : (getAddedBy().getId()==0? 0l : getAddedBy().getId()));
		LogU.add(getIsActive());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to userdtls : " + s.getMessage());
		}
		LogU.close();
		
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT userdtlsid FROM userdtls  ORDER BY userdtlsid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("userdtlsid");
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
		ps = conn.prepareStatement("SELECT userdtlsid FROM userdtls WHERE userdtlsid=?");
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
	
	public void delete(boolean retain){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE userdtls set isactive=0 WHERE userdtlsid=?";
		
		if(!retain){
			sql = "DELETE FROM userdtls WHERE userdtlsid=?";
		}
		
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
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactno() {
		return contactno;
	}
	public void setContactno(String contactno) {
		this.contactno = contactno;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public Login getLogin() {
		return login;
	}
	public void setLogin(Login login) {
		this.login = login;
	}
	
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}


	public UserDtls getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(UserDtls addedBy) {
		this.addedBy = addedBy;
	}

	public EmployeePosition getPositon() {
		return positon;
	}

	public void setPositon(EmployeePosition positon) {
		this.positon = positon;
	}
}

