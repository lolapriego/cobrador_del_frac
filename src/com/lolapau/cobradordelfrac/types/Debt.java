package com.lolapau.cobradordelfrac.types;

public class Debt {
	
	private String mDebtor_Id;
	private String mCreditor_Id;
	private double mQuantity;
	private String mComments;
	
	public Debt (){
	}
	
	public void setDebtorId (String id){
		this.mDebtor_Id = id;
	}
	
	public String getDebtorId(){
		return mDebtor_Id;
	}
	
	public void setCreditorId(String id){
		this.mCreditor_Id = id;
	}
	
	public String getCreditorId (){
		return mCreditor_Id;
	}
	
	public void setQuantity(String quantity){
		this.mQuantity = Double.parseDouble(quantity);
	}
	
	public double getQuantity (){
		return mQuantity;
	}
	
	public void setComments (String comments){
		this.mComments = comments;
	}
	
	public String getComments (){
		return mComments;
	}

}
