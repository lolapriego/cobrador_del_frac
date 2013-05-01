package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;


public class DebtsActivity extends ListActivity {
	
    private static final int DEBES1 = Menu.FIRST + 1;
    private static final int DEBO1 = Menu.FIRST + 2;
    private static final int INFO1 = Menu.FIRST + 3;
    private static final int DIALOGO_INFO =1;
	
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 ActionBar actionBar1 = getActionBar();
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
        
        MenuItem debes = menu.add(0, DEBES1,0, R.string.title_activity_home);
        MenuItem debo = menu.add(0, DEBO1,1, R.string.title_activity_debts);
        MenuItem info = menu.add(0,INFO1,2, R.string.info);
        debes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        debo.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        info.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
		return true;
	}



	private void fillData(){
		String response = null;
        ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();

        try {
            String [] params ={"user_debtor_id", HomeActivity.id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put("Creditor", debt.getCreditorName());
                 map.put(HomeActivity.QUANTITY, Double.toString(debt.getQuantity()));
                 map.put(HomeActivity.COMMENTS, debt.getComments());
                 
                 debtList.add(map);
            }
            
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
        }
        finally{
        	ListAdapter adapter = new SimpleAdapter(this, debtList,
                    R.layout.debt_row,
                    new String[] { "Creditor", HomeActivity.QUANTITY, HomeActivity.COMMENTS }, new int[] {
                            R.id.debtor, R.id.quantity, R.id.comments });
     
            setListAdapter(adapter);
        }

	}
	
    protected Dialog onCreateDialog(int id) {
    	Dialog dialogo = null;

    	switch(id)
    	{
    	case DIALOGO_INFO:
    	dialogo = crearDialogo1();
    	break;

    	default:
    	dialogo = null;
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
    
   
	

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
         
            case DEBES1: Intent j = new Intent(this, HomeActivity.class);
						startActivity(j);
						return true;
            case DEBO1: 	return true;
            case INFO1: onCreateDialog(DIALOGO_INFO).show();
            	return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
	
	
}
