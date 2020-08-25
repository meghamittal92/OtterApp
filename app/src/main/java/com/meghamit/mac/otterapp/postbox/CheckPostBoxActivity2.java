package com.meghamit.mac.otterapp.postbox;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.meghamit.mac.otterapp.LogoutActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.parse.ParseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckPostBoxActivity2 extends AppCompatActivity {

    @BindView(R.id.receivedLettersRecyclerView)
    RecyclerView receivedLettersRecyclerView;

    @BindView(R.id.noOneLovesYouTextView)
    TextView noOneLovesYouTextView;

    @BindView(R.id.receivedLettersTextView)
    TextView receivedLettersTextView;

    AlertDialog alertDialog;

    Boolean noOneLovesYou = false;

    boolean isLoading = false;
    boolean adapterNeedsRefresh = false;
    ReceivedLettersRecyclerViewAdapter receivedLettersRecyclerViewAdapter;
    int pageNum = 0;

    ArrayList<LetterMetadata> receivedLetterMetadatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("INFO", "in onCreate of CheckPostBox activity");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();
        alertDialog.show();

        new LoadLettersTask(this).execute();
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

    public void populateView() {

        setTextBoxesVisibility(noOneLovesYou);
        receivedLettersRecyclerViewAdapter = new ReceivedLettersRecyclerViewAdapter(this, receivedLetterMetadatas);
        receivedLettersRecyclerView.setAdapter(receivedLettersRecyclerViewAdapter);
        receivedLettersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        receivedLettersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == receivedLetterMetadatas.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });



    }

    private void loadMore() {
        receivedLetterMetadatas.add(null);

//        pageNum ++;
//        new LoadMoreLettersTask().execute(pageNum);


        receivedLettersRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                receivedLettersRecyclerViewAdapter.notifyItemInserted(receivedLetterMetadatas.size() - 1);
                receivedLetterMetadatas.remove(receivedLetterMetadatas.size() - 1);
                receivedLettersRecyclerViewAdapter.notifyItemRemoved(receivedLetterMetadatas.size());

                pageNum ++;

                final ArrayList<LetterMetadata> newReceivedLetterMetadatas = (ArrayList)ParseServerAccessor.getReceivedLetters(ParseUser.getCurrentUser(), pageNum);
                receivedLetterMetadatas.addAll(newReceivedLetterMetadatas);

                receivedLettersRecyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        });


    }

//    class LoadMoreLettersTask extends AsyncTask<Integer, Void, Void> {
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            recyclerViewAdapter.notifyDataSetChanged();
//            isLoading = false;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            receivedLetterMetadatas.remove(receivedLetterMetadatas.size() - 1);
//            recyclerViewAdapter.notifyItemRemoved(receivedLetterMetadatas.size());
//        }
//
//        @Override
//        protected Void doInBackground(Integer... integers) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            Integer pageNum = integers[0];
//            final ArrayList<LetterMetadata> newReceivedLetterMetadatas = (ArrayList)ParseServerAccessor.getReceivedLetters(ParseUser.getCurrentUser(), pageNum);
//            receivedLetterMetadatas.addAll(newReceivedLetterMetadatas);
//
//
//
//            return null;
//        }
//    }


    class LoadLettersTask extends AsyncTask<Void, Void, Void> {

        private CheckPostBoxActivity2 checkPostBoxActivity;

        public LoadLettersTask(CheckPostBoxActivity2 checkPostBoxActivity)
        {
            this.checkPostBoxActivity = checkPostBoxActivity;
        }

        @Override
        protected void onPreExecute() {
            // showDialog(AUTHORIZING_DIALOG);
        }

        @Override
        protected void onPostExecute(Void _void) {

            // Pass the result data back to the main activity
            Log.i("INFO", "in Post execute, noOneLovesyou is" + noOneLovesYou);
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            setContentView(R.layout.activity_check_post_box2);
            ButterKnife.bind(checkPostBoxActivity);

            populateView();

        }

        @Override
        protected Void doInBackground(Void... someVoids ) {

            //Do all your slow tasks here but dont set anything on UI
            //ALL ui activities on the main thread

             receivedLetterMetadatas = (ArrayList)ParseServerAccessor.getReceivedLetters(ParseUser.getCurrentUser(), pageNum);
            if(receivedLetterMetadatas.isEmpty())
            {
                Log.i("INFO", "in doInBackground, receivedLetterMetadatas is empty ");
                noOneLovesYou = true;
            }

            Log.i("INFO", "Size of receivedLetterMetadatas: " + receivedLetterMetadatas.size());

            return null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RECEIVED_LETTER_ACTIVITY_REQUEST_CODE) {

            Bundle extras = data.getExtras();
            if (extras != null) {

                adapterNeedsRefresh = Boolean.parseBoolean(extras.getString(Constants.IntentExtra.RECEIVED_LETTERS_ADAPTER_NEEDS_REFRESH));
                if (adapterNeedsRefresh) {
                    new LoadLettersTask(this).execute();
                }
            }
        }
    }
}
