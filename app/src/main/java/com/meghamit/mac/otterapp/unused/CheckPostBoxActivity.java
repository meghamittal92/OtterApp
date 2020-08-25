package com.meghamit.mac.otterapp.unused;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.meghamit.mac.otterapp.postbox.ReceivedLetter2Activity;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class CheckPostBoxActivity extends AppCompatActivity {

    @BindView(R.id.receivedLettersListView)
    ListView receivedLettersListView;

    @BindView(R.id.noOneLovesYouTextView)
    TextView noOneLovesYouTextView;

    @BindView(R.id.receivedLettersTextView)
    TextView receivedLettersTextView;

    SimpleAdapter simpleAdapter;
    AlertDialog alertDialog;

    Boolean noOneLovesYou = false;
    List<com.meghamit.mac.otterapp.pojo.LetterMetadata> receivedLetterMetadatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("INFO", "in onCreate of CheckPostBox activity");

       // noOneLovesYou = false;
//
//          progressDialog =  new ProgressDialog(this, R.style.AppTheme_NoActionBar );
//        progressDialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();
        alertDialog.show();

        new LoadLettersTask(this).execute();
        //setContentView(R.layout.activity_send_letter_succss);

//        Observable.create(new Observable.OnSubscribe<Void>() {
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//
//                setContentView(R.layout.activity_send_letter_succss);
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
//            @Override
//            public void onCompleted() {
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Void aVoid) {
//
//            }
//        });

//        if(!alertDialog.isShowing()) {
//
//        }

    }

    public void populateView() {

        setTextBoxesVisibility(noOneLovesYou);
        receivedLettersListView.setAdapter(simpleAdapter);
        receivedLettersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final com.meghamit.mac.otterapp.pojo.LetterMetadata item = receivedLetterMetadatas.get(i);
                Letter letter = ParseServerAccessor.getLetter(item);
                if(letter != null)
                {
                    ParseFile parseFile = (ParseFile) letter.get(Constants.Letter.LETTER_DATA);
                    String letterData = readFile(parseFile);
                    Log.i("INFO", "Char sequence is: !!!" + letterData);
                    Intent intent = new Intent(getApplicationContext(), ReceivedLetter2Activity.class);

                    intent.putExtra(Constants.Letter.LETTER_DATA, letterData);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Problem loading letter", Toast.LENGTH_LONG).show();

                }


//                Intent intent = new Intent(getApplicationContext(), ReceivedLetterActivity.class);
//                intent.putExtra(Constants.LetterMetadata.TITLE, item.getTitle());
//                intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
//                intent.putExtra(Constants.LetterMetadata.DATE_RECEIVED, item.getDateReceived());
//                intent.putExtra(Constants.LetterMetadata.FROM_POSTBOX, item.getFromPostBox());
//                intent.putExtra(Constants.LetterMetadata.TO_POSTBOX, item.getToPostBox());
//               // intent.putExtra(Constants.LetterMetadata.STATUS, item.getStatus());
//                startActivity(intent);

            }
        });

    }
    private String readFile(ParseFile parseFile) {
        //Get the text file


//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            File file = parseFile.getFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return text.toString();
    }

//    @Override
//    protected int getContentView() {
//        return R.layout.activity_check_post_box;
//    }

    class LoadLettersTask extends AsyncTask<Void, Void, List<LetterMetadata>> {

        private CheckPostBoxActivity checkPostBoxActivity;

        public LoadLettersTask(CheckPostBoxActivity checkPostBoxActivity)
        {
            this.checkPostBoxActivity = checkPostBoxActivity;
        }

        @Override
        protected void onPreExecute() {
            // showDialog(AUTHORIZING_DIALOG);
        }

        @Override
        protected void onPostExecute(List<LetterMetadata> result) {

            // Pass the result data back to the main activity

              receivedLetterMetadatas = result;
              Log.i("INFO", "in Post execute, noOneLovesyou is" + noOneLovesYou);
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            setContentView(R.layout.activity_check_post_box);
            ButterKnife.bind(checkPostBoxActivity);
            populateView();





        }

        @Override
        protected List<com.meghamit.mac.otterapp.pojo.LetterMetadata> doInBackground(Void... someVoids ) {

            //Do all your slow tasks here but dont set anything on UI
            //ALL ui activities on the main thread

            final List<com.meghamit.mac.otterapp.pojo.LetterMetadata> receivedLetterMetadatas = ParseServerAccessor.getReceivedLetters(ParseUser.getCurrentUser(),0);
            if(receivedLetterMetadatas.isEmpty())
            {
                Log.i("INFO", "in doInBackground, receivedLetterMetadatas is empty ");
                noOneLovesYou = true;
            }
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
            Log.i("INFO", "Size of receivedLetterMetadatas: " + receivedLetterMetadatas.size());
            for (com.meghamit.mac.otterapp.pojo.LetterMetadata receivedLetterMetadata : receivedLetterMetadatas) {
                Log.i("INFO", "Putting item in adaptor");
                final HashMap<String, String> item = new HashMap<>();
                String from = "From PO BOX No. " + Integer.toString(receivedLetterMetadata.getFromPostBox());
                item.put("line1", receivedLetterMetadata.getTitle());
                item.put("line2", from);
                list.add(item);
            }

            Log.i("INFO", "Size of list: " + list.size());
            SimpleAdapter simpleAdapterInTask = new SimpleAdapter(getApplicationContext(), list,
                    R.layout.twolines,
                    new String[] { "line1","line2" },
                    new int[] {R.id.line_a, R.id.line_b});

            simpleAdapter = simpleAdapterInTask;

            return receivedLetterMetadatas;

        }
    }

    private void setTextBoxesVisibility(Boolean noOneLovesYou) {

        if(noOneLovesYou)
        {
        noOneLovesYouTextView.setVisibility(View.VISIBLE);
        receivedLettersTextView.setVisibility(View.INVISIBLE);
            }
        else
        {
            noOneLovesYouTextView.setVisibility(View.INVISIBLE);
            receivedLettersTextView.setVisibility(View.VISIBLE);
        }
    }

}
