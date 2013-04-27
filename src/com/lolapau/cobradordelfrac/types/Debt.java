package com.lolapau.cobradordelfrac.types;

public class Debt {
	
	private String debtor_Id;
	private String creditor_Id;
	private double quantity;
	private String comments;
	
	public Debt (){
	}
	
	public void setDebtorId (String id){
		this.debtor_Id = id;
	}
	
	public String getDebtorId(){
		return debtor_Id;
	}
	
	public void setCreditorId(String id){
		this.creditor_Id = id;
	}
	
	public String getCreditorId (){
		return creditor_Id;
	}
	
	public void setQuantity(double quantity){
		this.quantity = quantity;
	}
	
	public double getQuantity (){
		return quantity;
	}
	
	public void setComments (String comments){
		this.comments = comments;
	}
	
	public String getComments (){
		return comments;
	}

}
