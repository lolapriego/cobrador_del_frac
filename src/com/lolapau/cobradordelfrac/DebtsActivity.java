package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;


public class DebtsActivity extends SherlockListActivity {
	
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DEBES = Menu.FIRST + 1;
    private static final int DEBO = Menu.FIRST + 2;
    private static final int INFO = Menu.FIRST + 3;
    
    private static final int DIALOGO_TIPO_1 = 1;
    private static final int CONNECTION_ERROR = 2;
    private static final int CONNECTING = 3;
    
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 ActionBar actionBar1 = getSupportActionBar();
		 actionBar1.show();
		
		setContentView(R.layout.debt_list);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
        fillData();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        
        MenuItem debes = menu.add(0, DEBES,0, R.string.title_activity_home);
        MenuItem debo = menu.add(0, DEBO,1, R.string.title_activity_debts);
        MenuItem info = menu.add(0,INFO,2, R.string.info);
        debes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        debo.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        info.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
		return true;
	}



	private void fillData(){
		String response = null;
        ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();
        Dialog dialog = null;

    	dialog = onCreateDialog(CONNECTING);
        try {
        	dialog.show();
            String [] params ={"user_debtor_id", HomeActivity.id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            mDebtList.clear();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put("Creditor", debt.getCreditorName());
                 map.put(HomeActivity.QUANTITY, Double.toString(debt.getQuantity()));
                 map.put(HomeActivity.COMMENTS, debt.getComments());
                 
                 debtList.add(map);
            }
        	ListAdapter adapter = new SimpleAdapter(this, debtList,
                    R.layout.debt_row,
                    new String[] { "Creditor", HomeActivity.QUANTITY, HomeActivity.COMMENTS }, new int[] {
                            R.id.debtor, R.id.quantity, R.id.comments });
     
            setListAdapter(adapter);
            
        	dialog.cancel();            
        } catch (Exception e) {
        	dialog.cancel();            
        	onCreateDialog(CONNECTION_ERROR).show();
            Log.e(Login.TAG, e.toString());
        }
	}
	
    protected Dialog onCreateDialog(int id) {
    	Dialog dialogo = null;

    	switch(id){
	    	case DIALOGO_TIPO_1: dialogo = crearDialogo1();
	    		                 break;
	    	case CONNECTING: dialogo = crearDialogo2();
	    							   break;
	    	case CONNECTION_ERROR: dialogo = crearDialogo3();
	    									 break;
	    	default: dialogo = null;
	    	         break;
    	}

    	return dialogo;
    }
    
    private Dialog crearDialogo1(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	builder.setTitle(R.string.info);
    	builder.setMessage(R.string.info_complete);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.cancel();
    		}
    	});

    	return builder.create();
    }
    
   
	

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID: createDebt();
                return true;
            case DEBES: finish();
						return true;
            case DEBO: return true;
            case INFO: onCreateDialog(DIALOGO_TIPO_1).show();
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createDebt() {
        Intent i = new Intent(this, DebtEdit.class);
        startActivityForResult(i, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
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
    	
    	builder.setPositiveButton(R.string.try_again, new OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			fillData();
    		}
    	});

    	dialogo = builder.create();

    	return dialogo;
    }
	
	
}
