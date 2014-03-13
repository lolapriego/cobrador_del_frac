package com.lolapau.cobradordelfrac.parser.json;

import java.util.ArrayList;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.types.Debt;
import com.lolapau.cobradordelfrac.types.User;

public class DbHelper {
	public static String getName(String username) throws Exception{
		String [] params = {"user", username};
   	 	String response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
   	 	String names [] = HttpResponseParser.getUserAndId(response);
   	 	if(names[2] == null || response.length() <10) return names[0];
   	 	else return names[2];
	}
	
	public static String getUsername(String name){
		String username = null;
		String [] params = {"name", name};
		try{
			String response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
			if(response.length() < 10) return name;
			username = HttpResponseParser.getUserAndId(response)[0];
		}
		catch(Exception e){
			
		}
		return username;
	}
    public static User getUser(String [] params) throws Exception{
		String response = null;
		User user = null;
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
            if(response.length() >10)
            	user = HttpResponseParser.getUser(response);
         return user;
    }
    
    public static double getTotalDebts(String id) throws Exception{
    	String response = null;
    	String [] params = {"user_debtor_id", id};
    	response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
    	ArrayList<Debt> debtList = HttpResponseParser.getDebts(response);
    	double count = 0;
    	
    	for(int i = 0; i<debtList.size(); i++)
    		count += debtList.get(i).getQuantity();
    	return count;
    }
}

