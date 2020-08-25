package com.meghamit.mac.otterapp.postOffice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

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
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.meghamit.mac.otterapp.postbox.ReceivedLettersRecyclerViewAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;

public class CheckLetterStatusActivity2 extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_check_letter_status3);
//    }


    @BindView(R.id.sentLettersRecyclerView)
    RecyclerView sentLettersRecyclerView;

    @BindView(R.id.youAreLazyTextView)
            TextView youAreLazyTextView;

    @BindView(R.id.sentLettersTextView)
            TextView sentLettersTextView;

    AlertDialog alertDialog;

    Boolean youAreLazy = false;


    boolean isLoading = false;
    SentLetterRecyclerViewAdapter sentLetterRecyclerViewAdapter;
    int pageNum = 0;

    ArrayList<LetterMetadata> sentLettersMetadatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setTextBoxesVisibility(youAreLazy);
        sentLetterRecyclerViewAdapter = new SentLetterRecyclerViewAdapter(this, sentLettersMetadatas);
        sentLettersRecyclerView.setAdapter(sentLetterRecyclerViewAdapter);
        sentLettersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sentLettersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == sentLettersMetadatas.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });



    }

    private void loadMore() {
        sentLettersMetadatas.add(null);


        sentLettersRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                sentLetterRecyclerViewAdapter.notifyItemInserted(sentLettersMetadatas.size() - 1);
                sentLettersMetadatas.remove(sentLettersMetadatas.size() - 1);
                sentLetterRecyclerViewAdapter.notifyItemRemoved(sentLettersMetadatas.size());

                pageNum ++;

                final ArrayList<LetterMetadata> newReceivedLetterMetadatas = (ArrayList) ParseServerAccessor.getSentLetters(ParseUser.getCurrentUser(), pageNum);
                sentLettersMetadatas.addAll(newReceivedLetterMetadatas);

                sentLetterRecyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        });


    }



    class LoadLettersTask extends AsyncTask<Void, Void, Void> {

        private CheckLetterStatusActivity2 checkLetterStatusActivity2;

        public LoadLettersTask(CheckLetterStatusActivity2 checkLetterStatusActivity2)
        {
            this.checkLetterStatusActivity2 = checkLetterStatusActivity2;
        }

        @Override
        protected void onPreExecute() {
            // showDialog(AUTHORIZING_DIALOG);
        }

        @Override
        protected void onPostExecute(Void _void) {

            // Pass the result data back to the main activity
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

            setContentView(R.layout.activity_check_letter_status3);
            ButterKnife.bind(checkLetterStatusActivity2);

            populateView();

        }

        @Override
        protected Void doInBackground(Void... someVoids ) {

            //Do all your slow tasks here but dont set anything on UI
            //ALL ui activities on the main thread

            sentLettersMetadatas = (ArrayList)ParseServerAccessor.getSentLetters(ParseUser.getCurrentUser(), pageNum);
            if(sentLettersMetadatas.isEmpty())
            {
                Log.i("INFO", "in doInBackground, sentLetterMetadatas is empty ");
                youAreLazy = true;
            }

            Log.i("INFO", "Size of sentLetterMetadatas: " + sentLettersMetadatas.size());

            return null;
        }
    }

    private void setTextBoxesVisibility(Boolean youAreLazy) {

        if(youAreLazy)
        {
            youAreLazyTextView.setVisibility(View.VISIBLE);
            sentLettersTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            youAreLazyTextView.setVisibility(View.INVISIBLE);
            sentLettersTextView.setVisibility(View.VISIBLE);
        }
    }

}
