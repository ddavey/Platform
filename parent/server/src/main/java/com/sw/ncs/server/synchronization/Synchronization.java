package com.sw.ncs.server.synchronization;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.Query;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.sw.ncs.server.ServerConstants;
import com.sw.ncs.server.customer.Customer;
import com.sw.ncs.server.customer.CustomerControl;
import com.sw.ncs.server.db.Database;
import com.sw.ncs.server.db.DbSession;
import com.sw.ncs.server.system.settings.SystemSettingException;
import com.sw.ncs.server.system.settings.SystemSettingsControl;


public class Synchronization extends SynchronizationConstants{
	private static Map<Long,Synchronization> instances = new HashMap<Long,Synchronization>();
	private static String hostname = "UNKNOWN";
	private long lastTimeStamp = System.currentTimeMillis();
	private long customerNo;
	private static SynchronizationThread syncThread;
	public static enum Entity {CUSTOMER,ACCOUNT};
	public static enum ChangeType {CREATE,UPDATE,DELETE};
	
	private static final Map<Entity,List<AbstractSynchronizationHandler>> syncEvents = 
			new HashMap<Entity,List<AbstractSynchronizationHandler>>();
	

	
	public synchronized void notify(Entity entity,DbSession session){
		Query query = session.createQuery("From SynchronizationEntry where entity = :entity");
		query.setString("entity", entity.toString());
		SynchronizationEntry entry = (SynchronizationEntry) query.uniqueResult();
		if(entry == null){
			entry = new SynchronizationEntry();
			entry.setEntity(entity.toString());
			
		}
		entry.setHost(hostname);
		
		entry.setDate(System.currentTimeMillis());
		session.save(entry);
	}
	
	private Synchronization(long customerNo){
		this.customerNo = customerNo;
		if(hostname.equals("UNKNOWN")){
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				
			}
		}
	}
	
	private static void check(){
		for(Synchronization sync : instances.values()){
			sync.processMessages(sync.list());
		}
		
	}
	
	private void processMessages(List<SynchronizationEntry> messages){
		for(SynchronizationEntry syncMsg : messages){
			List<AbstractSynchronizationHandler> handlers = syncEvents.get(Entity.valueOf(syncMsg.getEntity()));
			for(AbstractSynchronizationHandler handler :handlers){
				handler.update();
			}
			
		}
	}
	
	private List<SynchronizationEntry> list(){
		DbSession session = Database.getInstance().getSession(customerNo);
		Query query = session.createQuery("from SynchronizationEntry where host != :host and date > :date" );
		query.setLong("date", lastTimeStamp);
		query.setString("host", hostname);
		List<SynchronizationEntry> syncEntries = query.list();
		session.close();
		return syncEntries;
	}
	
	public static Synchronization getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo,new Synchronization(customerNo));
		}
		return instances.get(customerNo);
	}
	
	
	public static void shutdown(){
		
		syncThread.interrupt();
		
		
	}
	
	public static void start(){
		
		
		
		DbSession session = Database.getInstance().getSession(Database.DEFAULT_DB);
		List<Customer> customers = CustomerControl.getInstance().list(session);
		session.close();
		getInstance(Database.DEFAULT_DB);
		for(Customer customer : customers){
			getInstance(customer.getId());
		}
		
		syncThread = new SynchronizationThread();
		syncThread.start();
	}
	
	
	public static void addListener(Entity entity,AbstractSynchronizationHandler handler){
		if(!syncEvents.containsKey(entity)){
			syncEvents.put(entity, new ArrayList<AbstractSynchronizationHandler>());
		}
		
		syncEvents.get(entity).add(handler);
		
	}
	
	
	public static class SynchronizationThread extends Thread{

		@Override
		public void run() {
			boolean run = true;
			while(run){
				try {
					Thread.sleep(30000);
					check();
					
				} catch (InterruptedException e) {
					run = false;
					break;
				}
				
			}
		}
		
	}
}
