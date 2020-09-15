package com.italia.marxmind.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import com.italia.marxmind.application.Application;
import com.italia.marxmind.conf.Conf;
import com.italia.marxmind.controller.Login;
import com.italia.marxmind.enm.Module;
import com.italia.marxmind.security.License;
import com.italia.marxmind.sessions.SessionBean;
import com.italia.marxmind.utils.Whitelist;

@Named
@ViewScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private Login login;
	private String keyPress;
	
	private String ui="";
	private String idThemes="nova-colored";
	
	@PostConstruct
	public void init() {
		Conf.getInstance();//load configuration
	}
	
	public String validateUserNamePassword(){
		
		boolean isExpired = License.checkLicenseExpiration(Module.TIME);
		String result="login";
		
		if(isExpired) {
			result = "expired";
		}else {
			Login in = null;
			
			try{in = Login.validUser(Whitelist.remove(getName()), getPassword());}catch(Exception e) {} 
			 
			
			
			if(in != null){
				
				 	HttpSession session = SessionBean.getSession();
			        session.setAttribute("username", name);
					session.setAttribute("userid", in.getId());
					session.setAttribute("ui", getUi());
					session.setAttribute("theme",getIdThemes());
					result = "timesheetdtls";
			}else {
				Application.addMessage(2, "Error", "Wrong Password");
			}
			
		}
		return result;
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
	public Login getLogin() {
		return login;
	}
	public void setLogin(Login login) {
		this.login = login;
	}
	public String getKeyPress() {
		keyPress = "logId";
		return keyPress;
	}
	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public String getIdThemes() {
		return idThemes;
	}

	public void setIdThemes(String idThemes) {
		this.idThemes = idThemes;
	}
	
	
	
}

