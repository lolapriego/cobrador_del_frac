package com.lolapau.cobradordelfrac.types;

import android.os.Parcel;
import android.os.Parcelable;

/* 
 * Class for the object that represents a Debt between two users
 * Implements Parcelable for pasing a debt to Android OS when it is being edited and the user goes out and in the app
 */

public class Debt implements Parcelable{
	
	private String mDebtor_Id;
	private String mCreditor_Id;
	private String mDebtorName;
	private double mQuantity;
	private String mComments;
	private String mCreditorName;
	
	public Debt (){
	}
	
	// Id of MongoDB
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

    public int describeContents() {
        return 0;
    }
    
	public void setCreditorName(String creditorName){
		this.mCreditorName = creditorName;
	}
	
	public String getCreditorName(){
		return this.mCreditorName;
	}


    /* 
     * Save object in parcel
     * See Android documentation for more details. It is done exactly as the documentation explains 
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mDebtor_Id);
        out.writeString(mCreditor_Id);
        out.writeString(mDebtorName);
        out.writeString(mComments);
        out.writeDouble(mQuantity);
        out.writeString(mCreditorName);
    }

    public static final Parcelable.Creator<Debt> CREATOR
            = new Parcelable.Creator<Debt>() {
        public Debt createFromParcel(Parcel in) {
            return new Debt(in);
        }

        public Debt[] newArray(int size) {
            return new Debt[size];
        }
    };

    /** recreate object from parcel */
    private Debt(Parcel in) {
        mDebtor_Id = in.readString();
        mCreditor_Id = in.readString();
        mDebtorName = in.readString();
        mComments = in.readString();
        mQuantity = in.readDouble();
        mCreditorName = in.readString();
    }
}
