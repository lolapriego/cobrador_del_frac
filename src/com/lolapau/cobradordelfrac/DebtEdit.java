package com.lolapau.cobradordelfrac;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DebtEdit extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debt_edit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debt_edit, menu);
		return true;
	}

}
