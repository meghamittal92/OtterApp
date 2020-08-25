package com.meghamit.mac.otterapp.unused;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.constants.Constants;

import butterknife.BindView;

@Deprecated
public class ReceivedLetterActivity extends AbstractBaseActivity {

    @BindView(R.id.receivedLetterTitle)
    TextView letterTitle;

    @BindView(R.id.receivedLetterDateSent)
    TextView dateSent;

    @BindView(R.id.receivedLetterFromPostBox)
    TextView fromPostBox;

    @BindView(R.id.receivedLetterToPostBox)
    TextView toPostBox;


    @BindView(R.id.receivedLetterDateReceived)
    TextView dateReceived;

    //@BindView(R.id.dateReceived)
    //TextView dateReceivedText;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);


        Log.i("INFO", "In received letter  activity");

        final Bundle bundle = intent.getExtras();
        letterTitle.setText(bundle.getString(Constants.LetterMetadata.TITLE));
        dateSent.setText(bundle.getSerializable(Constants.LetterMetadata.DATE_SENT).toString());
        fromPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.FROM_POSTBOX, -1)));
        toPostBox.setText(Integer.toString(bundle.getInt(Constants.LetterMetadata.TO_POSTBOX, -1)));
        //status.setText(bundle.getString(Constants.LetterMetadata.STATUS));

        if (bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED) != null ) {
            dateReceived.setText(bundle.getSerializable(Constants.LetterMetadata.DATE_RECEIVED).toString());
        } else {
            //dateReceivedText.setText("");
            dateReceived.setText("");
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_received_letter;
    }
}
