package com.sw.ncs.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter extends ApplicationFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		String path = getCustomerPath(request);
		
		if(reservedPaths.indexOf(path)==-1){
			if(request.getSession(false)==null){
				HttpServletResponse response = (HttpServletResponse) resp;
				request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response);
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
