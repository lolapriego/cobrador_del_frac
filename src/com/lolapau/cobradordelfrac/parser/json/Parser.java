package com.lolapau.cobradordelfrac.parser.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.lolapau.cobradordelfrac.types.Debt;
import com.lolapau.cobradordelfrac.types.User;


/**
 * @date April 27, 2013
 * @author Lola Priego (md.priego@gmail.com)
 *
 */
public class Parser {

    public static Debt parseDebt(JSONObject json) throws JSONException {
        
        Debt obj = new Debt();
        if (json.has("user_debtor_id")) {
            obj.setDebtorId(json.getString("user_debtor_id"));
        } 
        if (json.has("user_creditor_id")) {
            obj.setCreditorId(json.getString("user_creditor_id"));
        } 
        if (json.has("quantity")) {
            obj.setQuantity(json.getDouble("quantity"));
        } 
        if (json.has("comments")) {
            obj.setComments(json.getString("comments"));
        } 
        if (json.has("debtor_name")){
        	obj.setDebtorName(json.getString("debtor_name"));
        }
        if (json.has("creditor_name")){
        	obj.setCreditorName(json.getString("creditor_name"));
        }
        
        return obj;
    }
    
    public static User parseUser(JSONObject json) throws JSONException{
    	User user = new User();
    	
    	if(json.has("user"))
    		user.setUserName(json.getString("user"));
    	if(json.has("pwd"))
    		user.setPassword(json.getString("pwd"));
    	if(json.has("email"))
    		user.setEmail(json.getString("email"));
    	if(json.has("name"))
    		user.setName(json.getString("name"));
    	if(json.has("contacts"))
    		user.setContacts(json.getJSONObject("contacts"));
    	if(json.has("_id")){
	    	   json = json.getJSONObject("_id");
	    	   if(json.has("$oid")){
	    		   user.setId(json.getString("$oid"));
	    	   }
	       }
    	
    	return user;
    		
    }
}