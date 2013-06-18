package org.saiku.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class LogFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger("kibana");

    public void init(FilterConfig filterConfig) throws ServletException {}
    public void destroy() {}

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
    	
    	long startTime = System.currentTimeMillis();
    	HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    	StatusWrapperServletResponse wrapResponse = new StatusWrapperServletResponse((HttpServletResponse)response);
    	String channel = httpServletRequest.getProtocol();
    	String clientIp = httpServletRequest.getRemoteAddr();
    	String queryString = httpServletRequest.getQueryString();
    	String referer = httpServletRequest.getHeader("Referer");
    	String requestId = httpServletRequest.getHeader("faodata-request-id");
   	
    	if (requestId == null)
    		requestId = UUID.randomUUID().toString(); 

    	if (isLogRequired(httpServletRequest, wrapResponse)) {	

			MDC.put("request-URI", httpServletRequest.getRequestURL().toString());
			MDC.put("remote-address", httpServletRequest.getRemoteAddr() + "");
			MDC.put("method", httpServletRequest.getMethod() + "");
			//MDC.put("node-name", httpServletRequest.getServerName());
			MDC.put("node-name", InetAddress.getLocalHost().getHostName() + "");
			MDC.put("port-number", Integer.toString(httpServletRequest.getServerPort()));
			MDC.put("action-code", "SERVICE_REQUEST");
			MDC.put("response-status-code", "0");
			MDC.put("faodata-request-id", requestId + "");			
			MDC.put("service-elapsed-time", "0");

			String optional = String.format(
					"_CHANNEL=%s, _CLIENT_IP=%s, _QUERY_STRING=%s, _REFERRER=%s",
					channel,
					clientIp,
					queryString, 
					referer);
			logger.info( optional );		
			MDC.clear();				
    	}

		filterChain.doFilter(request, wrapResponse);	
		
		long elapsedTime =  System.currentTimeMillis() - startTime;
		
		// Check if condition match, in order to print log to kibana
		//
		if (isLogRequired(httpServletRequest, wrapResponse)) {	
			
			MDC.put("request-URI", httpServletRequest.getRequestURL().toString());
			MDC.put("remote-address", httpServletRequest.getRemoteAddr() + "");
			MDC.put("method", httpServletRequest.getMethod() + "");
			//MDC.put("node-name", httpServletRequest.getServerName());
			MDC.put("node-name", InetAddress.getLocalHost().getHostName() + "");
			MDC.put("port-number", Integer.toString(httpServletRequest.getServerPort()));
			
			if (wrapResponse.getStatus() < 400) {
				MDC.put("action-code", "SERVICE_RESPONSE");
			} else {
				MDC.put("action-code", "SERVICE_ERROR");
			}
			
			MDC.put("response-status-code", Integer.toString(wrapResponse.getStatus()));
			MDC.put("faodata-request-id", requestId + "");			
			MDC.put("service-elapsed-time", Long.toString(elapsedTime));
			
	    	String mdxString = MDC.get("mdx-string");
			
			String optional = String.format(
					"_CHANNEL=%s, _CLIENT_IP=%s, _QUERY_STRING=%s, _REFERRER=%s, _MDXSTRING=%s",
					channel,
					clientIp,
					queryString, 
					referer,
					mdxString);
			
			logger.info( optional );		
			MDC.clear();		
		}
    }
    
    private boolean isLogRequired(HttpServletRequest request, StatusWrapperServletResponse reponse){ 

    	String requestUrl = request.getRequestURL().toString();
    	
    	// Check if this is sanity check call 
    	//
    	if (requestUrl.toLowerCase().matches(".+/sanitycheck"))
    		return false;

    	return true;
    }

}