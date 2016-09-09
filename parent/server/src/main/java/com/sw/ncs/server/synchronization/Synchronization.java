package com.sw.ncs.server.synchronization;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	private String databaseId;
	private long customerNo;
	private static final String HOST_QUEUE_NAME_SUFFIX = "-HOSTS";
	private BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJYNTDE6UUO5XSJHQ", "Eo3ccCMqWyNAf0RKvMz1PVZEDVYEdqfQg5tTyZQK");
	private AmazonSQS sqs = new AmazonSQSClient(credentials);
	private Region region = Region.getRegion(Regions.US_WEST_2);
	private String loadBalancedHostsQueueUrl;
	private final List<String> loadBalancedHosts = new ArrayList<String>();
	private String hostname = "UNKNOWN";
	private static SynchronizationThread syncThread;
	public static enum Entity {CUSTOMER,ACCOUNT};
	public static enum ChangeType {CREATE,UPDATE,DELETE};
	private static final String ATTRIBUTE_ENTITY = "ENTITY";
	private static final String ATTRIBUTE_UPDATE_TYPE = "UPDATE_TYPE";
	private static final String ATTRIBUTE_UPDATED_ID = "ENTITY_ID";
	
	private static final Map<Entity,Map<ChangeType,List<AbstractSynchronizationHandler>>> syncEvents = 
			new HashMap<Entity,Map<ChangeType,List<AbstractSynchronizationHandler>>>();
	
	public void notify(Object object,Synchronization.ChangeType changeType,Synchronization.Entity entity){
		
		List<AbstractSynchronizationHandler> handlerList = syncEvents.get(entity).get(changeType);
		for(AbstractSynchronizationHandler handler : handlerList){
			switch(changeType){
				case CREATE:handler.afterCreate(object); break;
			}
		}
		
		notifyLoadBalancedMembers(object,changeType,entity);
				
	}
	
	private void notifyLoadBalancedMembers(Object object,Synchronization.ChangeType changeType,Synchronization.Entity entity){
		
		Map<String,MessageAttributeValue> attributes = new HashMap<String,MessageAttributeValue>();
		MessageAttributeValue attributeValueEntity = new MessageAttributeValue();
		attributeValueEntity.setStringValue(entity.toString());
		MessageAttributeValue attributeValueUpdateType = new MessageAttributeValue();
		MessageAttributeValue attributeValueId = new MessageAttributeValue();
		attributeValueUpdateType.setStringValue(changeType.toString());
		attributeValueId.setStringValue(Long.toString(getEntityId(object)));
		attributes.put(ATTRIBUTE_ENTITY, attributeValueEntity);
		attributes.put(ATTRIBUTE_UPDATE_TYPE, attributeValueUpdateType);
		attributes.put(ATTRIBUTE_UPDATED_ID, attributeValueId);
		
		SendMessageRequest sendRequest;
		
		for(String host : loadBalancedHosts){
			sendRequest = new SendMessageRequest();
			sendRequest.setQueueUrl(databaseId+"-"+hostname);
			
			sendRequest.setMessageAttributes(attributes);
			sqs.sendMessage(loadBalancedHostsQueueUrl,hostname );
		}
	}
	
	private long getEntityId(Object object){
		if(object.getClass() == Customer.class){
			return ((Customer)object).getId();
		}else{
			return -1;
		}
	}
	
	public static Synchronization getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo,new Synchronization(customerNo));
		}
		return instances.get(customerNo);
	}
	
	private Synchronization(long customerNo){	
		this.customerNo = customerNo;
		populateDatabaseId();
		
		
		sqs.setRegion(region);
		registerHost();
	}
	
	public static void shutdown(){
		
		syncThread.interrupt();
		
		for(Synchronization sync : instances.values()){
			System.out.println("Shutting down sqs for customer:"+sync.customerNo);
			sync.sqs.shutdown();
		}
	}
	
	public static void start(){
		
		
		
		DbSession session = Database.getInstance().getSession(Database.DEFAULT_DB);
		List<Customer> customers = CustomerControl.getInstance().list(session);
		session.close();
		for(Customer customer : customers){
			getInstance(customer.getId());
		}
		
		syncThread = new SynchronizationThread();
		syncThread.start();
	}
	
	private void populateDatabaseId(){
		DbSession session;
		
			session = Database.getInstance().getSession(customerNo);
			SystemSettingsControl settingsControl = SystemSettingsControl.getInstance(customerNo);
			databaseId = settingsControl.getSetting(SYSTEM_SETTING_DATABASE_ID_KEY_NAME);
			if(databaseId == null){
				try {
					settingsControl.addSetting(SYSTEM_SETTING_DATABASE_ID_KEY_NAME, UUID.randomUUID().toString().replaceAll("-", ""), session);
					session.getTransaction().commit();
				} catch (SystemSettingException e) {
					databaseId = settingsControl.getSetting(SYSTEM_SETTING_DATABASE_ID_KEY_NAME);
				}
			}
			session.close();

	} 
	
	private void registerHost(){
		ListQueuesRequest request = new ListQueuesRequest();
		request.withQueueNamePrefix(databaseId + HOST_QUEUE_NAME_SUFFIX);
		ListQueuesResult queues = sqs.listQueues(request);
		List<String> urls = queues.getQueueUrls();
		if(urls.isEmpty()){
			CreateQueueRequest createRequest = new CreateQueueRequest(databaseId + HOST_QUEUE_NAME_SUFFIX);
			loadBalancedHostsQueueUrl = sqs.createQueue(createRequest).getQueueUrl();
		}else{
			loadBalancedHostsQueueUrl = urls.get(0);
		}
		
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(loadBalancedHostsQueueUrl);
		receiveMessageRequest.setMaxNumberOfMessages(10);;
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		sqs.sendMessage(loadBalancedHostsQueueUrl,databaseId+'-'+hostname );
		List<String> currentHosts = new ArrayList<String>();
		for(Message message : messages){
			if(message.getBody().equals(hostname)){
				DeleteMessageRequest dmr = new DeleteMessageRequest(loadBalancedHostsQueueUrl, message.getReceiptHandle());
				sqs.deleteMessage(loadBalancedHostsQueueUrl, message.getReceiptHandle());
			}else{
				currentHosts.add(message.getBody());
			}
		}
		
		
		loadBalancedHosts.retainAll(currentHosts);
		addOwnQueue();
		
		
	}
	
	private void addOwnQueue(){
		ListQueuesRequest request = new ListQueuesRequest();
		request.withQueueNamePrefix(databaseId+"-"+hostname);
		ListQueuesResult queuesResult = sqs.listQueues(request);
		if(queuesResult.getQueueUrls().isEmpty()){
			sqs.createQueue(databaseId+"-"+hostname);
		}
		
	}
	
	public static void addListener(Entity entity,ChangeType changeType,AbstractSynchronizationHandler handler){
		if(!syncEvents.containsKey(entity)){
			syncEvents.put(entity, new HashMap<ChangeType,List<AbstractSynchronizationHandler>>());
		}
		
		if(!syncEvents.get(entity).containsKey(changeType)){
			syncEvents.get(entity).put(changeType,new ArrayList<AbstractSynchronizationHandler>());
		}
		
		if(!syncEvents.get(entity).get(changeType).contains(handler)){
			syncEvents.get(entity).get(changeType).add(handler);
		}
		
		
	}
	
	private void checkSynchronization(){
		for(Synchronization sync : instances.values()){
			
		}
	}
	
	public static class SynchronizationThread extends Thread{

		@Override
		public void run() {
			boolean run = true;
			while(run){
				try {
					Thread.sleep(30000);
					for(Synchronization sync : instances.values()){
						sync.registerHost();
					}
					
				} catch (InterruptedException e) {
					run = false;
					break;
				}
				
			}
		}
		
	}
}
