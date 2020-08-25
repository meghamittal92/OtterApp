package com.meghamit.mac.otterapp.unused;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.meghamit.mac.otterapp.LoginActivity;
import com.meghamit.mac.otterapp.LogoutActivity;
import com.meghamit.mac.otterapp.R;
import com.meghamit.mac.otterapp.constants.Constants;
import com.meghamit.mac.otterapp.fragment.LetterFragment;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

@Deprecated
public class UserHomeActivity extends AppCompatActivity implements LetterFragment.OnListFragmentInteractionListener{

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        // boolean finish = getIntent().getBooleanExtra("finish", false);

        if(ParseUser.getCurrentUser() == null) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            //finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.attachImageFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SendLetterActivity.class));

            }
        });

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

    @Override
    public void onListFragmentInteraction(com.meghamit.mac.otterapp.pojo.LetterMetadata item) {

        Log.i("INFO", "in onListFragmentInteraction, letter title is:  " + item.getTitle());
        Intent intent = new Intent(this, LetterViewActivity.class);
        intent.putExtra(Constants.LetterMetadata.TITLE, item.getTitle());
        intent.putExtra(Constants.LetterMetadata.DATE_SENT, item.getDateSent());
        intent.putExtra(Constants.LetterMetadata.DATE_RECEIVED, item.getDateReceived());
        intent.putExtra(Constants.LetterMetadata.FROM_POSTBOX, item.getFromPostBox());
        intent.putExtra(Constants.LetterMetadata.TO_POSTBOX, item.getToPostBox());
        intent.putExtra(Constants.LetterMetadata.STATUS, item.getStatus());
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = LetterFragment.newInstance(position);
            return fragment;
        }

        @Override
        public int getCount() {

            return 2;
        }
    }
}
