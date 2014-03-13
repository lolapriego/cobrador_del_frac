package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.parser.json.DbHelper;
import com.lolapau.cobradordelfrac.types.User;
import com.lolapau.cobradordelfrac.types.Utility;

public class ViewContactsActivity extends SherlockListActivity {
	private ArrayList<HashMap<String, String>> userList;
	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7aa32d")));
	    actionBar.show();		
		setContentView(R.layout.activity_view_contacts);

		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}

        fillData();	
		Utility u = new Utility();
    	u.setListViewHeightBasedOnChildren(getListView());
	}


	private void fillData(){
	    userList = null;
        Dialog dialog = null;

    	dialog = getUpdatingDialog();
        try {
        	userList = new ArrayList<HashMap<String, String>>();
        	dialog.show();
            String [] params ={"user", HomeActivity.username};
            User me = DbHelper.getUser(params);
            JSONObject contacts = me.getContacts();
            Iterator it = contacts.keys();
            HashMap<String, String> map;
            while(it.hasNext()){
            	map = new HashMap<String, String>();
            	map.put("Name", DbHelper.getName((String)it.next()));
            	userList.add(map);
            }                        
        	ListAdapter adapter = new SimpleAdapter(this, userList,
                    R.layout.user_row,
                    new String[] { "Name"}, new int[] {
                            R.id.name_view});
     
            setListAdapter(adapter);
            
        	dialog.cancel();            
        } catch (Exception e) {
        	e.printStackTrace();
        	dialog.cancel();            
        	getErrorConnectionDialog().show();
        }
	}
	
	// make the menu work
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//actionbar menu
		getSupportMenuInflater().inflate(R.menu.home_activity_menu, menu);
		return true;
	}

	// when a user selects a menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_debtors:
				getUpdatingDialog().show();
				finish();
				return true;
			case R.id.menu_debts:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_DEBTS);
				finish();
				return true;
			case R.id.menu_add_debt:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_NEWD);
				finish();
                return true;
			case R.id.menu_friends:
				return true;
			case R.id.sign_out_menu:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_LOGOUT);
				finish();
				return true;
			case R.id.add_friend_menu:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_NEWC);
				finish();
    			return true;
			default:
				return false;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		dialog.cancel();
	}
	 @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
			dialog = getUpdatingDialog();
			dialog.show();
	        Intent i = new Intent(this, NewDebtActivity.class);
	        i.putExtra("CONTACT", DbHelper.getUsername(userList.get(position).get("Name")));
	        i.putExtra("ISCONTACT", true);
	        startActivityForResult(i, 0);
	    }
    
	private Dialog getUpdatingDialog(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.updating);        	
    	dialogo = builder.create();

    	return dialogo;
	}
	
    private Dialog getErrorConnectionDialog(){
    	Dialog dialogo = null;

	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);
    	
    	builder.setPositiveButton(R.string.try_again, new OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			fillData();
    		}
    	});

    	dialogo = builder.create();

    	return dialogo;
    }
	
}
