package com.sw.ncs.server.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sw.ncs.server.customer.Customer;
import com.sw.ncs.server.customer.CustomerControl;

public class CustomerFilter extends ApplicationFilter implements Filter {
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		String path = getCustomerPath(request);
		
		
		if(path!=null){
			if(reservedPaths.indexOf(path)==-1){
				Customer customer = CustomerControl.getInstance().getCustomer(path);
				
				if(customer == null){
					HttpServletResponse response = (HttpServletResponse) resp;
					response.sendRedirect("register");
				}else{
					chain.doFilter(req, resp);
				}
			}else{
				chain.doFilter(req, resp);
			}
		}else{
			chain.doFilter(req, resp);
		}
		
		
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	

}
