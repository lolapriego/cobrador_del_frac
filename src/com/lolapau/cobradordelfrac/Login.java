package com.lolapau.cobradordelfrac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DbHelper;
import com.lolapau.cobradordelfrac.parser.json.HttpResponseParser;
import com.lolapau.cobradordelfrac.types.User;




public class Login extends SherlockActivity {	
	public static final String TAG = "MyActivity";
	public static final String USER_ID = "User_id";
	
	private EditText mUsername;
	private EditText mPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Full screen for the login view
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
        mUsername = (EditText) findViewById(R.id.et_un);
        mPwd = (EditText) findViewById(R.id.et_pw);
                        
		Button btnLogin = (Button) findViewById(R.id.bt_login);	
		btnLogin.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View view){
				String res = null;
	        	 Dialog connecting = getDialogConnecting();
		         try {
		        	 connecting.show();

		        	String [] params = {"user", mUsername.getText().toString(), "pwd", mPwd.getText().toString()};		        	
		            res = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users")); 		 
		            
		            if(res.length() > 20){
		            	 goTo(HttpResponseParser.getUserAndId(res));
		             }
		             else{		    
		            	connecting.hide();
		            	Toast toast1 = Toast.makeText(getApplicationContext(), R.string.message_incorrect, Toast.LENGTH_SHORT);
		            	toast1.show();
		            	toast1.setDuration(Toast.LENGTH_LONG);
		             }
		         } catch (Exception e) {
		        	 getDialogErrorConnection().show();
		         }           
		         connecting.cancel();
			}			
		});
		
        Button btnForgot = (Button) findViewById(R.id.btn_forgot_pw);	
        btnForgot.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View view){
				forgot();
			}
		});
	}
	
	private void forgot(){
		try{
        	String [] params = {"user", mUsername.getText().toString()};
        	User u = DbHelper.getUser(params);
        	if(u == null){
        		Toast.makeText(this, R.string.no_user_message, Toast.LENGTH_LONG).show();
        	}
        	else{
        		BackgroundMail bm = new BackgroundMail(this);
        		bm.setGmailUserName("cobradordelffrac@gmail.com");
                bm.setGmailPassword("3v2lxjj017c");
		        bm.setMailTo(u.getEmail());
		        bm.setFormSubject(getString(R.string.your_password));
		        bm.setFormBody(getString(R.string.email_body) + " \n" + u.getPassword());
		        bm.send();
        	}
        }
        catch(Exception e){
        	getDialogErrorConnection().show();
        }
	}
	

    @Override
    protected void onResume() {
        super.onResume();
    }
	

	private void goTo(String [] params){
		// Save the userName and id, so user do not have to login every time he opens the app
	   	 SharedPreferences u_id = getSharedPreferences(USER_ID, 0);
	   	 SharedPreferences.Editor editor = u_id.edit();
	   	 editor.putString("u_id", params[1]);
	   	 editor.putString("u_name", params[0]);
	   	 editor.commit();
	   	 
	   	 Intent intent = new Intent(this, HomeActivity.class);
		 startActivity(intent);
		 finish();
	}
	
	public void signUp(View view){
		Intent intent = new Intent(this, SignUp.class);
		startActivityForResult(intent, 0);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
        	getDialogSingUp().show();
        }
    }
        
    private Dialog getDialogErrorConnection(){
    	Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});

    	dialog = builder.create();
    	return dialog;
    }


    private Dialog getDialogConnecting(){
        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.connecting);        	
        dialog = builder.create();
        return dialog;
    }
    
    private Dialog getDialogSingUp(){
    	Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.congrats);
    	builder.setMessage(R.string.sign_up_finished);	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});

    	dialog = builder.create();
    	return dialog;
    }
}
