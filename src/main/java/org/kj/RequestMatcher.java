package org.kj;

import static org.kj.web.service.Constants.MOCK_API_PREFIX;

public class RequestMatcher {

	
	public static final String getResourcePath(String requestPath){
		String apiName ="";
		if(requestPath.startsWith(MOCK_API_PREFIX)){
			apiName = requestPath.substring(MOCK_API_PREFIX.length());
			
		}
		return apiName;
	}
}
