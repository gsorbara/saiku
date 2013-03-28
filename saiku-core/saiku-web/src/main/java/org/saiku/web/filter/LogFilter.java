package org.saiku.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.io.IOException;
import java.net.InetAddress;

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

		filterChain.doFilter(request, wrapResponse);	
		
		long elapsedTime =  System.currentTimeMillis() - startTime;
		
		// Check if condition match, in order to print log to kibana
		//
		if( isLogRequired(httpServletRequest, wrapResponse) ){	
			
			MDC.put("request-URI", httpServletRequest.getRequestURL().toString());
			MDC.put("remote-address", httpServletRequest.getRemoteAddr() + "");
			MDC.put("method", httpServletRequest.getMethod() + "");
			//MDC.put("node-name", httpServletRequest.getServerName());
			MDC.put("node-name", InetAddress.getLocalHost().getHostName() + "");
			MDC.put("port-number", Integer.toString(httpServletRequest.getServerPort()));
			MDC.put("action-code", "SERVICE_REQUEST");
			MDC.put("response-status-code", Integer.toString(wrapResponse.getStatus()));
			MDC.put("faodata-request-id", httpServletRequest.getHeader("faodata-request-id") + "");			
			MDC.put("service-elapsed-time", Long.toString(elapsedTime));
			
			String optional = String.format(
					"_CHANNEL=%s, _CLIENT_IP=%s, _QUERY_STRING=%s, _REFERRER=%s",
					channel,
					clientIp,
					queryString, 
					referer);
			logger.info( optional );		
			MDC.clear();		
		}
    }
    
    private boolean isLogRequired(HttpServletRequest request, StatusWrapperServletResponse reponse){ 
    	int responseCode = reponse.getStatus();
    	String requestUrl = request.getRequestURL().toString();
    	
    	// Check if this is sanity check call and response code is 200
    	//
    	if( requestUrl.toLowerCase().matches(".+/sanitycheck") && ( responseCode== 200)){
    		return false;
    	}    	
    	return true;
    }

}