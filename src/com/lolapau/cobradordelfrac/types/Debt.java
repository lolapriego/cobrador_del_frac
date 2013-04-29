package com.lolapau.cobradordelfrac.types;

import android.os.Parcel;
import android.os.Parcelable;

public class Debt implements Parcelable{
	
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

    public int describeContents() {
        return 0;
    }

    /** save object in parcel */
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mDebtor_Id);
        out.writeString(mCreditor_Id);
        out.writeString(mDebtorName);
        out.writeString(mComments);
        out.writeDouble(mQuantity);
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
    }
}
