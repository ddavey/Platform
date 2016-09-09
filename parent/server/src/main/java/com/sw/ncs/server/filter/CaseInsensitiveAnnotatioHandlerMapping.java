package com.sw.ncs.server.filter;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

public class CaseInsensitiveAnnotatioHandlerMapping extends DefaultAnnotationHandlerMapping{

	@Override
	protected Object lookupHandler(String urlPath, HttpServletRequest request)
			throws Exception {
		// change path to lower case
		return super.lookupHandler(urlPath.toLowerCase(), request);
	}

	@Override
	protected void registerHandler(String urlPath, Object handler)
			throws BeansException, IllegalStateException {
		// change path to lower case
		super.registerHandler(urlPath.toLowerCase(), handler);
	}


}
