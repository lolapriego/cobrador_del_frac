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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.HttpResponseParser;
import com.lolapau.cobradordelfrac.parser.json.JsonFactory;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtEdit extends SherlockActivity {

    private TextView mDebtorName;
    private EditText mComments;
    private EditText mQuantity;
    private Debt mDebt;
    private double quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDebt = new Debt();
        // If user goes out the app meanwhile creating a debt, when he/she returns the data filled will be back
        if(savedInstanceState == null) {
	       	Bundle extras = getIntent().getExtras();
			mDebt= (Debt)extras.getParcelable("DEBT");
        }
        else
       	 mDebt = (Debt) savedInstanceState.getParcelable("DEBT");

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7aa32d")));
	    actionBar.show();		

        setContentView(R.layout.activity_debt_edit);

        //In order to avoid network android.os.Network error for making connections from Main Activity
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        setTitle(R.string.title_activity_debt_edit);

        mDebtorName = (TextView) findViewById(R.id.debtor_name);
        mComments = (EditText) findViewById(R.id.comments_edit);
        mQuantity = (EditText) findViewById(R.id.quantity_edit);
        
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

        Button confirmButton = (Button) findViewById(R.id.confirm);
		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                editDebt();
                finish();
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

    private void editDebt() {
    	Dialog updating = getUpdatingDialog();
    	updating.show();
    	Debt debt = new Debt();
    	        
    	debt.setDebtorName(mDebt.getDebtorName());
        debt.setCreditorId(HomeActivity.id);
        debt.setDebtorId(mDebt.getDebtorId());
        debt.setQuantity(quantity);
        debt.setComments(mComments.getText().toString());
        debt.setCreditorName(HomeActivity.username);
        try {
         	JSONObject json = JsonFactory.debtToJson(debt);
            CustomHttpClient.executeHttpPut(UrlBuilder.debtToQuery(mDebt), json);         
         } catch (Exception e) {
        	 getErrorConnectionDialog().show();
             e.printStackTrace();
         } 
        finally{
        	updating.cancel();
        }
    }
    
    public void deleteDebt(View view){
    	Dialog updating = getUpdatingDialog();
    	updating.show();
        try {        	
        	JSONObject json = new JSONObject();
            CustomHttpClient.executeHttpPut(UrlBuilder.debtToQuery(mDebt), json);
        } catch (Exception e) {
           	 getErrorConnectionDialog().show();
                e.printStackTrace();
        }
        finally{
        	updating.cancel();
        }
        finish();
    }
    
    public void mail(View view){
    	Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
   	 
        //vamos a enviar texto plano a menos que el checkbox esté marcado 
        itSend.setType("text/plain");
        
        String body = getText(R.string.mail_one) + " " + mDebt.getQuantity() + "\n" +  getText(R.string.mail_two) +
       		 " " + mDebt.getCreditorName() + "\n" + getText(R.string.mail_three) + " " + mDebt.getComments();
 
        //colocamos los datos para el envío
        itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getUserEmail(mDebt.getDebtorName())});
        itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.mail_subject));
        itSend.putExtra(android.content.Intent.EXTRA_TEXT, body);
 
        //iniciamos la actividad
        startActivity(itSend);
    }

	private String getUserEmail (String username){
		Dialog updating = getUpdatingDialog();
		updating.show();
		String email = null;
		String response = null;
         try {
        	String [] params = {"user", username};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
            email = HttpResponseParser.getEmail(response);
         } catch (Exception e) {
        	 updating.cancel();
        	 getErrorConnectionDialog().show();
             e.printStackTrace();
         }   
         return email;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//actionbar menu
		getSupportMenuInflater().inflate(R.menu.simple_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_debtors:
			finish();
			return true;
		case R.id.menu_debts:
			Intent i = new Intent(this, DebtsActivity.class);
			startActivity(i);
			return true;
		case R.id.menu_friends:
			return true;
		default:
			return false;
		}
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