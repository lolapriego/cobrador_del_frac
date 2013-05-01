package com.lolapau.cobradordelfrac;



import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	public static String username;
	
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    public static final String DEBTOR = "Debtor";
    public static final String QUANTITY = "Quantity";
    public static final String COMMENTS = "Comments";

    
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DEBES = Menu.FIRST + 1;
    private static final int DEBO = Menu.FIRST + 2;
    private static final int INFO = Menu.FIRST + 3;
    private static final int DELETE_ID = Menu.FIRST + 4;
    
    private static final int DIALOGO_TIPO_1 = 1;
    private static final int DIALOGO_TIPO_2 = 2;
    
    private int posicion;
    
    private ArrayList<Debt> mDebtList = new ArrayList<Debt>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences storage = getSharedPreferences(Login.USER_ID, 0);
		id = storage.getString("u_id", "");
		username = storage.getString("u_name", "");

		if(id.length() == 0){
			Intent intent = new Intent(this, Login.class);
		    startActivity(intent);
		    finish();
		}

		ActionBar actionBar = getActionBar();
		actionBar.show();
		
		setContentView(R.layout.debt_list);
		
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

        try {
            String [] params ={"user_creditor_id", id};
            response = CustomHttpClient.executeHttpGet(UrlBuilder.paramsToUrl(params, "debts"));
            
            
            JSONTokener tokener = new JSONTokener( response.toString() );
            JSONArray res = new JSONArray( tokener );
            DebtParser parser = new DebtParser();
            mDebtList.clear();
            
            for(int i = 0; i<res.length(); i++){
            	 Debt debt = parser.parse(res.getJSONObject(i));
            	 mDebtList.add(debt);
            	 
                 HashMap<String, String> map = new HashMap<String, String>();
                 map.put(DEBTOR, debt.getDebtorName());
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
            case DEBES: return true;
            case DEBO:  Intent i = new Intent(this, DebtsActivity.class);
            			startActivity(i);
            			return true;
            case INFO: onCreateDialog(DIALOGO_TIPO_1).show();
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
                posicion = info.position;
                onCreateDialog(DIALOGO_TIPO_2).show();
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createDebt() {
        Intent i = new Intent(this, DebtEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        Intent i = new Intent(this, DebtEdit.class);
        i.putExtra("DEBT", mDebtList.get(position));
        startActivityForResult(i, ACTIVITY_EDIT);
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
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialogo = null;

    	switch(id)
    	{
    	case DIALOGO_TIPO_1:
    	dialogo = crearDialogo1();
    	break;

    	case DIALOGO_TIPO_2:
    	dialogo = crearDialogo2();
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

    	private Dialog crearDialogo2()
    	{
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

       	builder.setTitle(R.string.ok);
    	builder.setMessage(R.string.message_confirm);
    	
    	builder.setPositiveButton(R.string.ok, new OnClickListener() {
    		
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
                deleteDebt(mDebtList.get(posicion));
    			dialog.cancel();
    		}
    	});
    	
    	builder.setNegativeButton(R.string.cancel, new OnClickListener() {
    	public void onClick(DialogInterface dialog, int which) {
    	dialog.cancel();
    	}
    	});

    	return builder.create();
    	}

}
