package com.lolapau.cobradordelfrac;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.HttpResponseParser;
import com.lolapau.cobradordelfrac.parser.json.JsonFactory;
import com.lolapau.cobradordelfrac.types.Debt;

public class NewDebtActivity extends SherlockActivity {

    private EditText mDebtorName;
    private EditText mComments;
    private EditText mQuantity;
    private Debt mDebt;
    private double quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDebt = new Debt();
        if(savedInstanceState == null)
       	 	mDebt =null;
        // If user goes out the app meanwhile creating a debt, when he/she returns the data filled will be back
        else
       	 	mDebt = (Debt) savedInstanceState.getParcelable("DEBT");
        
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7aa32d")));
	    actionBar.show();		

        setContentView(R.layout.new_debt_activity);
        
        //In order to avoid network android.os.Network error for making connections from Main Activity
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }

        setTitle(R.string.title_new_debt);

        mDebtorName = (EditText) findViewById(R.id.debtor_name);
        mComments = (EditText) findViewById(R.id.comments_edit);
        mQuantity = (EditText) findViewById(R.id.quantity_edit);
        
        Bundle extras = getIntent().getExtras();
        if(extras != null){
	        boolean isContact = extras.getBoolean("ISCONTACT");
	        if(isContact){
	        	String userName = extras.getString("CONTACT");
	        	mDebtorName.setText(userName);
	        	mDebtorName.setEnabled(false);
	        }
        }
        
        mQuantity.addTextChangedListener(new TextWatcher(){
        	public void afterTextChanged(Editable s) {
        	    quantity = 0;
        	    if (s != null) {
        	        try {
        	            quantity = Double.parseDouble(s.toString().replace(',', '.'));
        	        } catch (NumberFormatException e) {
        	            getFormatErrorDialog().show();
        	        }
        	    }
        	}
        	public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}     	
        });
		populateFields();

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	boolean result = true;
                result = saveDebt();
                if(result){
                	finish();
                }
            }
        });
    }

    private void populateFields() {
        if (mDebt != null) {
            mDebtorName.setText(mDebt.getDebtorName());
            mQuantity.setText(Double.toString(mDebt.getQuantity()));
            mComments.setText(mDebt.getComments());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("DEBT", mDebt);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private boolean saveDebt() {
    	Dialog dialog = getUpdatingDialog();
   	 	dialog.show();
   	 
    	Debt debt = new Debt();
        String uName = mDebtorName.getText().toString();
        String uId = userId(uName);

        try {
	        if (uId != null){
	        	debt.setDebtorName(mDebtorName.getText().toString());
	        	debt.setCreditorId(HomeActivity.id);
	        	debt.setDebtorId(uId);
	        	debt.setQuantity(quantity);
	        	debt.setComments(mComments.getText().toString());
	            debt.setCreditorName(HomeActivity.username);

	         	JSONObject json = JsonFactory.debtToJson(debt);
	            String response = CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("debts"), json);    
	            Log.i("RES", response);
	        	dialog.cancel();      
	        	return true;
	        }
	        else {
	        	dialog.cancel();
	            getNoUserDialog().show();
	            return false;
	        }
        }
         catch (Exception e) {
       	 	dialog.cancel();
       	 	getErrorConnectionDialog().show();
            return false;
        }
   
    }
    
    private String userId(String u_name){
    	// TODO Auto-generated method stub
		String response = null;
		String id = null;
         try {
        	String [] params = {"user", u_name};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
            id = HttpResponseParser.getUserAndId(response)[1];
         } catch (Exception e) {
         }   
         return id;
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
				getUpdatingDialog().show();
				finish();
				return true;
			case R.id.menu_debts:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_DEBTS);
				finish();
				return true;
			case R.id.menu_add_debt:
                return true;
			case R.id.menu_friends:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_CONTACTS);
				finish();
				return true;
			case R.id.sign_out_menu:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_LOGOUT);
				finish();
				return true;
			case R.id.add_friend_menu:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_NEWC);
				finish();
    			return true;
			default:
				return false;
		}
	}
    
    private Dialog getNoUserDialog(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.add_user);
    	builder.setMessage(R.string.add_user_text);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
           	 Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
             itSend.setType("plain/text");
             String body =  getText(R.string.mail_i_one) + "  " + Double.toString(quantity) + " \n" + getText(R.string.mail_i_two);
      
             //colocamos los datos para el env’o
             itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.mail_subject));
             itSend.putExtra(android.content.Intent.EXTRA_TEXT, body);
             //iniciamos la actividad
             startActivity(itSend);
             
    		dialog.cancel();
    		}
    	});
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			finish();
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
    
    private Dialog getFormatErrorDialog(){
    	Dialog dialog = null;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_text);
    	builder.setMessage(R.string.text_error);	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {  		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});
    	dialog = builder.create();

    	return dialog;
    }


}