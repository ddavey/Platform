package com.sw.ncs.server.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import com.sw.ncs.server.customer.Customer;
import com.sw.ncs.server.customer.CustomerControl;

public class Database {
	private static Database instance;
	private Map<Long,SessionFactory> sessionFactorys = new HashMap<Long,SessionFactory>();
	public static final long DEFAULT_DB = -1;
	private static final String DEFAULT_CUSTOMER_DB_NAME = "customer";
	
	public static Database getInstance(){
		if(instance == null){
			instance = new Database();
		}
		return instance;
	}
	
	private Database(){
		getDatabaseConfig();
	}
	
	private void getDatabaseConfig(){
		try{
			
			setupConfiguration();
			
			}catch(HibernateException he){
				he.printStackTrace();
				System.exit(1);
			}
	}
	
	private void setupConfiguration(){
		Configuration customerConfig = new Configuration().configure("settings/customer.cfg.xml");
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(customerConfig.getProperties());
		sessionFactorys.put(DEFAULT_DB,customerConfig.buildSessionFactory(ssrb.build()));
		DbSession session = new DbSession(sessionFactorys.get(DEFAULT_DB).openSession());
		List<Customer> customers = CustomerControl.getInstance().list(session);
		session.close();
		for(Customer customer : customers){
			setUpCustomerConfiguration(customer);
		}
		
	}
	
	private void setUpCustomerConfiguration(Customer customer){
		Configuration customerConfig = new Configuration().configure("settings/hibernate.cfg.xml");
		Properties properties = customerConfig.getProperties();
		String connectionUrl = properties.getProperty("connection.url");
		connectionUrl = connectionUrl.substring(0, connectionUrl.lastIndexOf("/"))+"/"+DEFAULT_CUSTOMER_DB_NAME+"_"+customer.getId()+"?createDatabaseIfNotExist=true";
		customerConfig.setProperty("hibernate.connection.url", connectionUrl);
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(properties);
		sessionFactorys.put(customer.getId(),customerConfig.buildSessionFactory(ssrb.build()));
		DbSession session = new DbSession(sessionFactorys.get(customer.getId()).openSession());
		session.close();
		
	}
	
	public DbSession getSession(long customerNo){
		return new DbSession(sessionFactorys.get(customerNo).openSession());
	}
}
