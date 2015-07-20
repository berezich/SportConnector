package com.berezich.sportconnector;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.berezich.sportconnector.GoogleMap.GoogleMapFragment;
import com.berezich.sportconnector.GoogleMap.SpotsData;
import com.berezich.sportconnector.MainFragment.Filters;
import com.berezich.sportconnector.PersonProfile.PersonProfileFragment;
import com.berezich.sportconnector.SpotInfo.SpotInfoFragment;
import com.berezich.sportconnector.backend.sportConnectorApi.model.RegionInfo;
import com.berezich.sportconnector.backend.sportConnectorApi.model.Spot;
import com.berezich.sportconnector.backend.sportConnectorApi.model.UpdateSpotInfo;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleMapFragment.OnActionListenerGMapFragment,
        MainFragment.OnActionListenerMainFragment {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*RegionInfo regionInfo = new RegionInfo();
        regionInfo.setId(regionId);
        regionInfo.setVersion("1.0.0.1");
        regionInfo.setLastSpotUpdate(new DateTime(new Date().getTime()-24*6*60*60*1000));
        regionInfo.setRegionName("moscow");
        try {
            LocalDataManager.saveRegionInfoToPref(regionInfo,this);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        LocalDataManager.init(this.getBaseContext());

        try {
            LocalDataManager.loadMyPersonInfoFromPref(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

       /*
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.setMenuVisibility(false);
        */
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment().setArgs(-1)).commit();
    }
    @Override
    protected void onResume()
    {
        super.onResume();

    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, new MainFragment().setArgs(position ))
                    .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PersonProfileFragment().setArgs(position))
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new LoginFragment().setArgs(position))
                        .commit();
        }
    }
    @Override
    public void onBtnClickMF(Filters filter, int sectionNumber)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.container, new YaMapFragment().setArgs(sectionNumber,filter)).commit();
        fragmentManager.beginTransaction().replace(R.id.container, new GoogleMapFragment().setArgs(sectionNumber,filter)).addToBackStack("tr1").commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.frame_main_title);
                break;
            case 1:
                mTitle = getString(R.string.frame_profile_title);
                break;
            case 2:
                mTitle = getString(R.string.frame_msg_title);
                break;
            case 3:
                mTitle = getString(R.string.frame_friends_title);
                break;
            case 4:
                mTitle = getString(R.string.frame_photo_title);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment!=null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClickGMF(Long spotId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.container, new YaMapFragment().setArgs(sectionNumber,filter)).commit();
        //fragmentManager.beginTransaction().replace(R.id.container, new GoogleMapFragment().setArgs(sectionNumber,filter)).commit();
        fragmentManager.beginTransaction().replace(R.id.container, SpotInfoFragment.newInstance(spotId)).addToBackStack("tr2").commit();

    }



}
