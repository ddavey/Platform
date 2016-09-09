package com.sw.ncs.server.controller;

import java.util.List;
import java.util.Map;

public class RequestErrorBean implements RequestResultBean{
	
	private Map<String,String> errors;
	private List<String> valid;
	
	public Map<String, String> getErrors() {
		return errors;
	}

	@Override
	public boolean isError() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public RequestErrorBean(Map<String,String> errors){
		this.errors = errors;
	}
	
	public RequestErrorBean(Map<String,String> errors,List<String> valid){
		this.errors = errors;
		this.valid = valid;
	}

	public List<String> getValid() {
		return valid;
	}

}
