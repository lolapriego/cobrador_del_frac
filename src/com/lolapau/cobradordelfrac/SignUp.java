package com.lolapau.cobradordelfrac;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;

public class SignUp extends Activity {
	private EditText username;
	private EditText email;
	private EditText password;
	private EditText password2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
	}

	private boolean validator(){
		boolean returned = true;
		
		if(!password.getText().toString().equals(password2.getText().toString())) returned = false;
		if(!isValidEmail(email.getText().toString())) returned = false;
		
		Log.i("Loggg", returned + "");
		
		return returned;
	}
	
	public void signUp(View view){
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.pw);
		password2 = (EditText) findViewById(R.id.pw_repeated);
		
		if(validator()){
			setResult(RESULT_OK);
			saveUser();
			finish();
		}
		else{
          	 Toast toast2 = 
       			 Toast.makeText(getApplicationContext(),
       			 R.string.incorrect_sign_up, Toast.LENGTH_SHORT);
          	 toast2.show();
		}
	}
	
	
	private void saveUser(){
        String response = null;
         try {
         	JSONObject json = userToJson();
             response = CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("system.users"), json);
             		             
             String res=response.toString();
             Log.e(Login.TAG, res);
         
         } catch (Exception e) {
             Log.e(Login.TAG, e.toString());
         }
	}
	
    private JSONObject userToJson(){
    	JSONObject json = new JSONObject();
    	
    	try{
    	json.put("user", username.getText().toString());
    	json.put("pwd", password.getText().toString());
    	json.put("email", email.getText().toString());
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }
    
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
