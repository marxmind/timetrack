package com.italia.marxmind.enm;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */
public enum TimeMode {

	MORNING(1, "MORNING"),
	AFTERNOON(2,"AFTERNOON"),
	LUNCH_BREAK(3,"LUNCH BREAK"),
	BREAK_TIME(4,"BREAK TIME"),
	OVERTIME(5,"OVERTIME"),
	OUT_OF_WORK(6,"OUT OF WORK"),
	SICK_LEAVE(7,"SICK LEAVE"),
	VACATION_LEAVE(8,"VACATION LEAVE"),
	SEMINAR(9,"SEMINAR"),
	TRAINING(10,"TRAINING"),
	CANVASS(11,"CANVASS"),
	MATERNAL_LEAVE(12,"MATERNAL LEAVE"),
	PATERNAL_LEAVE(13,"PATERNAL LEAVE"),
	EMERGENCY_LEAVE(14,"EMERGENCY LEAVE"),
	STRAIGHT_TIME(15,"STRAIGHT TIME"),
	OPEN_TIME(16,"OPEN TIME");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private TimeMode(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(TimeMode type : TimeMode.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return TimeMode.MORNING.getName();
	}
	
	public static TimeMode typeModeName(int id){
		for(TimeMode type : TimeMode.values()){
			if(id==type.getId()){
				return type;
			}
		}
		return TimeMode.MORNING;
	}
	
	public static int typeId(String name){
		for(TimeMode type : TimeMode.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return TimeMode.MORNING.getId();
	}
	
	public static TimeMode typeModeId(String name){
		for(TimeMode type : TimeMode.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type;
			}
		}
		return TimeMode.MORNING;
	}
	
}
