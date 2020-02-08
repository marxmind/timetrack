package com.italia.marxmind.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.conf.Conf;
import com.italia.marxmind.controller.Login;

@Named
@ViewScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private String errorMessage;
	private String messages;
	
	@PostConstruct
	public void init() {
		//load config
		Conf.getInstance();
	}
	
	public String getCurrentDate(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		return _date;
	}
	
	public String getMessages() {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		
		switch(_date){
		case "10-18" : {messages="HAPPY BIRTHDAY BOSS!!! We wish you good health and more blessing to come. Stay humble and kind to us..."; break;}
		}
		
		
		
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String back(){
		HttpSession session = SessionBean.getSession();
		session.setAttribute("username", "employeelogin");
		return "timesheetlogin";
	}
	
	//validate login
	public String validateUserNamePassword(){
		boolean valid = Login.validate(name, password);
		System.out.println("Valid: " + valid);
		String result="login";
		if(valid){
			HttpSession session = SessionBean.getSession();
			session.setAttribute("username", name);
			result = "timesheet";
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}
	//logout event, invalidate session
	public String logout(){
		HttpSession session = SessionBean.getSession();
		FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("loginBean");
		setName("");
		setPassword("");
		session.invalidate();
		return "login";
	}
	
}

