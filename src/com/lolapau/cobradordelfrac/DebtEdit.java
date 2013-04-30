package com.lolapau.cobradordelfrac;



import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtEdit extends Activity {

    private EditText mDebtorName;
    private EditText mComments;
    private EditText mQuantity;
    private Debt mDebt;
    private TextView error;
    private boolean edit_flag;
    private Button mailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDebt = new Debt();
        edit_flag = false;

        if(savedInstanceState == null) {
       	 mDebt =null;
        }
        else{
       	 mDebt = (Debt) savedInstanceState.getParcelable("DEBT");
       	 edit_flag = savedInstanceState.getBoolean("EDIT_ACTIVITY");
        }
		if (mDebt == null) {
			Bundle extras = getIntent().getExtras();
			if(extras != null){
				mDebt= (Debt)extras.getParcelable("DEBT");
				edit_flag = true;
			}
			else{
				mDebt = null;
				edit_flag = false;
			}
		}
		

        setContentView(R.layout.activity_debt_edit);
		Button mailBtn = (Button) findViewById(R.id.mail_btn);
		 if(!edit_flag) mailBtn.setVisibility(View.GONE);
        
        //In order to avoid network android.os.Network error for making connections from Main Activity
        		if (android.os.Build.VERSION.SDK_INT > 9) {
        		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        		    StrictMode.setThreadPolicy(policy);
        		}
        		
        setTitle("Debt Editor");

        mDebtorName = (EditText) findViewById(R.id.debtor_name);
        mComments = (EditText) findViewById(R.id.comments_edit);
        mQuantity = (EditText) findViewById(R.id.quantity_edit);

        Button confirmButton = (Button) findViewById(R.id.confirm);



		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                if(edit_flag)   editDebt();
                else  saveDebt();
                finish();
            }

        });
        
        mailBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	 Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
            	 
                 //vamos a enviar texto plano a menos que el checkbox estŽ marcado 
                 itSend.setType("plain/text");
                 
                 String body = "Buenas tardes le recordamos que le debe: " + mDebt.getQuantity() + " euros al usuario: " +
                   mDebt.getCreditorName() + " \nEn concepto de: \n" + mDebt.getComments();
          
                 //colocamos los datos para el env’o
                 itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getUserEmail(mDebt.getDebtorName())});
                 itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, "El Cobrador del Frac: Recordatorio de deuda");
                 itSend.putExtra(android.content.Intent.EXTRA_TEXT, body);
          
          
                 //iniciamos la actividad
                 startActivity(itSend);
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
        outState.putBoolean("EDIT_ACTIVITY", edit_flag);
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

    private void saveDebt() {
    	Debt debt = new Debt();
    	
        String uName = mDebtorName.getText().toString();
        String uId = userId(uName);
        Log.i("HELP", HomeActivity.username);

        if (uId.length() >10){
        	debt.setDebtorName(mDebtorName.getText().toString());
        	debt.setCreditorId(HomeActivity.id);
        	debt.setDebtorId(uId);
        	debt.setQuantity(Double.parseDouble(mQuantity.getText().toString()));
        	debt.setComments(mComments.getText().toString());
            debt.setCreditorName(HomeActivity.username);
        
        String response = null;
         try {
         	JSONObject json = debtToJson(debt);
             response = CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("debts"), json);
             		             
             String res=response.toString();
             Log.e(Login.TAG, res);
         
         } catch (Exception e) {
             Log.e(Login.TAG, e.toString());
             error.setText(e.toString());
         }
        }
         else {
           	 Toast toast1 = 
        			 Toast.makeText(getApplicationContext(),
        			 "Incorrect Username!", Toast.LENGTH_SHORT);

        	toast1.show();
        	}
   
    }
    
    private void editDebt() {
    	Debt debt = new Debt();
    	        
    	debt.setDebtorName(mDebt.getDebtorName());
        debt.setCreditorId(HomeActivity.id);
        debt.setDebtorId(mDebt.getDebtorId());
        debt.setQuantity(Double.parseDouble(mQuantity.getText().toString()));
        debt.setComments(mComments.getText().toString());
        debt.setCreditorName(HomeActivity.username);
        
        String response = null;
         try {
         	JSONObject json = debtToJson(debt);
             response = CustomHttpClient.executeHttpPut(UrlBuilder.debtToQuery(mDebt), json);
             		             
             String res=response.toString();
             Log.e(Login.TAG, res);
         
         } catch (Exception e) {
             Log.e(Login.TAG, e.toString());
             error.setText(e.toString());
         }           
   
    }
    
    private String userId(String u_name){
    	// TODO Auto-generated method stub
		String response = null;
		String res = null;
         try {
        	String [] params = {"user", u_name};
             response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
             		             
             res=response.toString();
             	 res = res.split("\"")[5];
                 Log.e(Login.TAG, res);
         
         } catch (Exception e) {
             e.printStackTrace();
         }   
         return res;
    }
    
    private JSONObject debtToJson(Debt debt){
    	JSONObject json = new JSONObject();
    	
    	try{
    	json.put("user_debtor_id", debt.getDebtorId());
    	json.put("debtor_name", debt.getDebtorName());
    	json.put("user_creditor_id", HomeActivity.id);
    	json.put("quantity", debt.getQuantity());
    	json.put("comments", debt.getComments());
    	json.put("creditor_name", debt.getCreditorName());
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return json;
    }

	
	private String getUserEmail (String username){
    	// TODO Auto-generated method stub
		String response = null;
		String res = null;
         try {
        	String [] params = {"user", username};
             response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "system.users"));
             		             
             res=response.toString();
             	 res = res.split("\"")[17];
                 Log.e(Login.TAG, res);
         
         } catch (Exception e) {
             e.printStackTrace();
         }   
         return res;
	}

}