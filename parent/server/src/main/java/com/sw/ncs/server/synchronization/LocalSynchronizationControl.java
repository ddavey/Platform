package com.sw.ncs.server.synchronization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class LocalSynchronizationControl {
	private static final ExecutorService execService = Executors.newFixedThreadPool(5);
	private static final Map<Synchronization.Entity,Map<Synchronization.ChangeType,List<AbstractSynchronizationHandler>>> handlers = 
			new HashMap<Synchronization.Entity,Map<Synchronization.ChangeType,List<AbstractSynchronizationHandler>>>();
	private long customerNo;
	private static Map<Long,LocalSynchronizationControl> instances = new HashMap<Long,LocalSynchronizationControl>();
	private Synchronization synchronization;
	
	public void notify(Object object,Synchronization.ChangeType changeType,Synchronization.Entity entity){
		
		List<AbstractSynchronizationHandler> handlerList = handlers.get(entity).get(changeType);
		for(AbstractSynchronizationHandler handler : handlerList){
			switch(changeType){
				case CREATE:handler.afterCreate(object); break;
			}
		}
			Synchronization.getInstance(customerNo).notify(object, changeType, entity);
				
	}
	
	public static LocalSynchronizationControl getInstance(long customerNo){
		if(!instances.containsKey(customerNo)){
			instances.put(customerNo, new LocalSynchronizationControl(customerNo));
		}
		return instances.get(customerNo);
	}
	
	private LocalSynchronizationControl(long customerNo){
		this.customerNo = customerNo;
		this.synchronization = Synchronization.getInstance(customerNo);
	}
	
	
	public static void addListener(Synchronization.Entity type,Synchronization.ChangeType change,AbstractSynchronizationHandler handler){
			Synchronization.addListener(type, change, handler);
		
			if(!handlers.containsKey(type)){
				handlers.put(type, new HashMap<Synchronization.ChangeType,List<AbstractSynchronizationHandler>>());
			}
		
			
				if(!handlers.get(type).containsKey(change)){
					handlers.get(type).put(change, new ArrayList<AbstractSynchronizationHandler>());
				}
				
				handlers.get(type).get(change).add(handler);
		
	}
	
	
	
}
