package com.lolapau.cobradordelfrac.parser.json;

import org.json.JSONObject;

import com.lolapau.cobradordelfrac.HomeActivity;
import com.lolapau.cobradordelfrac.types.Debt;
import com.lolapau.cobradordelfrac.types.User;

public class JsonFactory {
    public static JSONObject userToJson(User user, boolean isSignUp){
    	JSONObject json = new JSONObject();
    	JSONObject json2 = new JSONObject();
    	
    	try{
    	json.put("email", user.getEmail());
    	json.put("user", user.getUserName());
    	json.put("pwd", user.getPassword());
    	if(!isSignUp){
    		json2.put("$oid", user.getId());
    		json.put("_id", json2);
    	}
    	if(user.getName() != null)
    		json.put("name", user.getName());
    	json.put("contacts", user.getContacts());
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }
    
    public static JSONObject debtToJson(Debt debt){
    	JSONObject json = new JSONObject();
    	
    	try{
    	json.put("user_debtor_id", debt.getDebtorId());
    	json.put("debtor_name", debt.getDebtorName());
    	json.put("user_creditor_id", HomeActivity.id);
    	json.put("quantity", debt.getQuantity());
    	json.put("comments", debt.getComments());
    	json.put("creditor_name", debt.getCreditorName());
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }
    
}
