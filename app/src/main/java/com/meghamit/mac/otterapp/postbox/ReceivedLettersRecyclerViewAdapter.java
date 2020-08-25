package com.meghamit.mac.otterapp.postbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.accessor.ParseServerAccessor;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.Letter;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;
import com.parse.ParseFile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.meghamit.mac.otterapp.constants.Constants.RECEIVED_LETTER_ACTIVITY_REQUEST_CODE;

public class ReceivedLettersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<LetterMetadata> receivedLetterMetadatas;
    private Context mContext;

    public ReceivedLettersRecyclerViewAdapter(Context mContext, ArrayList<LetterMetadata> receivedLetterMetadatas) {

        this.receivedLetterMetadatas = receivedLetterMetadatas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.twolines, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_dialog, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }
    private void populateItemRows(@NonNull ItemViewHolder holder, int position) {

        Log.d(TAG, "On bindViewHolder called");
        LetterMetadata receivedLetterListItem = receivedLetterMetadatas.get(position);
        String from = "From PO BOX No. " + receivedLetterListItem.getFromPostBox();
        holder.lineA.setText(receivedLetterListItem.getTitle());
        holder.lineB.setText(from);
        String letterStatus = receivedLetterListItem.getStatus();
        if(LetterStatus.OPENED.toString().equals(letterStatus)) {
       //     holder.parentLayout.setBackgroundColor();
            holder.parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final com.meghamit.mac.otterapp.pojo.LetterMetadata item = receivedLetterMetadatas.get(position);
                Letter letter = ParseServerAccessor.getLetter(item);
                if(letter != null)
                {
                    ParseFile parseFile = (ParseFile) letter.get(Constants.Letter.LETTER_DATA);
                    ParseFile imageFile = (ParseFile) letter.get(Constants.Letter.LETTER_IMAGE);
                    Boolean isLetterImagePresent = false;

                    if(imageFile != null)
                    {
                        isLetterImagePresent = true;


                    }
                    String letterData = ParseServerAccessor.readFile(parseFile);
                    Log.i("INFO", "Char sequence is: !!!" + letterData);

                    Intent intent = new Intent(mContext, ReceivedLetter2Activity.class);

                    intent.putExtra(Constants.Letter.LETTER_DATA, letterData);
                    intent.putExtra(Constants.IntentExtra.IS_LETTER_IMAGE_PRESENT, isLetterImagePresent);
                    intent.putExtra(Constants.Letter.OBJECT_ID, letter.getObjectId());
                    intent.putExtra(Constants.LetterMetadata.FROM_POSTBOX, item.getFromPostBox());
                    intent.putExtra(Constants.LetterMetadata.STATUS, item.getStatus());

                    if(!LetterStatus.OPENED.toString().equals(item.getStatus())) {

                        item.setStatus(LetterStatus.OPENED);
                        item.saveInBackground();
                    }


                    ((Activity) mContext).startActivityForResult(intent, RECEIVED_LETTER_ACTIVITY_REQUEST_CODE);
                }
                else
                {
                    Toast.makeText(mContext, "Problem loading letter", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        //viewHolder.progressBar.

    }


    @Override
    public int getItemCount() {

        return receivedLetterMetadatas == null ? 0 : receivedLetterMetadatas.size();
    }
    @Override
    public int getItemViewType(int position) {
        return receivedLetterMetadatas.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView lineA;
        TextView lineB;

        ConstraintLayout parentLayout;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lineA = itemView.findViewById(R.id.line_a);
            lineB = itemView.findViewById(R.id.line_b);
            parentLayout = itemView.findViewById(R.id.twolines);
        }


    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
