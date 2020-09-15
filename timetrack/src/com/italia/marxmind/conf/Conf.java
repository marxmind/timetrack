package com.italia.marxmind.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.italia.marxmind.enm.ConfigProp;
import com.italia.marxmind.security.SecureChar;

public class Conf {

	private static volatile Conf conf;
	private static String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private static String SEPERATOR = File.separator;
	
	private String databaseName;
	private String databasePort;
	private String databaseUrl;
	private String databaseDriver;
	private String databaseSSL;
	private String databaseUserName;
	private String databasePassword;
	
	private String logFolder;
	private String imageFolder;
	private String reportFolder;
	private boolean logActive;
	
	private String licenseCode;
	private String dateFile;
	private String licenseFile;
	
	public boolean isLogActive() {
		return logActive;
	}

	public void setLogActive(boolean logActive) {
		this.logActive = logActive;
	}

	private Conf() {}
	
	public static Conf getInstance() {
		if(conf == null) {
			synchronized(Conf.class) {
				if(conf==null) {
					conf = new Conf();
					System.out.println("Create new instance");
					conf.createAppLocationIfNotExistAndLoadDefaultConf();
					conf.readConf();
				}
			}
		}
		return conf;
	}
	
	
	private void createAppLocationIfNotExistAndLoadDefaultConf() {
		
			File appFolder = new File(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName());
			if(!appFolder.isDirectory()) {
				appFolder.mkdir();//primary folder
				System.out.println("Creating app folder");
			}	
			//creating conf folder under timetrack folder
			String cFolder = PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_CONF_FOLDER.getName();
			File configFolder = new File(cFolder);
			if(!configFolder.isDirectory()) {
				configFolder.mkdir();
				System.out.println("Creating config folder");
				createConfFile(cFolder + SEPERATOR);
				createLicenseFile(cFolder + SEPERATOR);
				createDataLicenseFile(cFolder + SEPERATOR);
			}
			
			File backupFolder = new File(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_BACKUP_FOLDER.getName());
			if(!backupFolder.isDirectory()) {
				backupFolder.mkdir();
				System.out.println("Creating backup folder");
			}
			
			File reportFolder = new File(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_REPORT_FOLDER.getName());
			if(!reportFolder.isDirectory()) {
				reportFolder.mkdir();
				System.out.println("Creating report folder");
			}
			
			File imgFolder = new File(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_IMG_FOLDER.getName());
			if(!imgFolder.isDirectory()) {
				imgFolder.mkdir();
				System.out.println("Creating img folder");
			}
			
			File logFolder = new File(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_LOG_FOLDER.getName());
			if(!logFolder.isDirectory()) {
				logFolder.mkdir();
				System.out.println("Creating log folder");
			}
		
		
	}
	
	private void createConfFile(String folder) {
		try {
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String confile = externalContext.getRealPath("") + SEPERATOR + "resources" + SEPERATOR + "conf" + SEPERATOR + ConfigProp.APP_DEFAULT_CONFIG_FILE.getName();
			
			BufferedReader br = new BufferedReader(new FileReader(new File(confile)));
			
			PrintWriter pw = new PrintWriter(new FileWriter(folder + ConfigProp.APP_CONFIG_FILE.getName()));
			String line = null;
			while((line = br.readLine()) !=null ) {
				pw.println(line);
			}
			
			pw.flush();
			pw.close();
			br.close();
		}catch(IOException ioe) {}
	}
	
	private void createLicenseFile(String folder) {
		try {
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String confile = externalContext.getRealPath("") + SEPERATOR + "resources" + SEPERATOR + "conf" + SEPERATOR + ConfigProp.APP_LICENSE.getName();
			
			BufferedReader br = new BufferedReader(new FileReader(new File(confile)));
			
			PrintWriter pw = new PrintWriter(new FileWriter(folder + ConfigProp.APP_LICENSE.getName()));
			String line = null;
			while((line = br.readLine()) !=null ) {
				pw.println(line);
			}
			
			pw.flush();
			pw.close();
			br.close();
		}catch(IOException ioe) {}
	}
	
	private void createDataLicenseFile(String folder) {
		try {
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String confile = externalContext.getRealPath("") + SEPERATOR + "resources" + SEPERATOR + "conf" + SEPERATOR + ConfigProp.APP_LICENSE_CODE.getName();
			
			BufferedReader br = new BufferedReader(new FileReader(new File(confile)));
			
			PrintWriter pw = new PrintWriter(new FileWriter(folder + ConfigProp.APP_LICENSE_CODE.getName()));
			String line = null;
			while((line = br.readLine()) !=null ) {
				pw.println(line);
			}
			
			pw.flush();
			pw.close();
			br.close();
		}catch(IOException ioe) {}
	}
	
	private void readConf() {
		try {
			Properties prop = new Properties();
			String confFile = PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_CONF_FOLDER.getName() + SEPERATOR + ConfigProp.APP_CONFIG_FILE.getName();
			prop.load(new FileInputStream(new File(confFile)));
			
			String u_name = SecureChar.decode(prop.getProperty("APP_DATABASE_UNAME"));
			   u_name = u_name.replaceAll("mark", "");
			   u_name = u_name.replaceAll("rivera", "");
			   u_name = u_name.replaceAll("italia", "");
			String pword =  SecureChar.decode(prop.getProperty("APP_DATABASE_PWD"));
			   pword = pword.replaceAll("mark", "");
			   pword = pword.replaceAll("rivera", "");
			   pword = pword.replaceAll("italia", "");   
			conf.setDatabaseName(prop.getProperty("APP_DATABASE_NAME"));
			conf.setDatabaseDriver(prop.getProperty("APP_DATABASE_DRIVER"));
			conf.setDatabaseUrl(prop.getProperty("APP_DATABASE_URL"));
			conf.setDatabasePort(prop.getProperty("APP_DATABASE_PORT"));
			conf.setDatabaseSSL(prop.getProperty("APP_DATABASE_SSL"));
			conf.setDatabaseUserName(u_name);
			conf.setDatabasePassword(pword);
			
			conf.setLogActive(prop.getProperty("APP_LOG_OPEN").equalsIgnoreCase("yes")? true : false);
			conf.setLogFolder(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_LOG_FOLDER.getName() + SEPERATOR);
			conf.setImageFolder(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_IMG_FOLDER.getName() + SEPERATOR);
			conf.setReportFolder(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_REPORT_FOLDER.getName() + SEPERATOR);
			
			conf.setLicenseCode(prop.getProperty("APP_LICENSE"));
			conf.setDateFile(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_CONF_FOLDER.getName() + SEPERATOR + ConfigProp.APP_LICENSE_CODE.getName());
			conf.setLicenseFile(PRIMARY_DRIVE + SEPERATOR + ConfigProp.APP_FOLDER.getName() + SEPERATOR + ConfigProp.APP_CONF_FOLDER.getName() + SEPERATOR + ConfigProp.APP_LICENSE.getName());
			
		}catch(Exception e) {}
	}
	
	public static void main(String[] args) {
		Conf.getInstance();
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}

	public String getDatabaseSSL() {
		return databaseSSL;
	}

	public void setDatabaseSSL(String databaseSSL) {
		this.databaseSSL = databaseSSL;
	}

	public String getDatabaseUserName() {
		return databaseUserName;
	}

	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getLogFolder() {
		return logFolder;
	}

	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}

	public String getReportFolder() {
		return reportFolder;
	}

	public void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	public String getDateFile() {
		return dateFile;
	}

	public void setDateFile(String dateFile) {
		this.dateFile = dateFile;
	}

	public String getLicenseFile() {
		return licenseFile;
	}

	public void setLicenseFile(String licenseFile) {
		this.licenseFile = licenseFile;
	}
	
	
}
