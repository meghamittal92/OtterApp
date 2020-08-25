package com.meghamit.mac.otterapp.postOffice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.constants.LetterStatus;
import com.meghamit.mac.otterapp.pojo.LetterMetadata;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SentLetterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ArrayList<LetterMetadata> sentLetterMetadatas;
    private Context mContext;

    public SentLetterRecyclerViewAdapter(Context mContext, ArrayList<LetterMetadata> sentLetterMetadatas) {

        this.sentLetterMetadatas = sentLetterMetadatas;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentletterlistitem, parent, false);
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
        LetterMetadata sentLetterMetadataListItem = sentLetterMetadatas.get(position);
        String from = "To PO BOX No. " + sentLetterMetadataListItem.getToPostBox();
        holder.lineA.setText(sentLetterMetadataListItem.getTitle());
        holder.lineB.setText(from);
        //holder.imageView.setImageResource(R.drawable.ic_mail);
        String letterStatus = sentLetterMetadataListItem.getStatus();
        if(!(LetterStatus.SENT.toString().equals(letterStatus) || LetterStatus.OPENED.toString().equals(letterStatus))) {
                 holder.parentLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.iron));
            holder.imageView.setImageResource(R.drawable.ic_mail);

        }


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final com.meghamit.mac.otterapp.pojo.LetterMetadata item = sentLetterMetadatas.get(position);
                Intent intent = new Intent(mContext, SentLetterActivity.class);
                intent.putExtra(Constants.LetterMetadata.TITLE, item.getTitle());
                intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
                intent.putExtra(Constants.LetterMetadata.DATE_RECEIVED, item.getDateReceived());
                intent.putExtra(Constants.LetterMetadata.FROM_POSTBOX, item.getFromPostBox());
                intent.putExtra(Constants.LetterMetadata.TO_POSTBOX, item.getToPostBox());
                intent.putExtra(Constants.LetterMetadata.STATUS, item.getStatus());
                intent.putExtra(Constants.LetterMetadata.OBJECT_ID, item.getObjectId());
                intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
                intent.putExtra(Constants.LetterMetadata.DAYS_IN_TRANSIT, item.getDaysInTransit());
                mContext.startActivity(intent);
            }
        });
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        //viewHolder.progressBar.

    }


    @Override
    public int getItemCount() {

        return sentLetterMetadatas == null ? 0 : sentLetterMetadatas.size();
    }
    @Override
    public int getItemViewType(int position) {
        return sentLetterMetadatas.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView lineA;
        TextView lineB;
        ImageView imageView;

        ConstraintLayout parentLayout;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lineA = itemView.findViewById(R.id.line_a);
            lineB = itemView.findViewById(R.id.line_b);
            imageView = itemView.findViewById(R.id.imageView);
            parentLayout = itemView.findViewById(R.id.sentletterlistitem);
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
