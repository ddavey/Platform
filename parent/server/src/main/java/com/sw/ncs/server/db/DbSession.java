package com.sw.ncs.server.db;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.Transaction;

import com.sw.ncs.server.customer.Customer;

public class DbSession {
	private Session session;
	private long customerNo;
	
	DbSession(Session session){
		this.session = session;
	}
	
	@SuppressWarnings({"rawtypes" })
	public List list(String table,boolean closeOnCompletion){
		
		Query query = null;
		
		if(customerNo > -1){
			query = session.createQuery("from "+table+" where identity.customerNo = :customerNo");
			query.setLong("customerNo", customerNo);
		}else{
			query = session.createQuery("from "+table);
		}
		
		List list = query.list();
		close();
		
		return list;
	}
	
	
	public void close(){
		session.close();
	}
	
	public Transaction beginTransaction(){
		return session.beginTransaction();
	}
	
	public void commitTransaction(){
		session.getTransaction().commit();
	}
	
	public Transaction getTransaction(){
		return session.getTransaction();
	}
	
	public Serializable save(IEntity entity){
		
		return session.save(entity);
	}
	
	public Serializable save(ISystemEntity entity){
		
		return session.save(entity);
	}
	
public Serializable save(com.sw.ncs.server.customer.Session appSession){
		
		return session.save(appSession);
	}

	public Query createQuery(String queryString){
		return session.createQuery(queryString);
	}
	
	public void flush(){
		session.flush();
	}
	
}
