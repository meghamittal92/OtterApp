package com.meghamit.mac.otterapp.postOffice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.constants.Constants;

public class PostOfficeActivity extends AbstractBaseActivity {

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_office;
    }

    public void sendLetter (View view) {
        Intent intent  = new Intent(this, SendLetter2Activity.class);
        startActivity(intent);
    }

    public void checkStatus (View view) {

        Intent intent = new Intent(this, CheckLetterStatusActivity2.class);
        startActivity(intent);
    }
}
