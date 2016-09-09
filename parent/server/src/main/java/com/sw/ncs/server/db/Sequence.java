package com.sw.ncs.server.db;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Sequence implements ISystemEntity{
	@EmbeddedId
	protected SequenceId id;

	public SequenceId getId() {
		return id;
	}

	public void setId(SequenceId id) {
		this.id = id;
	}
	
	Sequence(){};
	
	Sequence(SequenceId id){
		this.id = id;
	}
	
	
	
}
