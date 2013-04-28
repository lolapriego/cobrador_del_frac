package com.lolapau.cobradordelfrac;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.example.cobradordelfrac.R;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtsActivity extends ListActivity {
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
        fillData();
	}


	private void fillData(){
		String response = null;
        try {
        	
            Log.i(Login.TAG, URLBuilder());

            response = CustomHttpClient.executeHttpGet(URLBuilder());
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            ArrayList<Debt> debt_list = new ArrayList<Debt>();
            DebtParser parser = new DebtParser();
            
            for(int i = 0; i<res.length(); i++){
            	 debt_list.add(parser.parse(res.getJSONObject(i)));
            }

        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
        }
	}

    
    private String URLBuilder(){
		String path = "%22user_debtor_id%22%3A%20%20%22" + HomeActivity.id + "%22";
			return Login.BASE_URL + "debts?q=%7B" + path + "%7D&" + Login.URL_API_KEY;
    }
	
}
