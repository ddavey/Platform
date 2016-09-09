package com.sw.ncs.server.customer;

public interface CustomerCreationEventHandler {
	
	public void afterCreate(Customer customer);
}
