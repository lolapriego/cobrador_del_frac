package com.lolapau.cobradordelfrac;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cobradordelfrac.R;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;




public class Login extends Activity {
	public final static String BASE_URL = "https://api.mongolab.com/api/1/databases/cobrador_frac_db/collections/";
	public final static String URL_API_KEY = "apiKey=nfd15AYKylMkyz1q1tC1LZocFeqxk12_";
	
	private static final String TAG = "MyActivity";
	
	private EditText mUsername;
	private EditText mPwd;
	private TextView error;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
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
		             String res=response.toString();
		             res= res.replaceAll("\\s+","");
		             if(res.equals("1"))
		                 error.setText("Correct Username or Password");
		             else
		                 error.setText("Sorry!! Incorrect Username or Password");
		         } catch (Exception e) {
		             Log.e(TAG, e.toString());
		         }
 
               
			}
			
		});

	}
	
	
	private String URLBuilder () throws Exception{
        Log.i(TAG, ">>>>>>      beginning  >>>>>>>" + mUsername.getText().toString());

		char comillas = (char) 34;
		String path = "{" + comillas + "user" + comillas + ": " + comillas + mUsername.getText().toString() + comillas
				+ ", " + comillas + "pwd" + comillas + ": " + comillas + mPwd.getText().toString() + comillas + "}&";
		return BASE_URL + "system.users?q=" + URLEncoder.encode(path, "UTF-8") + URL_API_KEY;
	}



}
