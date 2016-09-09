package com.sw.ncs.server.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityValidationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3545621762755191370L;
	private Map<String,String> errorMap;
	private List<String> valid = new ArrayList<String>();
	private String message = "Entity Validation Error: [";
	
	public EntityValidationException(Map<String,String> errorMap){
		this.errorMap = errorMap;
		
		setMessage();
	}
	
	public EntityValidationException(Map<String,String> errorMap,List<String> valid){
		this.errorMap = errorMap;
		this.valid = valid;
		
		setMessage();
	}
	
	public List<String> getValid() {
		return valid;
	}

	private void setMessage(){
		for(Entry<String, String> fieldError : errorMap.entrySet()){
			message += "["+fieldError.getKey()+" = "+fieldError.getValue()+"]";
		}
		message += "]";
	}

	@Override
	public String getMessage() {
		return message ;
	}
	
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	
	
}
