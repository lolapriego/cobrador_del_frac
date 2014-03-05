package com.lolapau.cobradordelfrac.parser.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HttpResponseParser {
	
   /* Parse username and id of a user from a user get to the database
    * Param: response converted to String
    * Return array with username and id
    */
   public static String[] getUser(String res){
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
}
