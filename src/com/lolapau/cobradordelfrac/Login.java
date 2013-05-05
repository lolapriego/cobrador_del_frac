package com.lolapau.cobradordelfrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import com.actionbarsherlock.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;




public class Login extends SherlockActivity {	
	public static final String TAG = "MyActivity";
	
	public static final String USER_ID = "User_id";
	
	private Button btnTwLogin;
	
	private EditText mUsername;
	private EditText mPwd;
	private TextView error;
	private TextView txtTwStatus;
	private boolean twitter_active = false;
	
	//private static CommonsHttpOAuthProvider provider =
	  //      new CommonsHttpOAuthProvider(
	 //       "https://api.twitter.com/oauth/request_token",
	   //     "https://api.twitter.com/oauth/access_token",
	     //   "https://api.twitter.com/oauth/authorize");
	 
//	private static CommonsHttpOAuthConsumer consumer =
	//        new CommonsHttpOAuthConsumer(
	//            "IULB5hcb2j9BJVyqkQwd5Q",
	  //          "Us5tu1NPrXpONYPLBbK37fw4hzizhxbVBHjEOqQ8Wg");
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		setTitle(R.string.title_activity_login);
		
        mUsername = (EditText) findViewById(R.id.et_un);
        mPwd = (EditText) findViewById(R.id.et_pw);
        error= (TextView)findViewById(R.id.login_error);
        		
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		Button btnSignUp = (Button) findViewById(R.id.btn_sign_up);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view){
				// TODO Auto-generated method stub
				
				String response = null;
		         try {
		        	String [] params = {"user", mUsername.getText().toString(), "pwd", mPwd.getText().toString()};
		        	
		        	Log.i(TAG, UrlBuilder.paramsToUrl(params, "system.users"));
		             response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
		             
		             String res=response.toString();
		             		             
		             if(res.length() > 20){
			             String name = res.split("\"")[9];
		            	 res = res.split("\"")[5];
		            	 goTo(res, name);

		             }
		             else{		     
		            	 Toast toast1 = 
		            			 Toast.makeText(getApplicationContext(),
		            					 R.string.message_incorrect, Toast.LENGTH_SHORT);

		            	toast1.show();
		             }
		         } catch (Exception e) {
		             Log.e(TAG, e.toString());
		         }
 
               
			}
			
		});
		

	}
	

    @Override
    protected void onResume() {
        super.onResume();
    }
	

	private void goTo(String id, String name){
	   	 SharedPreferences u_id = getSharedPreferences(USER_ID, 0);
	   	 SharedPreferences.Editor editor = u_id.edit();
	   	 editor.putString("u_id", id);
	   	 editor.putString("u_name", name);
	   	 
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
    	 Toast toast1 = 
 			 Toast.makeText(getApplicationContext(),
 			 R.string.correct_sign_up, Toast.LENGTH_SHORT);
    	 toast1.show();
        }
    }

}
