package com.sw.ncs.server.filter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public abstract class ApplicationFilter {
	public static final List<String> reservedPaths = Arrays.asList(new String[]{"register","resources"});
	
	String getCustomerPath(HttpServletRequest request){
		String path = request.getServletPath().substring(1);
		if(path.contains("/")){
			path = path.substring(0, path.indexOf("/"));
		}
		
		return path;
	}
}
