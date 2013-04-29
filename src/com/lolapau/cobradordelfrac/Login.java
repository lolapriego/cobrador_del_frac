package com.lolapau.cobradordelfrac;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;




public class Login extends Activity {	
	public static final String TAG = "MyActivity";
	
	public static final String USER_ID = "User_id";
	
	private EditText mUsername;
	private EditText mPwd;
	private TextView error;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		setTitle("Login");
		
        mUsername = (EditText) findViewById(R.id.et_un);
        mPwd = (EditText) findViewById(R.id.et_pw);
        error= (TextView)findViewById(R.id.login_error);
        		
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view){
				// TODO Auto-generated method stub
				
				String response = null;
		         try {
		        	String [] params = {"user", mUsername.getText().toString(), "pwd", mPwd.getText().toString()};
		             response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
		             		             
		             String res=response.toString();
		             		             
		             if(res.length() > 20){
			             String name = res.split("\"")[13];
		            	 res = res.split("\"")[5];
		            	 goTo(res, name);

		             }
		             else{		     
		                 error.setText("Sorry!! Incorrect Username or Password");
		             }
		         } catch (Exception e) {
		             Log.e(TAG, e.toString());
		             error.setText(e.toString());
		         }
 
               
			}
			
		});

	}
	


	private void goTo(String id, String name){
	   	 SharedPreferences u_id = getSharedPreferences(USER_ID, 0);
	   	 SharedPreferences.Editor editor = u_id.edit();
	   	 editor.putString("u_id", id);
	   	 editor.putString("u_name", name);
	   	 
	   	 editor.commit();
	   	 
			Intent intent = new Intent(this, HomeActivity.class);
		    startActivity(intent);
	}

}
