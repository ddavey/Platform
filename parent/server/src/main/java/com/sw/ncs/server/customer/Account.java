package com.sw.ncs.server.customer;

import javax.persistence.Entity;
import com.sw.ncs.server.db.SuperEntity;

@Entity
public class Account extends SuperEntity{
	private String firstName,lastName,email,username,password;
	private int loginCount;

	public int getLoginCount() {
		return loginCount;
	}

	void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	Account(){}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String pw) {
		this.password = pw;
	}
	
}
