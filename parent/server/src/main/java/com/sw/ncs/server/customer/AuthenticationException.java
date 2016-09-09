package com.sw.ncs.server.customer;

public class AuthenticationException extends Exception{
	private static final long serialVersionUID = 134839506257311310L;
	private long customerNo;
	private String login;

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "Login failed for CustomerNo:"+customerNo+" for user:"+login;
	}
	
	AuthenticationException(long customerNo,String login){
		this.customerNo = customerNo;
		this.login = login;
	}

}
