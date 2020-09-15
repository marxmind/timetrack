package com.italia.marxmind.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.italia.marxmind.controller.EmployeeTimeOff;
import com.italia.marxmind.enm.TimeMode;

public class TimeUtils {

	/**
	 * 	
	 * @return current date with time
	 * @format MM-dd-yyyy 12:00 AM
	 */
	public static String getCurrentDateMMDDYYYYTIME(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm: a");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * 	
	 * @return current date with time
	 * @format hh:mm am/pm 12:00 AM
	 */
	public static String getTime12Format(){
		DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		Date date = new Date();
		return dateFormat.format(date);
	}
	/**
	 * 	
	 * @return current date with time
	 * @format hh:mm am/pm 24:00 AM
	 */
	public static String getTime24Format(){
		DateFormat dateFormat = new SimpleDateFormat("kk:mm aa");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	
	public static void main(String[] args) {
		System.out.println(getTime12Format());
		System.out.println(getTime24Format());
	}
	
	public static String checkTime(String time) {
		
		String ampm = time.split(" ")[1];
		String mm = time.split(":")[1];
		if("PM".equalsIgnoreCase(ampm)) {
			int hh = time24Format(Integer.valueOf(time.split(":")[0]));
			return hh+":"+mm;
		}
		
		return time;
	}
	
	private static int time24Format(int hour) {
		
		switch(hour) {
		case 1 : return 13;
		case 2 : return 14;
		case 3 : return 15;
		case 4 : return 16;
		case 5 : return 17;
		case 6 : return 18;
		case 7 : return 19;
		case 8 : return 20;
		case 9 : return 21;
		case 10 : return 22;
		case 11 : return 23;
		}
		
		return 0;
	}
	
	/*
	public static double calculateTime(String timeIn, String timeOut) {
		
		//this line use in logging in time of employee
		if("IN".equalsIgnoreCase(timeIn) || "OUT".equalsIgnoreCase(timeOut)) {
			return 0;
		}
		
		if((timeOut!=null && !timeOut.isEmpty())) {
			
			timeIn = checkTime(timeIn);
			timeOut = checkTime(timeOut);
			
			String in = timeIn.split(" ")[0];
			double hourIn = Integer.valueOf(in.split(":")[0]);
			double minuteIn = Integer.valueOf(in.split(":")[1]);
			
			String out = timeOut.split(" ")[0];
			double hourOut = Integer.valueOf(out.split(":")[0]);
			double minuteOut = Integer.valueOf(out.split(":")[1]);
			
			double totalIn = hourIn + (minuteIn/60);
			double totalOut = hourOut + (minuteOut/60);
			
			
			
			double totalTime = totalOut - totalIn;
			
			return Numbers.formatDouble(totalTime);
		
		}			
		
		return 0;
	}
	*/
	private static String timeCorrecting(EmployeeTimeOff off,String type, String time, int mode) {
		
		boolean hasUndergoInThreeCondition=false;
		String ampm = time.split(" ")[1];
		int hhInOff = 0;
		int mmInOff = 0;
		int hhInAct = 0;
		int mmInAct = 0;
		
		String offIn = null;
		String timeOff = null;
		
		if(TimeMode.STRAIGHT_TIME.getId()==mode){
			
			time = time.replace(" AM", "");
			time = time.replace(" PM", "");
			
			if("IN".equalsIgnoreCase(type)) {
				timeOff = off.getMorningTimeIn();
				offIn = off.getMorningTimeIn().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}else {
				timeOff = off.getAfternoonOut();
				offIn = off.getAfternoonOut().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}
			hasUndergoInThreeCondition=true;
		}else if(TimeMode.MORNING.getId()==mode){
			time = time.replace(" AM", "");
			time = time.replace(" PM", "");
			
			if("IN".equalsIgnoreCase(type)) {
				timeOff = off.getMorningTimeIn();
				offIn = off.getMorningTimeIn().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}else {
				timeOff = off.getMorningTimeOut();
				offIn = off.getMorningTimeOut().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}
			hasUndergoInThreeCondition=true;
		}else if(TimeMode.AFTERNOON.getId()==mode){
			time = time.replace(" AM", "");
			time = time.replace(" PM", "");
			
			if("IN".equalsIgnoreCase(type)) {
				timeOff = off.getAfternoonIn();
				offIn = off.getAfternoonIn().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}else {
				timeOff = off.getAfternoonOut();
				offIn = off.getAfternoonOut().replace(" AM", "");
				offIn = offIn.replace(" PM", "");
			}
			hasUndergoInThreeCondition=true;
		}
		if(hasUndergoInThreeCondition) {	
			hhInAct = Integer.valueOf(time.split(":")[0]);
			mmInAct = Integer.valueOf(time.split(":")[1]);
			
			hhInOff = Integer.valueOf(offIn.split(":")[0]);
			mmInOff = Integer.valueOf(offIn.split(":")[1]);
			
			boolean hasNoChange = true;
			if("IN".equalsIgnoreCase(type)) {
				
				if(hhInAct<hhInOff) {//early in
					time = timeOff;//get the set time in time
					hasNoChange = false;
				}else {
					if(hhInAct==hhInOff) {
						if(mmInAct<mmInOff) {
							time = timeOff;//get the set time in time
							hasNoChange = false;
						}
					}
				}
			}else {
				
				if(hhInAct>hhInOff) {//overtime
					time = timeOff;//get the set time out time
					hasNoChange = false;
				}else {
					if(hhInAct==hhInOff) {
						if(mmInAct>mmInOff) {
							time = timeOff;//get the set time out time
							hasNoChange = false;
						}
					}
				}
			}
			if(hasNoChange) {
				time = time +" "+ampm;
			}
		}	
		
		return time;
	}
	
	public static boolean isForOvertime(EmployeeTimeOff off) {
		
		String timeNow = getTime12Format().replace(" AM", "");
		timeNow = timeNow.replace(" PM", "");
		
		String employeeTimeOut = off.getAfternoonOut().replace(" AM", "");
		employeeTimeOut = employeeTimeOut.replace(" PM", "");
		
		int hhInOff = Integer.valueOf(employeeTimeOut.split(":")[0]);
		int mmInOff = Integer.valueOf(employeeTimeOut.split(":")[1]);
		
		int hhInAct = Integer.valueOf(timeNow.split(":")[0]);
		int mmInAct = Integer.valueOf(timeNow.split(":")[1]);
		
		if(hhInAct>hhInOff) {
			return true;
		}else if(hhInAct==hhInOff) {
			if(mmInAct>(off.getTimeExtension()+mmInOff)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static double calculateTime(EmployeeTimeOff off, int mode, String timeIn, String timeOut) {
		
		//this line use in logging in time of employee
		if("IN".equalsIgnoreCase(timeIn) || "OUT".equalsIgnoreCase(timeOut)) {
			return 0;
		}
		
		
		
		if((timeOut!=null && !timeOut.isEmpty())) {
			
			timeIn = timeCorrecting(off, "IN", timeIn, mode);//check in employee time off time
			timeOut = timeCorrecting(off, "OUT", timeOut, mode);//check in employee time off time
			
			timeIn = checkTime(timeIn);
			timeOut = checkTime(timeOut);
			
			String in = timeIn.split(" ")[0];
			double hourIn = Integer.valueOf(in.split(":")[0]);
			double minuteIn = Integer.valueOf(in.split(":")[1]);
			
			String out = timeOut.split(" ")[0];
			double hourOut = Integer.valueOf(out.split(":")[0]);
			double minuteOut = Integer.valueOf(out.split(":")[1]);
			
			double totalIn = hourIn + (minuteIn/60);
			double totalOut = hourOut + (minuteOut/60);
			
			
			
			double totalTime = totalOut - totalIn;
			
			return Numbers.formatDouble(totalTime);
		
		}			
		
		return 0;
	}
	
}
