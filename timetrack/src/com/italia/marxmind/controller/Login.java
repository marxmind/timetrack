package com.italia.marxmind.controller;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.marxmind.application.ClientInfo;
import com.italia.marxmind.database.ConnectDB;
import com.italia.marxmind.security.SecureChar;
import com.italia.marxmind.sessions.SessionBean;
import com.italia.marxmind.utils.LogU;

public class Login {

	private long id;
	private String username;
	private String password;
	private String lastlogin;
	private UserAccessLevel accessLevel;
	private int isOnline;
	private UserDtls userDtls;
	private Timestamp timestamp;
	private String logintime;
	private String clientip;
	private String clientbrowser;
	private int isActive;
	
public Login(){}
	
	public static Login validUser(String userName, String password) {
		Login in = null;
		String[] params = new String[1];
		params[0] = userName;
		String sql ="SELECT * FROM login WHERE isactive=1 AND username=?";
		
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
			String dbPass = SecureChar.decodePassword(rs.getString("password"));
			
			if(password.equals(dbPass)) {
				in = new Login();
				try{in.setId(rs.getLong("logid"));}catch(NullPointerException e){}
				try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
				try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
				try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
				try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
				try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
				try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
				try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
				try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
				
				UserAccessLevel level = new UserAccessLevel();
				try{level.setUseraccesslevelid(rs.getInt("useraccesslevelid"));}catch(NullPointerException e){}
				in.setAccessLevel(level);
				
				UserDtls user = new UserDtls();
				try{user.setId(rs.getLong("userdtlsid"));}catch(NullPointerException e){}
				in.setUserDtls(user);
			}
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return in;
	}
	
	public static boolean isUserActive(Login in) {
		
		String sql ="SELECT isOnline FROM login WHERE logid=?";
		String[] params = new String[1];
		params[0] = in.getId()+"";
		
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
			
			int isOnline = rs.getInt("isOnline");
			return isOnline==1? true : false;
			
			
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return false;
	}
	
	public static List<Login> retrieve(String sql, String[] params){
		List<Login> ins = new ArrayList<Login>();
		
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
			
			Login in = new Login();
			try{in.setId(rs.getLong("logid"));}catch(NullPointerException e){}
			try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
			try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
			try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
			try{in.setAccessLevel(UserAccessLevel.userAccessLevel(rs.getString("useraccesslevelid")));}catch(NullPointerException e){}
			try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
			try{UserDtls user = new UserDtls();
			user.setId(rs.getLong("userdtlsid"));
			in.setUserDtls(user);}catch(NullPointerException e){}
			try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
			try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
			try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
			try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
			ins.add(in);
		}
		
		rs.close();
		ps.close();
		ConnectDB.close(conn);
		}catch(Exception e){}
		
		return ins;
	}
	
	public static Login login(String logid){
		Login in = new Login();
		
		in.setId(Long.valueOf(logid));
		in = Login.retrieve("SELECT * FROM login WHERE isactive=1 WHERE logid="+logid, new String[0]).get(0);
		
		return in;
	}
	
	public static boolean validate(String sql, String[] params){
		
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
			
			if(rs.next()){
				return true;
			}
			rs.close();
			ps.close();
			ConnectDB.close(conn);
			}catch(Exception e){}
		return false;
	}
	
	public static void save(Login in){
		if(in!=null){
			LogU.open();
			long id = Login.getInfo(in.getId()==0? Login.getLatestId()+1 : in.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				Login.insertData(in, "1");
			}else if(id==2){
				LogU.add("update Data ");
				Login.updateData(in);
			}else if(id==3){
				LogU.add("added new Data ");
				Login.insertData(in, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getInfo(getId()==0? getLatestId()+1 : getId());
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
	
	public static Login insertData(Login in, String type){
		String sql = "INSERT INTO login ("
				+ "logid,"
				+ "username,"
				+ "password,"
				+ "lastlogin,"
				+ "useraccesslevelid,"
				+ "isOnline,"
				+ "userdtlsid,"
				+ "logintime,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "isactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table login");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			in.setId(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			in.setId(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(2, in.getUsername());
		ps.setString(3, in.getPassword());
		ps.setString(4, in.getLastlogin());
		ps.setInt(5, in.getAccessLevel().getUseraccesslevelid());
		ps.setInt(6, in.getIsOnline());
		ps.setLong(7, in.getUserDtls().getId());
		ps.setString(8, in.getLogintime());
		ps.setString(9, in.getClientip());
		ps.setString(10, in.getClientbrowser());
		ps.setInt(11, in.getIsActive());
		
		LogU.add(in.getUsername());
		LogU.add(in.getPassword());
		LogU.add(in.getLastlogin());
		LogU.add(in.getAccessLevel().getUseraccesslevelid());
		LogU.add(in.getIsOnline());
		LogU.add(in.getUserDtls().getId());
		LogU.add(in.getLogintime());
		LogU.add(in.getClientip());
		LogU.add(in.getClientbrowser());
		LogU.add(in.getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to login : " + s.getMessage());
		}
		LogU.close();
		return in;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO login ("
				+ "logid,"
				+ "username,"
				+ "password,"
				+ "lastlogin,"
				+ "useraccesslevelid,"
				+ "isOnline,"
				+ "userdtlsid,"
				+ "logintime,"
				+ "clientip,"
				+ "clientbrowser,"
				+ "isactive)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table login");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			LogU.add("Logid: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			LogU.add("logid: " + id);
		}
		
		ps.setString(2, getUsername());
		ps.setString(3, getPassword());
		ps.setString(4, getLastlogin());
		ps.setInt(5, getAccessLevel().getUseraccesslevelid());
		ps.setInt(6, getIsOnline());
		ps.setLong(7, getUserDtls().getId());
		ps.setString(8, getLogintime());
		ps.setString(9, getClientip());
		ps.setString(10, getClientbrowser());
		ps.setInt(11, getIsActive());
		
		LogU.add(getUsername());
		LogU.add(getPassword());
		LogU.add(getLastlogin());
		LogU.add(getAccessLevel().getUseraccesslevelid());
		LogU.add(getIsOnline());
		LogU.add(getUserDtls().getId());
		LogU.add(getLogintime());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getIsActive());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to login : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static Login updateData(Login in){
		String sql = "UPDATE login SET "
				+ "username=?,"
				+ "password=?,"
				+ "lastlogin=?,"
				+ "useraccesslevelid=?,"
				+ "isOnline=?,"
				+ "userdtlsid=?,"
				+ "logintime=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "isactive=?" 
				+ " WHERE logid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table login");
		
		ps.setString(1, in.getUsername());
		ps.setString(2, in.getPassword());
		ps.setString(3, in.getLastlogin());
		ps.setInt(4, in.getAccessLevel().getUseraccesslevelid());
		ps.setInt(5, in.getIsOnline());
		ps.setLong(6, in.getUserDtls().getId());
		ps.setString(7, in.getLogintime());
		ps.setString(8, in.getClientip());
		ps.setString(9, in.getClientbrowser());
		ps.setInt(10, in.getIsActive());
		ps.setLong(11, in.getId());
		 
		LogU.add(in.getUsername());
		LogU.add(in.getPassword());
		LogU.add(in.getLastlogin());
		LogU.add(in.getAccessLevel().getUseraccesslevelid());
		LogU.add(in.getIsOnline());
		LogU.add(in.getUserDtls().getId());
		LogU.add(in.getLogintime());
		LogU.add(in.getClientip());
		LogU.add(in.getClientbrowser());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to login : " + s.getMessage());
		}
		LogU.close();
		return in;
	}
	
	public void updateData(){
		String sql = "UPDATE login SET "
				+ "username=?,"
				+ "password=?,"
				+ "lastlogin=?,"
				+ "useraccesslevelid=?,"
				+ "isOnline=?,"
				+ "userdtlsid=?,"
				+ "logintime=?,"
				+ "clientip=?,"
				+ "clientbrowser=?,"
				+ "isactive=?" 
				+ " WHERE logid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		LogU.add("===========================START=========================");
		LogU.add("update data into table login");
		
		ps.setString(1,getUsername());
		ps.setString(2,getPassword());
		ps.setString(3,getLastlogin());
		ps.setInt(4,getAccessLevel().getUseraccesslevelid());
		ps.setInt(5,getIsOnline());
		ps.setLong(6,getUserDtls().getId());
		ps.setString(7,getLogintime());
		ps.setString(8,getClientip());
		ps.setString(9,getClientbrowser());
		ps.setInt(10, getIsActive());
		ps.setLong(11, getId());
		
		LogU.add(getUsername());
		LogU.add(getPassword());
		LogU.add(getLastlogin());
		LogU.add(getAccessLevel().getUseraccesslevelid());
		LogU.add(getIsOnline());
		LogU.add(getUserDtls().getId());
		LogU.add(getLogintime());
		LogU.add(getClientip());
		LogU.add(getClientbrowser());
		LogU.add(getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		ConnectDB.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to login : " + s.getMessage());
		}
		LogU.close();
	}
	
	public static boolean checkUserStatus(){
		LogU.open();
		Login in = getUserLogin();
		LogU.add("Checking user status...");
		//check browser, user ip address and online status
		if(ClientInfo.getBrowserName().trim().equalsIgnoreCase(in.getClientbrowser().trim())
				&& ClientInfo.getClientIP().trim().equalsIgnoreCase(in.getClientip().trim())
					&& in.getIsOnline()==1
				){
			System.out.println("user is valid");
			return true;
			
		}else{
			LogU.add("user is invalid. System invalidating the process...");
		}
		LogU.close();
		return false;
	}
	
	public static boolean checkUserOnline() {
		
		Login in = getUserLogin();
		
		if(in.getIsOnline()==1) {
			return true;
		}
		
		return false;
	}
	
	public static Login getUserLogin(){
		String username = "error";
		String userid = "error";
		Login in = new Login();
		try{
			HttpSession session = SessionBean.getSession();
			username = session.getAttribute("username").toString();
			userid = session.getAttribute("userid").toString();
			
			String tableLogin = "lin";
			String tableUser = "lusr";
			String tableLvl = "lvl";
			
			 String sql = "SELECT * FROM login " + tableLogin + ", userdtls " + tableUser +", useraccesslevel "+ tableLvl +" WHERE " +
			 tableLogin +".userdtlsid=" + tableUser +".userdtlsid AND " +
			 tableLogin +".useraccesslevelid=" + tableLvl +".useraccesslevelid AND " +
			 tableLogin + ".logid=? AND " +
			 tableLogin + ".username=? AND " +
			 tableLogin + ".isactive=1 ";
			 String[] params = new String[2];
				params[0] = userid;
				params[1] = username;
			 
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
				System.out.println("SQL Login " + ps.toString());
				rs = ps.executeQuery();
				
				while(rs.next()){
					
					try{in.setId(rs.getLong("logid"));}catch(NullPointerException e){}
					try{in.setUsername(rs.getString("username"));}catch(NullPointerException e){}
					try{in.setPassword(rs.getString("password"));}catch(NullPointerException e){}
					try{in.setLastlogin(rs.getString("lastlogin"));}catch(NullPointerException e){}
					try{in.setIsOnline(rs.getInt("isOnline"));}catch(NullPointerException e){}
					try{in.setLogintime(rs.getString("logintime"));}catch(NullPointerException e){}
					try{in.setClientip(rs.getString("clientip"));}catch(NullPointerException e){}
					try{in.setClientbrowser(rs.getString("clientbrowser"));}catch(NullPointerException e){}
					try{in.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
					
					
					UserAccessLevel level = new UserAccessLevel();
					try{level.setUseraccesslevelid(rs.getInt("useraccesslevelid"));}catch(NullPointerException e){}
					try{level.setLevel(rs.getInt("level"));}catch(NullPointerException e){}
					try{level.setName(rs.getString("levelname"));}catch(NullPointerException e){}
					try{in.setAccessLevel(level);}catch(NullPointerException e){}
					
					
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
					try{user.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
					try{user.setIsActive(rs.getInt("isactive"));}catch(NullPointerException e){}
					try{in.setUserDtls(user);}catch(NullPointerException e){}
					
					
					}
				
				rs.close();
				ps.close();
				ConnectDB.close(conn);
				}catch(Exception e){}
			
		}catch(Exception e){}
		return in;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT logid FROM login  ORDER BY logid DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("logid");
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
		ps = conn.prepareStatement("SELECT logid FROM login WHERE logid=?");
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
		String sql = "UPDATE login set isactive=0 WHERE logid=?";
		
		if(!retain){
			sql = "DELETE FROM login WHERE logid=?";
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(String lastlogin) {
		this.lastlogin = lastlogin;
	}
	public UserAccessLevel getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(UserAccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}
	public int getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}
	public UserDtls getUserDtls() {
		return userDtls;
	}
	public void setUserDtls(UserDtls userDtls) {
		this.userDtls = userDtls;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public String getLogintime() {
		return logintime;
	}
	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}
	public String getClientip() {
		return clientip;
	}
	public void setClientip(String clientip) {
		this.clientip = clientip;
	}
	public String getClientbrowser() {
		return clientbrowser;
	}
	public void setClientbrowser(String clientbrowser) {
		this.clientbrowser = clientbrowser;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
}
