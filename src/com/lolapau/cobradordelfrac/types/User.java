package com.lolapau.cobradordelfrac.types;

import org.json.JSONObject;

public class User {
	private String userName;
	private String id;
	private String password;
	private String email;
	private String name;
	private JSONObject contacts;
	int nContacts;
	
	public User(String userName, String password, String email, String name, JSONObject contacts){
		this.userName = userName;
		this.email = email;
		this.name = name;
		this.contacts = contacts;
		this.password = password;
		nContacts = 0;
	}
	
	public User(){
		contacts = new JSONObject();
		nContacts = 0;
	}
	
	public void addContact(String u_name){
		try{
			contacts.put(u_name,"");
			nContacts++;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getEmail() {
		return email;
	}
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPassword() {
		return password;
	}
	
	public JSONObject getContacts() {
		return contacts;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setContacts(JSONObject contacts) {
		try{
		this.contacts = contacts;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		nContacts = contacts.length();
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
