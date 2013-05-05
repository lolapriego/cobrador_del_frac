package com.lolapau.cobradordelfrac;



import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;

public class Reminder extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {		
		// Prepare intent which is triggered if the
	    // notification is selected
	    Intent intentTo = new Intent(context, HomeActivity.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intentTo, 0);
	    
	    String debts = getDebts();
	    if(debts.length() >2){
	
		    // Build notification
		    // Actions are just fake
		    Notification noti = new Notification.Builder(context)
		        .setContentTitle("El Cobrador del Frac")
		        .setContentText("You owe money to: " + getDebts()).setSmallIcon(R.drawable.android_smoking)
		        .addAction(0, "View", pIntent).build();
		    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		    // Hide the notification after its selected
		    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	
		    notificationManager.notify(0, noti);
	    }
	 }
	
	private String getDebts(){
		String response = null;
        String debts = "";

        try {
            String [] params ={"user_debtor_id", HomeActivity.id};
            Log.i("Reminder", HomeActivity.id);
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 if(i != res.length() - 1)
            		 debts += debt.getCreditorName() + ", ";
            	 else 
            		 debts += debt.getCreditorName() + ".";
            }
            
            
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
        }
        return debts;
        
	}

}
