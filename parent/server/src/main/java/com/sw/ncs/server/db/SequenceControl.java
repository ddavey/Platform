package com.sw.ncs.server.db;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

import com.sw.ncs.server.customer.Account;

public class SequenceControl {
	private static Map<Long,SequenceControl> instances = new HashMap<Long,SequenceControl>();
	private long customerNo;
	private enum SequenceType {ACCOUNT};
	
	static SequenceControl getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo, new SequenceControl(customerNo));
		}
		return instances.get(customerNo);
	}
	
	private SequenceControl(long customerNo){
		this.customerNo = customerNo;
	}
	
	private String getSequenceCode(Class cls){
	
			if(cls == Account.class) return "ACCT";	
	
		return null;
	}
	
	public synchronized long getNextSequence(IEntity entity,DbSession session){
		String queryString = "from Sequence where id.customerNo = :customerNo and id.type = :type";
		boolean sessionWasNull = (session == null);
		if(sessionWasNull){
			session = Database.getInstance().getSession(customerNo);
		}
		Query query = session.createQuery(queryString);
		query.setLong("customerNo", customerNo);
		query.setString("type", getSequenceCode(entity.getClass()));
		
		Sequence sequence = (Sequence) query.uniqueResult();
		
		if(sessionWasNull){
			session.beginTransaction();
		}
		
		
		if(sequence == null){
			sequence = new Sequence(new SequenceId(1,getSequenceCode(entity.getClass())));
		}else{
			sequence.getId().setCurrentId(sequence.getId().getCurrentId()+1);
		}
		
		session.save(sequence);
		
		if(sessionWasNull){
			session.getTransaction().commit();
		}
		
		return sequence.getId().getCurrentId();
		
	}
}
