package com.sw.ncs.server.customer;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Session {
	@Id
	private String id;

	String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	
}
