package com.sw.ncs.server.customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.sw.ncs.server.db.IEntity;
import com.sw.ncs.server.db.ISystemEntity;


@Entity

public class Customer implements ISystemEntity{
	@Id @GeneratedValue
	private long id;
	private String name;
	private String path;
	private String serialNo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	
}
