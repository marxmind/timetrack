package com.italia.marxmind.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.database.ConnectDB;


@Named
@ViewScoped
public class TimelogBean {

	private String searchParam;
	private List<Employee> emp = new ArrayList<Employee>();
	private Employee employee;
	private List<Employee> employees = new ArrayList<>();
	
	public List<Employee> getEmployees() {
		//init();
		employees = new ArrayList<>();
		employees = emp;
		/*for(Employee e : dataEmployee.values()){
			employees.add(e);
		}*/
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	
	@PostConstruct
	public void init(){
		//delete previous null timein and out
		deleteNullTimesheet();
		
		//create todays data if not existing
		createTodaysTime();
		
		String type = getSearchParam()==null? "init" : getSearchParam().equalsIgnoreCase("")? "init" : getSearchParam().isEmpty()? "init" : "search";
		loadEmployee(type);
	}
	
	public String loginButton(boolean isActivate){
		System.out.println("=============IN============== " + isActivate );
		if(isActivate && getEmployee()!=null){
			System.out.println("Employee: " + getEmployee().getFirstName() + " " + getEmployee().getMiddleName() + " " + getEmployee().getLastName());
			
			String sql = "UPDATE employeetime SET "
					+ "timeIn=?,"
					+ "procby=?"
					+ " WHERE timeId=?";
			PreparedStatement ps = null;
			Connection conn = null;
			Employee e = getEmployee();
			EmployeeTime time = e.getEmployeeTime();
			try{
			conn = ConnectDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, TimeUtils.getTime12Format());
			ps.setString(2, processBy());
			ps.setLong(3, time.getTimeId());
			System.out.println("SQL UPDATE : " + ps.toString());
			ps.executeUpdate();
			ConnectDB.close(conn);
			}catch(SQLException s){
				s.printStackTrace();
			}
			init();
		}
		return "";
	}
	public String logoutButton(boolean isActive){
		System.out.println("=============OUT==============");
		
		if(isActive && getEmployee()!=null){
			System.out.println("Employee: " + getEmployee().getFirstName() + " " + getEmployee().getMiddleName() + " " + getEmployee().getLastName());
			
			Employee e = getEmployee();
			EmployeeTime time = e.getEmployeeTime();
			//calculate rendered time
			TimeSetting.initTime();
			double breakTime = CalculateEmployeeHours.calcBreakTime(TimeSetting.BREAK_TIME);
			double otStart = CalculateEmployeeHours.calcBreakTime(TimeSetting.OT_START);
			Double render[] = CalculateEmployeeHours.calcEmployeeTime(TimeSetting.TIME_IN, TimeSetting.TIME_OUT, time.getTimeIn(), TimeUtils.getTime12Format(), breakTime,otStart,1);
			//setTimeRendered(render[0]);
			
			getEmployee().getEmployeeTime().setTimeOut(TimeUtils.getTime12Format());
			getEmployee().getEmployeeTime().setTimeRendered(render[0]);
			getEmployee().getEmployeeTime().setLate(render[1]);
			getEmployee().getEmployeeTime().setOvertime(render[2]);
			getEmployee().getEmployeeTime().setUndertime(render[3]);
			getEmployee().getEmployeeTime().setEmergencyLeave(render[4]);
			getEmployee().getEmployeeTime().setSickLeave(render[5]);
			getEmployee().getEmployeeTime().setVacationLeave(render[6]);
			getEmployee().getEmployeeTime().setAbsent(render[7]);
			updateData();
		}
		
		return "";
	}
	private void updateData(){
		String sql = "UPDATE employeetime SET "
				+ "timeOut=?,"
				+ "timeRendered=?,"
				+ "late=?,"
				+ "overtime=?,"
				+ "undertime=?,"
				+ "procby=?,"
				+ "sl=?,"
				+ "vl=?,"
				+ "el=?,"
				+ "absent=?"
				+ " WHERE timeId=?";		
		PreparedStatement ps = null;
		Connection conn = null;
		Employee e = getEmployee();
		EmployeeTime time = e.getEmployeeTime();
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, time.getTimeOut());
		ps.setDouble(2, time.getTimeRendered());
		ps.setDouble(3, time.getLate());
		ps.setDouble(4, time.getOvertime());
		ps.setDouble(5, time.getUndertime());
		ps.setString(6, processBy());
		ps.setDouble(7, time.getSickLeave());
		ps.setDouble(8, time.getVacationLeave());
		ps.setDouble(9, time.getEmergencyLeave());
		ps.setDouble(10, time.getAbsent());
		ps.setLong(11, time.getTimeId());
		System.out.println("SQL UPDATE : " + ps.toString());
		ps.executeUpdate();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		init();
	}
	private void loadEmployee(String type){
		//dataEmployee = Collections.synchronizedMap(new HashMap<Long, Employee>()); 
		emp = new ArrayList<Employee>(); 
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		System.out.println("==================== loading employee details");
		
		String sql ="SELECT * FROM employeedtls, employeetime WHERE employeedtls.isActive=1 AND employeetime.employeeId = employeedtls.empId AND employeetime.status='NEW' ";
		try{
		conn = ConnectDB.getConnection();
		if("search".equalsIgnoreCase(type)){
			
			String fields = getSearchParam().replace("--", "");
			       fields = fields.replace(";", "");
			       
			       
						sql +=" AND ( employeedtls.firstName like '%"+ fields +"%'";
						sql += " OR employeedtls.middleName like '%"+ fields +"%'";
						sql += " OR employeedtls.lastName like '%"+ fields +"%')";
						sql += "AND employeetime.date=?";
						ps = conn.prepareStatement(sql);
						ps.setString(1, DateUtils.getCurrentDateMMDDYYYY());
						
			       
		rs = ps.executeQuery();
		
		while(rs.next()){
			Employee e = new Employee();
			EmployeeTime t = new EmployeeTime();
			e.setEmpId(rs.getLong("empId"));
			e.setFirstName(rs.getString("firstName"));
			e.setMiddleName(rs.getString("middleName").substring(0, 1)+".");
			e.setLastName(rs.getString("lastName"));
			Long id = rs.getLong("timeId");
			t.setTimeId(id);
			try{t.setDate(rs.getString("date"));}catch(NullPointerException n){}
			String timein="IN",timeout="OUT";
			try{
				timein=rs.getString("timeIn");
				if(timein==null)
					timein="[===IN==]";
				t.setTimeIn(timein);}catch(NullPointerException n){}
			try{
				timeout=rs.getString("timeOut");
				if(timeout==null)
					timeout="[==OUT=]";
				t.setTimeOut(timeout);}catch(NullPointerException n){}
			e.setEmployeeTime(t);
			//dataEmployee.put(e.getEmpId(), e);
			emp.add(e);
		}
		}
		ConnectDB.close(conn);
		}catch(SQLException e){}
		
	}
	
	private void deleteNullTimesheet(){
		
		//calculate rendered time
		TimeSetting.initTime();
		double breakTime = CalculateEmployeeHours.calcBreakTime(TimeSetting.BREAK_TIME);
		double otStart = CalculateEmployeeHours.calcBreakTime(TimeSetting.OT_START);
		Double render[] = CalculateEmployeeHours.calcEmployeeTime(TimeSetting.TIME_IN, TimeSetting.TIME_OUT, TimeSetting.TIME_IN, TimeSetting.TIME_OUT, breakTime,otStart,9);// marked as absent
		//setTimeRendered(render[0]);
		String sql = "UPDATE employeetime set timein=?, timeout=?, absent=?, description=? WHERE date<? AND (timein is null OR timeout is null)";
		Connection conn = ConnectDB.getConnection();
		try{
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, TimeSetting.TIME_IN);
		ps.setString(2, TimeSetting.TIME_OUT);
		ps.setDouble(3, render[7]);
		ps.setString(4, "Absent");
		ps.setString(5, DateUtils.getCurrentDateMMDDYYYY());
		ps.executeUpdate();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){}
	}
	private void createTodaysTime(){
		List<Long> empIds = new ArrayList<Long>();
		Connection conn = ConnectDB.getConnection();
		PreparedStatement ps = null;
		String sql = "";
		try{
			sql = "SELECT * FROM employeedtls WHERE isActive=1";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			System.out.println("query.... active employees");
			while(rs.next()){
				empIds.add(rs.getLong("empId"));
			}
			rs.close();
			ps.close();
			ConnectDB.close(conn);
		}catch(SQLException s){}
		
		
		//pre - inserting data to employeetime table
		
			for(Long empId : empIds){
				if(!checkDataExisting(empId)){
					Employee e = new Employee();
					EmployeeTime t = new EmployeeTime();
					e.setEmpId(empId);
					t.setDate(DateUtils.getCurrentDateMMDDYYYY());
					t.setTimeIn(null);
					t.setTimeOut(null);
					t.setDescription(null);
					t.setTimeRendered(new Double(0));
					t.setLate(new Double(0));
					t.setOvertime(new Double(0));
					t.setUndertime(new Double(0));
					t.setAbsent(new Double(0));
					t.setEmergencyLeave(new Double(0));
					t.setSickLeave(new Double(0));
					t.setVacationLeave(new Double(0));
					t.setStatus("NEW");
					e.setEmployeeTime(t);
					setEmployee(e);
					long id = getTimeInInfo(getLatestId()+1);
					if(id==1){
						insertData("1"); // insert data for the first Time
					}else if(id==3){
						insertData("3"); // add data
					}
				}
			}
		}
	private void insertData(String type){
		String sql = "INSERT INTO employeetime ("
				+ "timeId,"
				+ "date,"
				+ "timeIn,"
				+ "timeOut,"
				+ "description,"
				+ "employeeId,"
				+ "timeRendered,"
				+ "late,"
				+ "overtime,"
				+ "undertime,"
				+ "status,"
				+ "procby,"
				+ "absent,"
				+ "sl,vl,el) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		Employee e = getEmployee();
		EmployeeTime time = e.getEmployeeTime();
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement(sql);
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, 1);
		}else if("3".equalsIgnoreCase(type)){
			ps.setLong(1, getLatestId()+1);
		}
		ps.setString(2, time.getDate());
		ps.setString(3, time.getTimeIn());
		ps.setString(4, time.getTimeOut());
		ps.setString(5, time.getDescription());
		ps.setLong(6, e.getEmpId());
		ps.setDouble(7, time.getTimeRendered());
		ps.setDouble(8, time.getLate());
		ps.setDouble(9, time.getOvertime());
		ps.setDouble(10, time.getUndertime());
		ps.setString(11, EmployeePayStatus.NEW.getStatus());
		ps.setString(12, processBy());
		ps.setDouble(13, time.getAbsent());
		ps.setDouble(14, time.getSickLeave());
		ps.setDouble(15, time.getEmergencyLeave());
		ps.setDouble(16, time.getVacationLeave());
		System.out.println("SQL ADD : " + ps.toString());
		ps.execute();
		ps.close();
		ConnectDB.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
	}
	
	private String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	private Long getTimeInInfo(long id){
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
		
		//check if check_no is existing in table
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
	private boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = ConnectDB.getConnection();
		ps = conn.prepareStatement("SELECT timeId FROM employeetime WHERE timeId=?");
		ps.setLong(1, id);
		System.out.println("Is exist sql: " + ps.toString());
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
	private long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT timeId FROM employeetime  ORDER BY timeId DESC LIMIT 1";	
		conn = ConnectDB.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("timeId");
		}
		
		rs.close();
		prep.close();
		ConnectDB.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public boolean checkDataExisting(Long empId){
		try{
		String sql = "SELECT * FROM employeetime WHERE date=? AND employeeId=?";
		Connection conn = ConnectDB.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, DateUtils.getCurrentDateMMDDYYYY());
		ps.setLong(2, empId);
		ResultSet rs = ps.executeQuery();
		ConnectDB.close(conn);
		if(rs.next()){
			return true;
		}
		}catch(SQLException sql){}
		return false;
	}
	
}
