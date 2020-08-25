package com.meghamit.mac.otterapp.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LetterFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LetterFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LetterFragment newInstance(int columnCount) {
        LetterFragment fragment = new LetterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata;
            if(isSentLettersTab()) {
                letterMetadata = getSentLetters();
            }
            else
            {
                letterMetadata = getReceivedLetters();
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(letterMetadata, mListener));
        }
        return view;
    }

    private Boolean isSentLettersTab() {

        final Integer columnNumber = (Integer)this.getArguments().get(ARG_COLUMN_COUNT);
        Log.i("INFO", "COLUMN NUM IS : " + columnNumber);

        //first tab is sent letters , second one is received letters
        if (columnNumber == 0) {
            return true;
        }
        else
        {
            return false;
        }
    }

    private List<com.meghamit.mac.otterapp.pojo.LetterMetadata> getReceivedLetters() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.LetterMetadata.class);
        Log.i("INFO", "To is: " + parseUser.get(Constants.User.POST_BOX));
        try {
            letterMetadata =   query.whereEqualTo(Constants.LetterMetadata.TO_POSTBOX, parseUser.get(Constants.User.POST_BOX)).find();
            Log.i("INFO", "Query result is " + letterMetadata.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return letterMetadata;
    }
    public List<com.meghamit.mac.otterapp.pojo.LetterMetadata> getSentLetters() {

        ParseUser parseUser = ParseUser.getCurrentUser();
        List<com.meghamit.mac.otterapp.pojo.LetterMetadata> letterMetadata = new ArrayList<>();

        ParseQuery<com.meghamit.mac.otterapp.pojo.LetterMetadata> query = ParseQuery.getQuery(com.meghamit.mac.otterapp.pojo.LetterMetadata.class);
        Log.i("INFO", "From is: " + parseUser.get(Constants.User.POST_BOX));
        try {
          letterMetadata =   query.whereEqualTo(Constants.LetterMetadata.FROM_POSTBOX, parseUser.get(Constants.User.POST_BOX)).find();
          Log.i("INFO", "Query result is " + letterMetadata.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return letterMetadata;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(com.meghamit.mac.otterapp.pojo.LetterMetadata item);
    }
}
