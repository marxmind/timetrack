package com.italia.marxmind.enm;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 02/08/2020
 *
 */
public enum Status {

	ACTIVE(1,"ACTIVE"),
	IN_ACTIVE(2, "IN ACTIVE"),
	FOR_APPROVAL(3,"FOR APPROVAL"),
	APPROVED(4,"APPROVED"),
	COMPLETED(5,"COMPLETED");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Status(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(Status type : Status.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return Status.ACTIVE.getName();
	}
	public static int typeId(String name){
		for(Status type : Status.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return Status.ACTIVE.getId();
	}
	
}
