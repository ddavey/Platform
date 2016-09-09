package com.sw.ncs.server.db;

import java.io.Serializable;

public class SequenceId implements Serializable {

	private static final long serialVersionUID = -5289117727486640239L;
	
	private long currentId;
	private String type;
	
	SequenceId(){}
	
	SequenceId(long currentId,String type){
		this.currentId = currentId;
		this.type = type;
	}
	
	
	public long getCurrentId() {
		return currentId;
	}
	
	public void setCurrentId(long currentId) {
		this.currentId = currentId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
