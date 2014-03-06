package com.lolapau.cobradordelfrac.parser.json;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lolapau.cobradordelfrac.HomeActivity;
import com.lolapau.cobradordelfrac.types.Debt;

public class HttpResponseParser {
	
   /* Parse username and id of a user from a user get to the database
    * Param: response converted to String
    * Return array with username and id
    */
   public static String[] getUserAndId(String res) {
	   JSONTokener tokener = new JSONTokener( res );
       String username = null;
       String id = null;

       try{
	   JSONArray array = new JSONArray( tokener );
	   JSONObject json = array.getJSONObject(0);    
	       if (json.has("user")) {
	       		username = json.getString("user");
	       }
	       if(json.has("_id")){
	    	   json = json.getJSONObject("_id");
	    	   if(json.has("$oid")){
	    		   id = json.getString("$oid");
	    	   }
	       }
       }
       catch(Exception e){
    	   e.printStackTrace();
       }
	   String [] r = {username, id};
	   return  r;   
   }
   
   public static String getEmail(String res){
	   JSONTokener tokener = new JSONTokener(res);
       String email = null;

       try{
	   JSONArray array = new JSONArray( tokener );
	   JSONObject json = array.getJSONObject(0);    
	       if (json.has("email")) {
	       		email = json.getString("email");
	       }
       }
       catch(Exception e){
    	   e.printStackTrace();
       }
	   return email;
   }
   
   public static ArrayList<HashMap<String, String>> getDebts(ArrayList<Debt> debts, String res, boolean isHomeActv){  
	   ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();
   		try {
	        JSONTokener tokener = new JSONTokener(res);
	        JSONArray array = new JSONArray(tokener);
	        DebtParser parser = new DebtParser();
	        debts.clear();
	        
	        for(int i = 0; i<array.length(); i++){
	        	 Debt debt = parser.parse(array.getJSONObject(i));
	        	 debts.add(debt);
	        	 
	        	 // TODO: modifed at Debt object what it is consider the "name"
	             HashMap<String, String> map = new HashMap<String, String>();
	             if(isHomeActv)
	            	 map.put(HomeActivity.DEBTOR, debt.getDebtorName());
	             else
	                 map.put("Creditor", debt.getCreditorName());
	             map.put(HomeActivity.QUANTITY, Double.toString(debt.getQuantity()));
	             map.put(HomeActivity.COMMENTS, debt.getComments());
	             
	             debtList.add(map);
	        }
   		}
   		catch(Exception e){
   			e.printStackTrace();
   		}
   		return debtList;
   }
   
}
