package com.meghamit.mac.otterapp.unused;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.meghamit.mac.otterapp.LogoutActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;

import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class LetterViewActivity extends AppCompatActivity {

    @BindView(R.id.letterViewTitle)
    TextView letterViewTitle;

    @BindView(R.id.letterViewDateSent)
    TextView dateSent;

    @BindView(R.id.letterViewFromPostBox)
    TextView fromPostBox;

    @BindView(R.id.letterViewToPostBox)
    TextView toPostBox;

    @BindView(R.id.letterViewStatus)
    TextView status;
    @BindView(R.id.letterViewDateReceived)
    TextView dateReceived;
    @BindView(R.id.dateReceived)
    TextView dateReceivedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_view);
        ButterKnife.bind(this);

        Log.i("INFO", "In letter view activity");


        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        letterViewTitle.setText(bundle.getString(Constants.LetterMetadata.TITLE));
        dateSent.setText(bundle.getSerializable(Constants.LetterMetadata.DATE_SENT).toString());
        fromPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.FROM_POSTBOX, -1)));
        toPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.TO_POSTBOX, -1)));
        status.setText(bundle.getString(Constants.LetterMetadata.STATUS));

        if (bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED) != null && LetterStatus.FAILED.equals(bundle.getString(Constants.LetterMetadata.STATUS))) {
            dateReceived.setText(bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED).toString());
        } else {
            dateReceivedText.setText("");
            dateReceived.setText("");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            startActivity(new Intent(this, LogoutActivity.class));

            //Log.i("INFO", "current user is " + ParseUser.getCurrentUser().getUsername());
        }

        return super.onOptionsItemSelected(item);
    }
}
