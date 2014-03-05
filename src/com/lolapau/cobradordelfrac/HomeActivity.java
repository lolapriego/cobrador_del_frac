package com.lolapau.cobradordelfrac;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;
import com.lolapau.cobradordelfrac.types.Typefaces;

public class HomeActivity extends SherlockListActivity {

	public static String id;
	public static String username;
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    public static final String DEBTOR = "Debtor";
    public static final String QUANTITY = "Quantity";
    public static final String COMMENTS = "Comments";

    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DEBES = Menu.FIRST + 1;
    private static final int DEBO = Menu.FIRST + 2;
    private static final int DELETE_ID = Menu.FIRST + 3;
    
    private int position;
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences storage = getSharedPreferences(Login.USER_ID, 0);
		id = storage.getString("u_id", "");
		username = storage.getString("u_name", "");

		if(id.length() == 0){
			Intent intent = new Intent(this, Login.class);
		    startActivity(intent);
		    finish();
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
		
		setContentView(R.layout.debt_list);
		setTitle(R.string.title_activity_home);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
        fillData();
        registerForContextMenu(getListView());
        
        if(Reminder.mNotificationManager != null){
        	Reminder.mNotificationManager.cancel(0);
        }
        
        // This set up a daily reminder of your debts
        else{        
        Intent myIntent = new Intent(this , Reminder.class);     
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

       alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60*24*60*1000 , pendingIntent);  //set repeating every 24 hours
        }	
       }
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        
        MenuItem debes = menu.add(0, DEBES,0, R.string.title_activity_home);
        MenuItem debo = menu.add(0, DEBO,1, R.string.title_activity_debts);
        debes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        debo.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
		return true;
	}

	private void fillData(){
		Dialog dialog = null;
		String response = null;
		ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();
        
    	dialog = getUpdatingDialog();
        try {
        	dialog.show();
            String [] params ={"user_creditor_id", id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            mDebtList.clear();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put(DEBTOR, debt.getDebtorName());
                 map.put(QUANTITY, Double.toString(debt.getQuantity()));
                 map.put(COMMENTS, debt.getComments());
                 
                 debtList.add(map);
            }
                        
        	ListAdapter adapter = new SimpleAdapter(this, debtList,
                    R.layout.debt_row,
                    new String[] { DEBTOR, QUANTITY, COMMENTS }, new int[] {
                            R.id.debtor, R.id.quantity, R.id.comments });
        	
        	dialog.cancel();
     
            setListAdapter(adapter);
            
            
        } catch (Exception e) {
        	dialog.cancel();
        	getErrorConnectionDialog().show();
            Log.e(Login.TAG, e.toString());
        }
		
	}
	
	

	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createDebt();
                return true;
            case DEBES: return true;
            case DEBO:  Intent i = new Intent(this, DebtsActivity.class);
            			startActivity(i);
            			return true;

        }

        return super.onMenuItemSelected(featureId, item);
    }
    

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                position = info.position;
                getDeleteDialog().show();
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createDebt() {
        Intent i = new Intent(this, DebtEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        Intent i = new Intent(this, DebtEdit.class);
        i.putExtra("DEBT", mDebtList.get(position));
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
	
    private void deleteDebt(Debt debt){
        try {        	
        	Log.i(Login.TAG, UrlBuilder.debtToQuery(debt));
        	JSONObject json = new JSONObject();
            String res = CustomHttpClient.executeHttpPut(UrlBuilder.debtToQuery(debt), json);
            Log.i(Login.TAG, res);
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
            e.printStackTrace();
        }
        finally{
        	fillData();
        }
    	
    }
    
    private Dialog getUpdatingDialog(){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.updating);        	
        dialog = builder.create();
        return dialog;
    }
    

    private Dialog getDeleteDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.menu_delete);
    	builder.setMessage(R.string.message_confirm);
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
                deleteDebt(mDebtList.get(position));
    			dialog.cancel();
    		}
    	});
    	
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.cancel();
    		}
    	});

    	return builder.create();
    }
    
	
    private Dialog getErrorConnectionDialog(){
    	Dialog dialog = null;

	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);
    	
    	builder.setPositiveButton(R.string.try_again, new OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			fillData();
    		}
    	});

    	dialog = builder.create();
    	return dialog;
    }

}
