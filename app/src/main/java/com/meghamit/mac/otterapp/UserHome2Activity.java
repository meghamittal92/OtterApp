package com.meghamit.mac.otterapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.postOffice.PostOfficeActivity;
import com.meghamit.mac.otterapp.postbox.CheckPostBoxActivity2;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parse.ParseUser;

import butterknife.BindView;

public class UserHome2Activity extends AbstractBaseActivity {

    @BindView(R.id.postBoxNumberTextView)
    TextView postBoxNumberTextView;

    @BindView(R.id.attachImageFab)
    ExtendedFloatingActionButton fab;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        postBoxNumberTextView.setText("P.O. Box No. " + ParseUser.getCurrentUser().get(Constants.User.POST_BOX));
        fab.shrink();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_home2;
    }

    public void checkPostBox(View view) {
        Intent intent = new Intent(this, CheckPostBoxActivity2.class);
        startActivity(intent);
    }

    public void goToPostOffice(View view) {
        if(fab.isExtended()) {
            Intent intent = new Intent(this, PostOfficeActivity.class);
            startActivity(intent);
            fab.shrink();
        }
        else
        {
            fab.extend();
        }
    }
}
