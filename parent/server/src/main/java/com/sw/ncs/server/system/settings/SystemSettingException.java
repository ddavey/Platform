package com.sw.ncs.server.system.settings;

public class SystemSettingException extends Exception{
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

	private String message;

	SystemSettingException(String message){
		this.message = message;
	}
}
