package com.sw.ncs.server.synchronization;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.sw.ncs.server.db.ISystemEntity;

@Entity
@Table(name="Synchronization")
public class SynchronizationEntry implements ISystemEntity{
	@Id
	private String entity;
	private long date;
	private String host;
	
	public String getEntity() {
		return entity;
	}
	
	void setEntity(String entity) {
		this.entity = entity;
	}
	
	public long getDate() {
		return date;
	}
	
	void setDate(long date) {
		this.date = date;
	}
	
	public String getHost() {
		return host;
	}
	
	void setHost(String host) {
		this.host = host;
	}
	
	
}
