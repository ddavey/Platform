package com.sw.ncs.server.customer;

public class RegistrationRequestBean {
	private String company,fName,lName,email,user,pw,cnfmPw,url;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCnfmPw() {
		return cnfmPw;
	}

	public void setCnfmPw(String cnfmPw) {
		this.cnfmPw = cnfmPw;
	}
	
	public Customer getCustomer(){
		Customer customer = new Customer();
		customer.setName(getCompany());
		customer.setPath(getUrl());
		
		return customer;
	}
	
}
