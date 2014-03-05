package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.HttpResponseParser;
import com.lolapau.cobradordelfrac.types.Debt;


public class DebtsActivity extends SherlockListActivity {
	
    private static final int INSERT_ID = Menu.FIRST;
    private static final int GOOD = Menu.FIRST + 1;
    private static final int BAD = Menu.FIRST + 2;
    
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.show();		
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
        
        MenuItem debes = menu.add(0, GOOD,0, R.string.title_activity_home);
        MenuItem debo = menu.add(0, BAD,1, R.string.title_activity_debts);
        debes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        debo.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
		return true;
	}



	private void fillData(){
		String response = null;
	    ArrayList<HashMap<String, String>> debtList = null;
        Dialog dialog = null;

    	dialog = getUpdatingDialog();
        try {
        	dialog.show();
            String [] params ={"user_debtor_id", HomeActivity.id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            debtList = HttpResponseParser.getDebts(mDebtList, response, false);
            
        	ListAdapter adapter = new SimpleAdapter(this, debtList,
                    R.layout.debt_row,
                    new String[] { "Creditor", HomeActivity.QUANTITY, HomeActivity.COMMENTS }, new int[] {
                            R.id.debtor, R.id.quantity, R.id.comments });
     
            setListAdapter(adapter);
            
        	dialog.cancel();            
        } catch (Exception e) {
        	dialog.cancel();            
        	getErrorConnectionDialog().show();
        }
	}
	

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID: createDebt();
                return true;
            case GOOD: finish();
					   return true;
            case BAD: return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
    private void createDebt() {
        Intent i = new Intent(this, DebtEdit.class);
        startActivityForResult(i, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }
    
	private Dialog getUpdatingDialog(){
    	Dialog dialogo = null;

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.updating);        	
    	dialogo = builder.create();

    	return dialogo;
	}
	
    private Dialog getErrorConnectionDialog(){
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
