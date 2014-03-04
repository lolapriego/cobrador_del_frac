package com.lolapau.cobradordelfrac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.types.Typefaces;




public class Login extends SherlockActivity {	
	public static final String TAG = "MyActivity";
	
	public static final String USER_ID = "User_id";
	
	
	private EditText mUsername;
	private EditText mPwd;
	private TextView error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
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
        error= (TextView)findViewById(R.id.login_error);
        
        Typeface roboto = Typefaces.get(this, "fonts/robotolight.tff");
        TextView usrView = (TextView) findViewById(R.id.username_view);
        usrView.setTypeface(roboto);
        TextView pwView = (TextView) findViewById(R.id.pw_title);
        pwView.setTypeface(roboto);
        		
		Button btnLogin = (Button) findViewById(R.id.bt_login);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view){
				// TODO Auto-generated method stub
				
				String response = null;
		         try {
		        	 onCreateDialog(1).show();

		        	String [] params = {"user", mUsername.getText().toString(), "pwd", mPwd.getText().toString()};
		        	
		            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users")); 
		            String res=response.toString();		             		             
		            if(res.length() > 20){
			             String name = res.split("\"")[9];
		            	 res = res.split("\"")[5];
		            	 goTo(res, name);
		             }
		             else{		     
		            	Toast toast1 = Toast.makeText(getApplicationContext(), R.string.message_incorrect, Toast.LENGTH_SHORT);
		            	toast1.show();
		             }
		         } catch (Exception e) {
		        	 onCreateDialog(0).show();
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
    		public void onClick(DialogInterface dialog, int which) {
 
    		}
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
