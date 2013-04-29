package com.lolapau.cobradordelfrac;



import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDebt = new Debt();
        edit_flag = false;

      		
        setContentView(R.layout.activity_debt_edit);
        
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

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                if(edit_flag)   editDebt();
                else  saveDebt();
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
        } else {
                error.setText("Sorry!! Incorrect Username");
        }
        
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
             Log.e(Login.TAG, e.toString());
             error.setText(e.toString());
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

	public static void main(String[] args) {
		//In order to avoid network android.os.Network error for making connections from Main Activity
				if (android.os.Build.VERSION.SDK_INT > 9) {
				    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				    StrictMode.setThreadPolicy(policy);
				}
	}

}