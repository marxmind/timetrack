package com.italia.marxmind.database;

import java.sql.Connection;
import java.sql.DriverManager;

import com.italia.marxmind.conf.Conf;
import com.italia.marxmind.security.SecureChar;


public class ConnectDB {

	public static Connection getConnection(){
		Connection conn = null;
		
		try{
			Conf conf = Conf.getInstance();
			Class.forName(conf.getDatabaseDriver());
			String url = conf.getDatabaseUrl() + ":" + conf.getDatabasePort() + "/" +conf.getDatabaseName()+ "?" + conf.getDatabaseSSL();
			String u_name = conf.getDatabaseUserName();
				   
			String pword = conf.getDatabasePassword();
				   
			conn = DriverManager.getConnection(url, u_name, pword);
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Connection c = getConnection();
		System.out.println("Successfully connected" + c.toString());
	}
	
}
