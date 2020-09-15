package com.italia.marxmind.utils;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

import com.italia.marxmind.conf.Conf;

/**
 * 
 * @author mark italia
 * @since 09/30/2016
 * @version 1.0
 *
 */
public class LogU {
	
	private static volatile LogU log;
	private static StringBuilder build;
	
	public static void add(Double newlog){
		try{add(newlog+"");}catch(Exception e){add("null");}
	}
	
	public static void add(BigDecimal newlog){
		try{add(newlog+"");}catch(Exception e){add("null");}
	}
	
	public static void add(long newlog){
		try{add(newlog+"");}catch(Exception e){add("null");}
	}
	
	public static void add(int newlog){
		try{add(newlog+"");}catch(Exception e){add("null");}
	}
	
	public static void add(String newlog){
		try{build.append(newlog+"\n");}catch(Exception e) {System.out.println("Error logging = " + newlog); log =null; open(); add(newlog);}
	}
	
	private LogU() {}
	
	/**
	 * 
	 * Use this if you are logging line per line
	 * dont forget to close using close() method 
	 * if not it will create error
	 */
	public static void open() {
		if(Conf.getInstance().isLogActive()) {//check if logging is active
			if(log == null) {
				synchronized(LogU.class) {
					if(log==null) {
						log = new LogU();
						System.out.println("Create new instance log");
						build = new StringBuilder();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * You can use this only for one line logging
	 */
	public static void openSave(String val) {
		if(Conf.getInstance().isLogActive()) {//check if logging is active
			open();
			add(val);
			close();
		}
	}
	
	/**
	 * do not close if you are not open() the method
	 */
	public static void close(){
		if(build!=null && build.length()>0) {
		
		try{
			
		String FILE_LOG_NAME = "systemlog";
		String FILE_LOG_TMP_NAME = "tmpsystemlog";
		String EXT = ".log";
		
		
        String logpath = Conf.getInstance().getLogFolder();
        
        String finalFile = logpath + FILE_LOG_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
        String tmpFileName = logpath + FILE_LOG_TMP_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
        
        File originalFile = new File(finalFile);
        
        //check log directory
        File logdirectory = new File(logpath);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
        
        if(!originalFile.exists()){
        	originalFile.createNewFile();
        }
        
        BufferedReader br = new BufferedReader(new FileReader(originalFile));
        
        // Construct the new file that will later be renamed to the original
        // filename.
        File tempFile = new File(tmpFileName);
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        
        String line = null;
        // Read from the original file and write to the new
        while ((line = br.readLine()) != null) {
            pw.println(line);
        }
       
        try {
        pw.println("============================"+ DateUtils.getCurrentDateMMDDYYYYTIME() +"===============================");
        pw.println(build);
        }catch(Exception e) {}
        pw.flush();
        pw.close();
        br.close();

        // Delete the original file
        if (!originalFile.delete()) {
            System.out.println("Could not delete file");
            return;
        }

        // Rename the new file to the filename the original file had.
        if (!tempFile.renameTo(originalFile))
            System.out.println("Could not rename file");
		
		}catch(Exception e){e.getMessage();}
		
		log = null;
		}
	}

	
	
}