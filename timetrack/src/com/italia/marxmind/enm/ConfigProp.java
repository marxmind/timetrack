package com.italia.marxmind.enm;

public enum ConfigProp {

	APP_FOLDER("timetrack"),
	APP_CONF_FOLDER("conf"),
	APP_BACKUP_FOLDER("databasebackup"),
	APP_REPORT_FOLDER("reports"),
	APP_IMG_FOLDER("img"),
	APP_LOG_FOLDER("logs"),
	APP_CONFIG_FILE("app.max"),
	APP_REPORTS_CONFIG_FILE("reports.max"),
	APP_DEFAULT_CONFIG_FILE("default-config.max"),
	SECURITY_ENCRYPTION_FORMAT("utf-8"),
	APP_LICENSE("license.max"),
	APP_LICENSE_CODE("data.max");	
	
	
	private String name;
	
	
	
	private ConfigProp(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
}
