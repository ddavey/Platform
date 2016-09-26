package com.sw.ncs.server.customer;

import com.sw.ncs.server.synchronization.AbstractSynchronizationHandler;

public class CustomerSynchronizationHandler extends
		AbstractSynchronizationHandler {

	@Override
	public void update() {
		CustomerControl.getInstance().reload();
	}

}
