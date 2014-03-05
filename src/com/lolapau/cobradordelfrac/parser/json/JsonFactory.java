package com.lolapau.cobradordelfrac.parser.json;

import org.json.JSONObject;

public class JsonFactory {
    public static JSONObject userToJson(String username, String password, String email){
    	JSONObject json = new JSONObject();
    	
    	try{
    	json.put("user", username);
    	json.put("pwd", password);
    	json.put("email", email);
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }
    
}
