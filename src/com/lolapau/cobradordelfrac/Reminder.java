package com.lolapau.cobradordelfrac;



import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.Parser;
import com.lolapau.cobradordelfrac.types.Debt;

public class Reminder extends BroadcastReceiver {
	public static NotificationManager mNotificationManager = null;
	        

	@Override
	public void onReceive(Context context, Intent intent) {	
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
			// Prepare intent which is triggered if the
			// notification is selected
		    Intent intentTo = new Intent(context, HomeActivity.class);
		    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intentTo, 0);
	    

	    String debts = getDebts();
	    if(debts.length() >2){
	    	Notification noti = null;
	    	if (android.os.Build.VERSION.SDK_INT >= 13) {
	 		    // Build notification
			    // Actions are just fake
				/*	
		    	noti = new Notification.Builder(context)
			        .setContentTitle("El Cobrador del Frac")
			        .setContentText(context.getText(R.string.notification_text) + " " + getDebts())
			        .setSmallIcon(R.drawable.android_smoking)
			        .addAction(0, "View", pIntent).build();
			        */
				}
			else{
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.android_smoking)
				        .setContentTitle("El Cobrador del Frac")
				        .setContentText(context.getText(R.string.notification_text) + " " + getDebts());
				mBuilder.setContentIntent(pIntent);
				
				noti = mBuilder.build();
			}
		    // Hide the notification after its selected
		    //mNotificationManager.notify(0, noti);
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
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = Parser.parseDebt(res.getJSONObject(i));
            	 if(i != res.length() - 1)
            		 debts += debt.getCreditorName() + " una cantidad de " + Double.toString(debt.getQuantity()) + ", ";
            	 else 
            		 debts += debt.getCreditorName() + " una cantidad de " + Double.toString(debt.getQuantity()) + ". ";
            }
            
            
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
        }
        return debts;
        
	}

}
