package com.lolapau.cobradordelfrac;



import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.content.Intent;


import com.example.cobradordelfrac.R;

public class HomeActivity extends Activity {
	
	public static String id;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
