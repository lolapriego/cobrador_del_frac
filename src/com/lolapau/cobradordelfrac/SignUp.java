package com.lolapau.cobradordelfrac;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.JsonFactory;

public class SignUp extends SherlockActivity {
	private EditText username;
	private EditText email;
	private EditText password;
	private EditText password2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Sign Up full screen!
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		setContentView(R.layout.activity_sign_up);
	}


	
	public void signUp(View view){
		//name
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.pw_sign_up);
		password2 = (EditText) findViewById(R.id.pw_repeated_sign_up);
				
		if(!isValidEmail(email.getText().toString()))
			Toast.makeText(getApplicationContext(), R.string.incorrect_email, Toast.LENGTH_LONG).show();
		else if(!validatorPw())
     		 Toast.makeText(getApplicationContext(), R.string.invalid_pw, Toast.LENGTH_LONG).show();
		else if(duplicatedUserName(username.getText().toString())){
			return;
		}
		else{
			setResult(RESULT_OK);
			if(saveUser())
				finish();
		}
	}
	
	private boolean saveUser(){
         try {
        	getConnectingDialog().show();
         	JSONObject json = JsonFactory.userToJson(username.getText().toString(), password.getText().toString(), email.getText().toString());
            CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("system.users"), json);
            return true;
         } catch (Exception e) {
        	 getErrorConnectionDialog().show();
        	 return false;
         }
	}
	

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    
	private boolean validatorPw(){
		return password.getText().toString().equals(password2.getText().toString());
	}
	
	private boolean duplicatedUserName(String username){
		String res = null;
		try{
			String [] params = {"user", username};
			res = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
			if(res.length() < 15){
				return false;
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.repeated_usern, Toast.LENGTH_LONG).show();
				return true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			getErrorConnectionDialog().show();
			return true;
		}
	}
    
    private Dialog getErrorConnectionDialog(){
    	Dialog dialog = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {}
    	});

    	dialog = builder.create();
    	return dialog;
    }

    private Dialog getConnectingDialog(){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.connecting);        	
        dialog = builder.create();
        return dialog;
    }
}
