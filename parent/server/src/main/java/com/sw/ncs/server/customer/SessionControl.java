package com.sw.ncs.server.customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import com.sw.ncs.server.db.AbstractEntityControl;
import com.sw.ncs.server.db.DbSession;
import com.sw.ncs.server.db.EntityValidationException;
import com.sw.ncs.server.db.GridBean;
import com.sw.ncs.server.db.IEntity;
import com.sw.ncs.server.db.QueryParams;

class SessionControl {
	
	private long customerNo;
	private static Map<Long,SessionControl> instances = new HashMap<Long,SessionControl>();
	
	public static SessionControl getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo, new SessionControl(customerNo));
		}
		return instances.get(customerNo);
	}
	
	private SessionControl(long customerNo){
		this.customerNo = customerNo;
	}
	
	synchronized boolean addSession(String sessionId,DbSession session){
		if(validate(sessionId,session)){
			Session appSession = new Session();
			appSession.setId(sessionId);
			session.save(appSession);
			session.flush();
			return true;
		}
		return false;
	}
	
	private boolean validate(String sessionId,DbSession dbSession){
		Query query = dbSession.createQuery("select count(*) from Session where "+
	"identifier.customerNo = :customerNo and identifier.sessionId = :sessionId");
		
		return (long)query.uniqueResult() == 0;
	}
	
}
