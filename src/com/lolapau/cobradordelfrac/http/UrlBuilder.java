package com.lolapau.cobradordelfrac.http;

import com.lolapau.cobradordelfrac.types.Debt;

/*
 * Middleware for the construction of URLs. This way its easier to use REST API.
 * We can receive directly the parameters or the Debt instance for constructing a query to the database.
 * 
 * Author: Lola Priego. me@lolapriego.com
 */

public class UrlBuilder {
	
	// Every query goesto this URL. URL of the app DB
	public final static String BASE_URL = "https://api.mongolab.com/api/1/databases/cobrador_frac_db/collections/";
	
	// Lola Priego API key to access MongoLab services. Not very secure. ToDo: cipher someway
	private final static String URL_API_KEY = "apiKey=bLbJB4v2EbgoIaC5NaUxrOImvRcLT9au";
	
	// It takes a collection and the parameters and give you the right url. For example, for the debts collection and debttor name paramether it will return its debts
	public static String paramsToUrl (String [] params, String collection){
		String path = "%22" + params[0] + "%22%3A%20%20%22" + replaceSpaces(params[1]) + "%22";
		for(int i=2; i<params.length; i= i+2){
			path += ",%20%22" + params[i] + "%22%3A%20%20%22" + replaceSpaces(params[i+1]) + "%22";
		}
		return BASE_URL + collection + "?q=%7B" + path + "%7D&" + URL_API_KEY;
	}
	
	public static String replaceSpaces(String s){
		int count = 0;
		for(int i = 0; i<s.length(); i++)
			if(s.charAt(i) == ' ') count++;
		
		char [] array = new char[s.length() + count * 2];
		int j = 0;
		for(int i = 0; i<s.length(); i++){
			if(s.charAt(i) == ' '){
				array[j++] = '%';
				array[j++] = '2';
				array[j++] = '0';
			}
			else
				array[j++] = s.charAt(i);
		}
		
		return new String(array);
	}
	
	// Method that receive a Debt and construct the url for saving it into the DB 
	public static String debtToQuery (Debt debt){
		String [] params = new String [5];
		params[0] = "user_debtor_id";
		params[2] = "user_creditor_id";
		params[4] = "quantity";
		params[1] = debt.getDebtorId();
		params[3] = debt.getCreditorId();
		
		String path = "%22" + params[0] + "%22%3A%20%20%22" + params[1] + "%22";
		for(int i=2; i<4; i= i+2){
			path += ",%20%22" + params[i] + "%22%3A%20%20%22" + params[i+1] + "%22";
		}
		
		path += ",%20%22" + params[4] + "%22%3A%20%20" + debt.getQuantity();
		return BASE_URL + "debts" + "?q=%7B" + path + "%7D&" + URL_API_KEY;	
	}
	
	// It provide access to one collection
	public static String toUrl(String collection){
		return BASE_URL + collection + "?" + URL_API_KEY;
	}
}
