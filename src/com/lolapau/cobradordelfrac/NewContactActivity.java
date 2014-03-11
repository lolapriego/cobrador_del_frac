package com.lolapau.cobradordelfrac;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DbHelper;
import com.lolapau.cobradordelfrac.parser.json.JsonFactory;
import com.lolapau.cobradordelfrac.types.User;

public class NewContactActivity extends SherlockActivity {
	private EditText userName;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7aa32d")));
	    actionBar.show();		

        setContentView(R.layout.new_contact_activity);
        
        //In order to avoid network android.os.Network error for making connections from Main Activity
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }

        setTitle(R.string.add_friend);

        userName = (EditText) findViewById(R.id.input_username_cont);
        email = (EditText) findViewById(R.id.email_input_cont);
        
        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	boolean result = true;
                result = saveContact();
                if(result){
                	finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean saveContact() {
    	Dialog dialog = getUpdatingDialog();
   	 	dialog.show();
   	 
        String emailS = email.getText().toString();
        String usernameS = userName.getText().toString();
        
        String query = emailS.equals("") ? usernameS : emailS;
        String queryKey = emailS.equals("") ? "user" : "email";
        String [] params = {queryKey, query};

        User myUser = null;
        User myContact = null; 
        try {
        	myContact = DbHelper.getUser(params);
        	if (myContact != null){
        		String [] params2 = {"user", HomeActivity.username};
        		myUser = DbHelper.getUser(params2);
            	
        		myUser.addContact(myContact.getUserName());
        		myContact.addContact(HomeActivity.username);
        		
        		JSONObject json = JsonFactory.userToJson(myUser);
        		CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("system.users"), json);            		                         
        		
        		json = JsonFactory.userToJson(myContact);
        		CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("system.users"), json);            		                         
        		
        		dialog.cancel(); 
        		return true;
        }
        else {
        	dialog.cancel();
            getNoUserDialog().show();
            return false;
        }
        }
        catch(JSONException e){
        	e.printStackTrace();
        	return false;
        }
        catch (Exception e) {
       	 	dialog.cancel();
       	 	getErrorConnectionDialog().show();
            return false;
        }
   
    }

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
				finish();
				return true;
			case R.id.menu_debts:
				setResult(HomeActivity.RESULT_GOTO_DEBTS);
				finish();
				return true;
			case R.id.menu_add_debt:
				setResult(HomeActivity.RESULT_GOTO_NEWD);
				finish();
                return true;
			case R.id.menu_friends:
				setResult(HomeActivity.RESULT_GOTO_CONTACTS);
				finish();
				return true;
			case R.id.sign_out_menu:
				setResult(HomeActivity.RESULT_LOGOUT);
				finish();
				return true;
			case R.id.add_friend_menu:
    			return true;
			default:
				return false;
		}
	}
	
    private Dialog getNoUserDialog(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
       	builder.setTitle(R.string.no_user);
    	builder.setMessage(R.string.no_user_message);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});
    	dialogo = builder.create();

    	return dialogo;
    }
    
	private Dialog getUpdatingDialog(){
    	Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
       	builder.setTitle(R.string.updating);        	
    	dialog = builder.create();

    	return dialog;
	}
	
    private Dialog getErrorConnectionDialog(){
    	Dialog dialogo = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});
    	dialogo = builder.create();

    	return dialogo;
    }
    
}
