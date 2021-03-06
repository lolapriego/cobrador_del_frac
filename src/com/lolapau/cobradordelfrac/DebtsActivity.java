package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.HttpResponseParser;
import com.lolapau.cobradordelfrac.types.Debt;
import com.lolapau.cobradordelfrac.types.Utility;


public class DebtsActivity extends SherlockListActivity {
	    
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7aa32d")));
	    actionBar.show();		
		setContentView(R.layout.debt_list2);

		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}

        fillData();	
        
		Utility u = new Utility();
    	u.setListViewHeightBasedOnChildren(getListView());
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
	
	// make the menu work
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
				return true;
			case R.id.menu_add_debt:
				getUpdatingDialog().show();
				setResult(HomeActivity.RESULT_GOTO_NEWD);
				finish();
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
