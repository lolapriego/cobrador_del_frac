package com.lolapau.cobradordelfrac;



import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lolapau.cobradordelfrac.http.CustomHttpClient;
import com.lolapau.cobradordelfrac.http.UrlBuilder;
import com.lolapau.cobradordelfrac.parser.json.DebtParser;
import com.lolapau.cobradordelfrac.types.Debt;

public class HomeActivity extends ListActivity {

	public static String id;
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    public static final String DEBTOR = "Debtor";
    public static final String QUANTITY = "Quantity";
    public static final String COMMENTS = "Comments";

    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences storage = getSharedPreferences(Login.USER_ID, 0);
		id = storage.getString("u_id", "");

		if(id.length() == 0){
			Intent intent = new Intent(this, Login.class);
		    startActivity(intent);
		}

		setContentView(R.layout.activity_home);
		
		//In order to avoid network android.os.Network error for making connections from Main Activity
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
        fillData();
        registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        
		// Inflate the menu; this adds items to the action bar if it is present.
        //TODO
		//getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	private void fillData(){
		String response = null;
        ArrayList<HashMap<String, String>> debtList = new ArrayList<HashMap<String, String>>();

        try {
            String [] params ={"user_creditor_id", id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put(DEBTOR, debt.getDebtorId());
                 map.put(QUANTITY, Double.toString(debt.getQuantity()));
                 map.put(COMMENTS, debt.getComments());
                 
                 debtList.add(map);
            }
            
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
        }
        finally{
        	ListAdapter adapter = new SimpleAdapter(this, debtList,
                    R.layout.debt_row,
                    new String[] { DEBTOR, QUANTITY, COMMENTS }, new int[] {
                            R.id.debtor, R.id.quantity, R.id.comments });
     
            setListAdapter(adapter);
        }

		
	}
	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createDebt();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                deleteDebt(mDebtList.get(info.position));
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createDebt() {
        //Intent i = new Intent(this, NoteEdit.class);
        //startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Intent i = new Intent(this, NoteEdit.class);
        //i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        //startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
	
    private void deleteDebt(Debt debt){
        try {        	
        	Log.i(Login.TAG, UrlBuilder.debtToQuery(debt));
        	JSONObject json = new JSONObject();
            String res = CustomHttpClient.executeHttpPut(UrlBuilder.debtToQuery(debt), json);
            Log.i(Login.TAG, res);
        } catch (Exception e) {
            Log.e(Login.TAG, e.toString());
            e.printStackTrace();
        }
        finally{
        	fillData();
        }
    	
    }

}
