package com.lolapau.cobradordelfrac;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lolapau.cobradordelfrac.parser.json.DbHelper;

public class Reminder extends BroadcastReceiver {
	public static NotificationManager mNotificationManager = null;
	        
	@Override
	public void onReceive(Context context, Intent intent) {	
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
		// Prepare intent which is triggered if the
		// notification is selected
		Intent resultIntent = new Intent(context, HomeActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);

	    double debts = getDebts();
	    if(debts != -1){
	    	Notification noti = null;
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.android_smoking)
				        .setContentTitle("")
				        .setAutoCancel(true)
				        .setContentText(context.getText(R.string.notification_text) + " " + getDebts() + " " + context.getText(R.string.notification_text2) );
			mBuilder.setContentIntent(pIntent);
				
			noti = mBuilder.build();
		    mNotificationManager.notify(0, noti);
	    }
	 }
	
	private int getDebts(){
        double count;
        try {
        	count = DbHelper.getTotalDebts(HomeActivity.id);
        	return (int) count;
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
            count = -1;
        }
        return (int)count;
	}

}
