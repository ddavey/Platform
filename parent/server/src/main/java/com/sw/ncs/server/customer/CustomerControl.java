package com.sw.ncs.server.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;

import com.sw.ncs.server.db.Database;
import com.sw.ncs.server.db.DbSession;
import com.sw.ncs.server.db.EntityValidationException;
import com.sw.ncs.server.filter.ApplicationFilter;
import com.sw.ncs.server.synchronization.AbstractSynchronizationHandler;
import com.sw.ncs.server.synchronization.Synchronization;
import com.sw.ncs.server.synchronization.Synchronization.Entity;
import com.sw.ncs.server.utils.encryption.KeyGenerator;


public class CustomerControl {
	private static CustomerControl instance;
	private static final Map<Long,Customer> customerIdMap = new HashMap<Long,Customer>();
	private static final Map<String,Customer> customerUrlMap = new HashMap<String,Customer>();
	private static final List<Customer> customers = new ArrayList<Customer>();
	
	public static CustomerControl getInstance(){
		if(instance == null){
			instance = new CustomerControl();
		}
		return instance;
	}
	
	private CustomerControl(){
	}
	
	public void reload(){
		List<Customer> newCustomers = dbList(null);
		newCustomers = newCustomers.subList(customers.size(), newCustomers.size());
		customers.addAll(newCustomers);
		for(Customer customer : newCustomers){
			customerIdMap.put(customer.getId(),customer);
			customerUrlMap.put(customer.getPath(), customer);
		}
	}
	
	public Customer get(long customerNo,DbSession session){
		boolean sessionNull = session == null;
		
		if(customerIdMap.containsKey(customerNo)){
			return customerIdMap.get(customerNo);
		}
		
		if(sessionNull){
			session = Database.getInstance().getSession(-1);
		}
		
		Query query = session.createQuery("from Customer where id = :customerNo");
		query.setLong("customerNo", customerNo);
		
		Customer customer = (Customer) query.uniqueResult();
		
		if(sessionNull){
			session.close();
		}
		
		return customer;
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> list(DbSession session){
		
		if(customerIdMap.isEmpty()){
			Query query = session.createQuery("from Customer");
			List<Customer> customers = dbList(session);
			this.customers.addAll(customers);
			for(Customer customer : customers){
				customerIdMap.put(customer.getId(), customer);
				customerUrlMap.put(customer.getPath(), customer);
			}
			return customers;
		}else{
			return customers;
		}
		
		
		
	}
	
	private List<Customer> dbList(DbSession session){
		boolean sessionNull = session == null;
		
		if(sessionNull){
			session = Database.getInstance().getSession(Database.DEFAULT_DB);
		}
		
		Query query = session.createQuery("from Customer");
		List<Customer> customers = (List<Customer>)query.list();
		
		if(sessionNull){
			session.close();
		}
		
		return customers;
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> list(){
		
		if(customerIdMap.isEmpty()){
			DbSession session = Database.getInstance().getSession(-1);
			List<Customer> customers = (List<Customer>)session.list("Customer", true);
			
			for(Customer customer : customers){
				customerIdMap.put(customer.getId(), customer);
				customerUrlMap.put(customer.getPath(), customer);
			}
			session.close();
			return customers;
		}else{
			return (List<Customer>) customerIdMap.values();
		}
		
		
		
	}
	
	public Customer getCustomer(String path){
		return customerUrlMap.get(path);
	}
	
	public synchronized Customer save(RegistrationRequestBean registration,HttpSession httpSession) throws EntityValidationException{
		Customer customer = new Customer();
		customer.setName(registration.getCompany());
		customer.setPath(registration.getUrl());
		
		DbSession session = Database.getInstance().getSession(-1);
		session.beginTransaction();
		
		EntityValidationException validationException = null;
		AccountControl accountControl = null;
		Account account = null;
		try{
			save(customer,session);
			session.getTransaction().commit();
			session.close();
			session = Database.getInstance().getSession(customer.getId());
			session.beginTransaction();
		}catch(EntityValidationException eve){
			validationException = eve;
		}
		
		try{
			account = new Account();
			account.setFirstName(registration.getfName());
			account.setLastName(registration.getlName());
			account.setEmail(registration.getEmail());
			account.setUsername(registration.getUser());
			account.setPassword(registration.getPw());
			accountControl = AccountControl.getInstance(customer.getId());
			accountControl.save(account,session);
			
			
			
		}catch(EntityValidationException eve){
			if(validationException != null){
				validationException.getErrorMap().putAll(eve.getErrorMap());
				validationException.getValid().addAll(eve.getValid());
			}else{
				validationException = eve;
			}
		}
		
		if(validationException != null){
			if(session.getTransaction()!=null){
				session.getTransaction().rollback();
			}
			throw validationException;
		}
		
		session.commitTransaction();
		session.flush();
		
		
		try {
			accountControl.login(account.getUsername(),registration.getPw(),httpSession,session);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		session.close();
		
		
		return customer;
	}
	
	
	private String generateSerialNo() {
		
		return KeyGenerator.generateKey();
	}

	public synchronized Customer save(Customer customer,DbSession session) throws EntityValidationException{
		if(validate(customer)){
			customer.setSerialNo(generateSerialNo());
			session.save(customer);
			customers.add(customer);
			customerUrlMap.put(customer.getPath(), customer);
			customerIdMap.put(customer.getId(),customer);
			Database.getInstance().getSession(customer.getId()).close();
			Synchronization.getInstance(-1).notify(Entity.CUSTOMER,session);
		}
		return customer;
	}
	
	public boolean validate(Customer customer) throws EntityValidationException{
		
		Map<String,String> errors = new HashMap<String,String>();
		List<String> valid = new ArrayList<String>();
		
		if(customer.getName().length() < 3){
			errors.put("customerName", "Must be minimum 3 Characters.");
		}else{
			valid.add("customerName");
		}
		
		if(customer.getPath().length() < 3){
			errors.put("customerPath", "Must be minimum 2 Characters.");
		} else	if(!checkAvailability(customer.getPath())){
			errors.put("customerPath", "This url is not available. Please try another.");
		}else{
			valid.add("customerName");
		}
		
		if(!errors.isEmpty()){
			throw new EntityValidationException(errors,valid);
		}
		return true;
	}
	
	public synchronized boolean checkAvailability(String path){
		return ApplicationFilter.reservedPaths.indexOf(path)==-1 && !customerUrlMap.containsKey(path);
		
	}
	
	
}
