package com.sw.ncs.server.utils.encryption;

import com.sw.ncs.server.customer.Customer;
import com.sw.ncs.server.synchronization.AbstractSynchronizationHandler;

public class CustomerEncryptionObserver extends AbstractSynchronizationHandler{

	public void afterCreate(Object object) {
		if(object.getClass() == Customer.class){
			Customer customer = (Customer) object;
			Encryption.addInstance(customer);
		}
	}

}
