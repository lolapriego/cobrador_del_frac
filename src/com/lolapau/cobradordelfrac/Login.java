package com.lolapau.cobradordelfrac;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cobradordelfrac.R;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;




public class Login extends Activity {
	public final static String BASE_URL = "https://api.mongolab.com/api/1/databases/cobrador_frac_db/collections/";
	public final static String URL_API_KEY = "apiKey=bLbJB4v2EbgoIaC5NaUxrOImvRcLT9au";
	
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
		             response = CustomHttpClient.executeHttpGet(URLBuilder());
		             
		             Log.i(TAG, "Url Builder" + '\n' + URLBuilder() + '\n');
		             
		             String res=response.toString();
		             		             
		             if(res.length() > 20){
		            	 res = res.split("\"")[5];
		            	 SharedPreferences u_id = getSharedPreferences(USER_ID, 0);
		            	 SharedPreferences.Editor editor = u_id.edit();
		            	 editor.putString("u_id", res);
		            	 
		            	 editor.commit();
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
	
	//After having troubles with URLEncoder, this function it's a little bit hand made
	//TODO: use URLEncoder
	//mailto: md.priego@gmail.com
	private String URLBuilder () throws Exception{
		String path = "%22user%22%3A%20%20%22" + mUsername.getText().toString() + "%22"
			 + ",%20%22pwd%22%3A%20%20%22" + mPwd.getText().toString() + "%22";
		return BASE_URL + "system.users?q=%7B" + path + "%7D&" + URL_API_KEY;
	}



}
