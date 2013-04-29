package com.lolapau.cobradordelfrac.http;

import com.lolapau.cobradordelfrac.types.Debt;


public class UrlBuilder {
	public final static String BASE_URL = "https://api.mongolab.com/api/1/databases/cobrador_frac_db/collections/";
	public final static String URL_API_KEY = "apiKey=bLbJB4v2EbgoIaC5NaUxrOImvRcLT9au";
	
	public static String paramsToUrl (String [] params, String collection){
		String path = "%22" + params[0] + "%22%3A%20%20%22" + params[1] + "%22";
		for(int i=2; i<params.length; i= i+2){
			path += ",%20%22" + params[i] + "%22%3A%20%20%22" + params[i+1] + "%22";
		}
		return BASE_URL + collection + "?q=%7B" + path + "%7D&" + URL_API_KEY;
	}
	
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
		return BASE_URL + "debts" + "?q=%7B" + path + "%7D&" + URL_API_KEY;	}
}
