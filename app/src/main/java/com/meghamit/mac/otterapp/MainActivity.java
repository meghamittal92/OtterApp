package com.meghamit.mac.otterapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseUser;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //ParseInstallation.getCurrentInstallation().saveInBackground();

       // boolean finish = getIntent().getBooleanExtra("finish", false);

        if(ParseUser.getCurrentUser() == null) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

  else {
            Intent intent = new Intent(this, UserHome2Activity.class);
            startActivity(intent);
            finish();
        }


    }
}
