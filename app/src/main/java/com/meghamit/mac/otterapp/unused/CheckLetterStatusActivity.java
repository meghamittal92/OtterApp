package com.meghamit.mac.otterapp.unused;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meghamit.mac.otterapp.AbstractBaseActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.postOffice.SentLetterActivity;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Deprecated
public class CheckLetterStatusActivity extends AbstractBaseActivity {

    @BindView(R.id.sentLettersListView)
    ListView sentLettersListView;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        final List<com.meghamit.mac.otterapp.pojo.LetterMetadata> sentLettersMetadata = ParseServerAccessor.getSentLetters(ParseUser.getCurrentUser(), 1);
        final List<String> letterTitles = new ArrayList<>();
        for (com.meghamit.mac.otterapp.pojo.LetterMetadata sentLetterMetadata : sentLettersMetadata) {
            letterTitles.add(sentLetterMetadata.getTitle());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, letterTitles);

        sentLettersListView.setAdapter(arrayAdapter);

        sentLettersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final com.meghamit.mac.otterapp.pojo.LetterMetadata item = sentLettersMetadata.get(i);
                Intent intent = new Intent(getApplicationContext(), SentLetterActivity.class);
                intent.putExtra(Constants.LetterMetadata.TITLE, item.getTitle());
                intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
                intent.putExtra(Constants.LetterMetadata.DATE_RECEIVED, item.getDateReceived());
                intent.putExtra(Constants.LetterMetadata.FROM_POSTBOX, item.getFromPostBox());
                intent.putExtra(Constants.LetterMetadata.TO_POSTBOX, item.getToPostBox());
                intent.putExtra(Constants.LetterMetadata.STATUS, item.getStatus());
                intent.putExtra(Constants.LetterMetadata.OBJECT_ID, item.getObjectId());
                intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
                intent.putExtra(Constants.LetterMetadata.DAYS_IN_TRANSIT, item.getDaysInTransit());
                startActivity(intent);

            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_check_letter_status;
    }
}
