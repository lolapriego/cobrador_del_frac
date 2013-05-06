package com.lolapau.cobradordelfrac;



import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtEdit extends SherlockActivity {

    private EditText mDebtorName;
    private EditText mComments;
    private EditText mQuantity;
    private Debt mDebt;
    private TextView error;
    private boolean edit_flag;
    private Button mailBtn;
    private double quantity;
    
    private static final int CONNECTION_ERROR = 3;
    private static final int CONNECTING = 4;
    private static final int TEXT_ERROR = 5;

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

        
        //In order to avoid network android.os.Network error for making connections from Main Activity
        		if (android.os.Build.VERSION.SDK_INT > 9) {
        		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        		    StrictMode.setThreadPolicy(policy);
        		}
        if(edit_flag)		
        	setTitle(R.string.title_activity_debt_edit);
        else
        	setTitle(R.string.title_new_debt);

        mDebtorName = (EditText) findViewById(R.id.debtor_name);
        mComments = (EditText) findViewById(R.id.comments_edit);
        mQuantity = (EditText) findViewById(R.id.quantity_edit);
        
        mQuantity.addTextChangedListener(new TextWatcher(){
        	public void afterTextChanged(Editable s) {
        	    quantity = 0;
        	    if (s != null) {
        	        try {
        	            quantity = Double.parseDouble(s.toString().replace(',', '.'));
        	        } catch (NumberFormatException e) {
        	            onCreateDialog(TEXT_ERROR);
        	        }
        	    }
        	}
        	public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}     	
        });

        Button confirmButton = (Button) findViewById(R.id.confirm);

		 if(!edit_flag) mailBtn.setVisibility(View.GONE);
		 else   mDebtorName.setEnabled(false);
		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	boolean result = true;
                if(edit_flag)   editDebt();
                else {
                	result = saveDebt();
                }
                
                if(result){
                    setResult(RESULT_OK);
                	finish();
                }
            }

        });
        
        mailBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	 Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
            	 
                 //vamos a enviar texto plano a menos que el checkbox estŽ marcado 
                 itSend.setType("plain/text");
                 
                 String body = getText(R.string.mail_one) + "" + mDebt.getQuantity() + "" +  getText(R.string.mail_two) +
                		 "" + mDebt.getCreditorName() + "" + getText(R.string.mail_three) + "" + mDebt.getComments();
          
                 //colocamos los datos para el env’o
                 itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ getUserEmail(mDebt.getDebtorName())});
                 itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, getText(R.string.mail_subject));
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

    private boolean saveDebt() {
    	Dialog dialog = null;
   	 	dialog = onCreateDialog(CONNECTING);
   	 	dialog.show();
   	 
    	Debt debt = new Debt();
    	
        String uName = mDebtorName.getText().toString();
        String uId = userId(uName);

        try {
        if (uId.length() >10){
        	debt.setDebtorName(mDebtorName.getText().toString());
        	debt.setCreditorId(HomeActivity.id);
        	debt.setDebtorId(uId);
        	debt.setQuantity(quantity);
        	debt.setComments(mComments.getText().toString());
            debt.setCreditorName(HomeActivity.username);
        
            String response = null;


         	JSONObject json = debtToJson(debt);
             response = CustomHttpClient.executeHttpPost(UrlBuilder.toUrl("debts"), json);
             		             
             String res=response.toString();
             Log.e(Login.TAG, res);
             
        	 dialog.cancel();
         
         
         return true;
        }
        else {
            onCreateDialog(0).show();
            return false;
        }
        } catch (Exception e) {
       	 	dialog.cancel();
       	 	onCreateDialog(CONNECTION_ERROR).show();
            Log.e(Login.TAG, e.toString());
            return false;
        }
   
    }
    
    private void editDebt() {
    	Debt debt = new Debt();
    	        
    	debt.setDebtorName(mDebt.getDebtorName());
        debt.setCreditorId(HomeActivity.id);
        debt.setDebtorId(mDebt.getDebtorId());
        debt.setQuantity(quantity);
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
             if(res.length()>5){
             	 res = res.split("\"")[5];
             }
         
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

	// TODO: This should be at user model
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
	
        protected Dialog onCreateDialog(int id) {
        	Dialog dialogo = null;
        	switch(id){    	
    	    	case 0: dialogo = crearDialogo1();
    	    	   					 break;
    	    	case CONNECTION_ERROR: dialogo = crearDialogo3();
    	    							break;
    	    	case CONNECTING: dialogo = crearDialogo2();
    	    					 break;
    	    	case TEXT_ERROR: dialogo = crearDialogo4();
    	    					 break;
    	    	default: dialogo = null;
    	    	         break;
        	}

        	return dialogo;
        }
	
    private Dialog crearDialogo1(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.add_user);
    	builder.setMessage(R.string.add_user_text);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
           	 Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
        	 
             itSend.setType("plain/text");
             
             String body =  getText(R.string.mail_i_one) + "" + Double.toString(quantity) + "" + getText(R.string.mail_i_two);
      
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
    			dialog.cancel();
    		}
    	});

    	dialogo = builder.create();

    	return dialogo;
    }
    
	private Dialog crearDialogo2(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.updating);        	
    	dialogo = builder.create();

    	return dialogo;
	}
	
    private Dialog crearDialogo3(){
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
    
    private Dialog crearDialogo4(){
    	Dialog dialogo = null;

	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.error_text);
    	builder.setMessage(R.string.text_error);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    		}
    	});

    	dialogo = builder.create();

    	return dialogo;
    }


}