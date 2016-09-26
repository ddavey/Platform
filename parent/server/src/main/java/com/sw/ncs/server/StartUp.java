package com.sw.ncs.server;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.sw.ncs.server.customer.CustomerSynchronizationHandler;
import com.sw.ncs.server.db.Database;
import com.sw.ncs.server.synchronization.Synchronization;
import com.sw.ncs.server.synchronization.Synchronization.Entity;
import com.sw.ncs.server.utils.encryption.Encryption;

public class StartUp extends HttpServlet{
	
	@Override
	public void destroy() {
		
	}

	private static final long serialVersionUID = 3372205257709960720L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		ServerConstants.webAppName = config.getServletContext().getContextPath().substring(1);
		Database.getInstance();
		Synchronization.addListener(Entity.CUSTOMER, new CustomerSynchronizationHandler());
		Synchronization.start();
		
	}



	

}
