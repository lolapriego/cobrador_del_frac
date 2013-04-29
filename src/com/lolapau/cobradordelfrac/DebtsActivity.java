package com.lolapau.cobradordelfrac;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.lolapau.cobradordelfrac.R;
import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;

public class DebtsActivity extends ListActivity {
	
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	
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
        ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();

        try {
            String [] params ={"user_creditor_id", HomeActivity.id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put("Creditor", debt.getCreditorId());
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
	
}
