package com.lolapau.cobradordelfrac;

import com.example.cobradordelfrac.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;




public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setTitle("Login");
		
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view){
				
			}
			
		});

	}

}
