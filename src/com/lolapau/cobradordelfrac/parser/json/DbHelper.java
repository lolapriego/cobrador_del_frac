package com.lolapau.cobradordelfrac.parser.json;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.types.User;

public class DbHelper {
	public static String getName(String username) throws Exception{
		String [] params = {"user", username};
   	 	String response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
   	 	String names [] = HttpResponseParser.getUserAndId(response);
   	 	if(names[2] == null || response.length() <10) return names[0];
   	 	else return names[2];
	}
	
    public static User getUser(String [] params) throws Exception{
		String response = null;
		User user = null;
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
            if(response.length() >10)
            	user = HttpResponseParser.getUser(response);
         return user;
}
}
