package com.lolapau.cobradordelfrac;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Patterns;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;

public class SignUp extends SherlockActivity {
	private EditText username;
	private EditText email;
	private EditText password;
	private EditText password2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
	}
	
	private boolean validatorPw(){
		return !password.getText().toString().equals(password2.getText().toString());
	}
	
	public void signUp(View view){
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.pw);
		password2 = (EditText) findViewById(R.id.pw_repeated);
		
		if(!isValidEmail(email.getText().toString()) && validatorPw()){
			setResult(RESULT_OK);
			saveUser();
			finish();
		}
		else{
          	 Toast toast2;
          	 if(!validatorPw())
          		 toast2 = Toast.makeText(getApplicationContext(), R.string.invalid_pw, Toast.LENGTH_SHORT);
          	 else 
          		toast2 = Toast.makeText(getApplicationContext(), R.string.incorrect_sign_up, Toast.LENGTH_SHORT);
          	 toast2.show();
		}
	}
	
	private void saveUser(){
         try {
        	onCreateDialog(1);
         	JSONObject json = userToJson();
            CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("system.users"), json);        
         } catch (Exception e) {
        	 onCreateDialog(0);
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
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialogo = null;
    	switch(id){
	    	case 0: dialogo = crearDialogo1();
	    		    break;
	    	case 1: dialogo = crearDialogo2();
	    	   		break;
	    	default:dialogo = null;
	    	        break;
    	}
    	return dialogo;
    }
    
    private Dialog crearDialogo1(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_connection);
    	builder.setMessage(R.string.text_error_connection);    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {}
    	});

    	dialogo = builder.create();
    	return dialogo;
    }

    private Dialog crearDialogo2(){
        Dialog dialogo = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.connecting);        	
        dialogo = builder.create();
        return dialogo;
    }
}
