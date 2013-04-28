package com.lolapau.cobradordelfrac;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;

import com.lolapau.cobradordelfrac.R;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtsActivity extends ListActivity {
	
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
            String [] params = {"user_debtor_id", HomeActivity.id};
        	response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
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
	
}
