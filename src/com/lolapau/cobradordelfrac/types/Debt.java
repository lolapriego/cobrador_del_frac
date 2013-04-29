package com.lolapau.cobradordelfrac.types;

public class Debt {
	
	private String mDebtor_Id;
	private String mCreditor_Id;
	private String mDebtorName;
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
	
	public void setQuantity(double quantity){
		this.mQuantity = quantity;
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
	
	public void setDebtorName(String debtorName){
		this.mDebtorName = debtorName;
	}
	
	public String getDebtorName(){
		return this.mDebtorName;
	}

}
