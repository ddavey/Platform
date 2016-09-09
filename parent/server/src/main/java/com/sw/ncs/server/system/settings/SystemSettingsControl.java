package com.sw.ncs.server.system.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.sw.ncs.server.db.Database;
import com.sw.ncs.server.db.DbSession;

public class SystemSettingsControl {
	private static Map<Long,SystemSettingsControl> instances = new HashMap<Long,SystemSettingsControl>();
	private long customerNo;
	private Map<String,String> values = new HashMap<String,String>();
	
	public static SystemSettingsControl getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo, new SystemSettingsControl(customerNo));
		}
		return instances.get(customerNo);
	}
	
	private SystemSettingsControl(long customerNo){
		this.customerNo = customerNo;
		getCurrentSettings();
	}
	
	private void getCurrentSettings(){
		DbSession session = Database.getInstance().getSession(customerNo);
		Query query = session.createQuery("from SystemSetting");
		List<SystemSetting> settings = query.list();
		
		for(SystemSetting setting : settings){
			values.put(setting.getSettingKey(), setting.getSettingValue());
		}
		
		session.close();
	}
	
	public synchronized String getSetting(String key){
		return values.get(key);
	}
	
	public synchronized void addSetting(String key,String value,DbSession session) throws SystemSettingException{
		SystemSetting setting = new SystemSetting(key, value);
		boolean sessionNull = session == null;
		
		if(sessionNull){
			session = Database.getInstance().getSession(customerNo);
		}
		
		boolean transactionStarted = session.getTransaction()!=null && session.getTransaction().isActive();
		
		if(!transactionStarted){
			session.beginTransaction();
		}
		
		try{
			session.save(setting);
			
			if(sessionNull){
				if(!transactionStarted){
					session.commitTransaction();
				}
			}
		}catch(ConstraintViolationException cve){
			if(sessionNull){
				if(!transactionStarted){
					session.getTransaction().rollback();
				}
			}
			throw new SystemSettingException("Key "+setting.getSettingKey()+" already exists.");
		}
		
		
	}
	
	
}
