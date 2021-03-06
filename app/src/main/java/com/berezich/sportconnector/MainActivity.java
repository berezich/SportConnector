package com.berezich.sportconnector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.berezich.sportconnector.Fragments.LoginFragment;
import com.berezich.sportconnector.Fragments.MainFragment;
import com.berezich.sportconnector.Fragments.MainFragment.Filters;
import com.berezich.sportconnector.Fragments.NavigationDrawerFragment;
import com.berezich.sportconnector.GoogleMap.GoogleMapFragment;
import com.berezich.sportconnector.PersonProfile.PersonProfileFragment;
import com.berezich.sportconnector.SpotInfo.SpotInfoFragment;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleMapFragment.OnActionListenerGMapFragment,
        MainFragment.OnActionListenerMainFragment,
        LoginFragment.OnActionListenerLoginFragment {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle="";
    private static final String TAG = "MyLog_MainActivity";
    private boolean isRecover = false;
    private boolean isInstanceStateSaved = false;
    private boolean isSpotsSynced=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null)
                isRecover = true;
            setContentView(R.layout.activity_main);

            Log.d(TAG, "------SpotConnector started-------");
            LocalDataManager.init(this);

            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(drawerLayout!=null)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (savedInstanceState != null) {
                mNavigationDrawerFragment = (NavigationDrawerFragment)
                        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
                // Set up the drawer.
                mNavigationDrawerFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer_layout));
                mNavigationDrawerFragment.setMenuVisibility(true);
                mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
            else {
                fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment().setArgs(-1)).commit();
                Log.d(TAG, String.format("prev fragment replaced with %s", LoginFragment.class.getName()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            isInstanceStateSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        try {
            super.onResume();
            isInstanceStateSaved = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        try {
            if(isInstanceStateSaved)
                return;
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(isRecover){
                isRecover=false;
                return;
            }
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }
            switch (position) {
                case 0:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new MainFragment().setArgs(position))
                        .commit();
                    Log.d(TAG, String.format("prev fragment replaced with %s", MainFragment.class.getName()));

                    break;
                case 1:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new PersonProfileFragment().setArgs(position, true, null))
                            .commit();
                    Log.d(TAG, String.format("prev fragment replaced with %s", PersonProfileFragment.class.getName()));
                    break;
                default:
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new LoginFragment().setArgs(position))
                            .commit();
                    Log.d(TAG, String.format("prev fragment replaced with %s", LoginFragment.class.getName()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerBottomItemSelected(int position) {
        try {
            switch (position){
                case 0://logout
                    AppPref appPref = LocalDataManager.getAppPref();
                    if( appPref==null)
                        appPref = new AppPref(false);
                    else
                        appPref.setIsAutoLogin(false);
                    try {
                        LocalDataManager.saveAppPref(appPref,this);
                        LocalDataManager.saveMyPersonInfoToPref(LocalDataManager.getMyPersonInfo().setPass(""), this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mNavigationDrawerFragment.setMenuVisibility(false);
                    mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i)
                        fragmentManager.popBackStack();
                    fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment().setArgs(-1)).commit();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBtnClickMF(Filters filter, int sectionNumber)
    {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            GoogleMapFragment fragment = new GoogleMapFragment().setArgs(filter);
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.getClass().getName()).commit();
            Log.d(TAG, String.format("prev fragment replaced with %s", fragment.getClass().getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthorized() {

        try {
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mTitle = getTitle();
            // Set up the drawer.
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
            mNavigationDrawerFragment.setMenuVisibility(true);
            mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mNavigationDrawerFragment.selectItem(0);
            onSectionAttached(0);
            restoreActionBar();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.mainSearch_fragmentTitle);
                break;
            case 1:
                mTitle = getString(R.string.personprofile_myProfile_fragmentTitle);
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
        if(mTitle!=null &&( mTitle.equals(getString(R.string.mainSearch_fragmentTitle)) ||
                mTitle.equals(getString(R.string.personprofile_myProfile_fragmentTitle))||
                mTitle.equals(getString(R.string.app_name)))) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        }
        else {
            actionBar.setHomeAsUpIndicator(null);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        try {
            super.onPostCreate(savedInstanceState);
            // Sync the toggle state after onRestoreInstanceState has occurred.

            if(mNavigationDrawerFragment!=null) {
                ActionBarDrawerToggle mDrawerToggle = mNavigationDrawerFragment.getDrawerToggle();
                if (mDrawerToggle != null)
                    mDrawerToggle.syncState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if(mNavigationDrawerFragment!=null) {
                ActionBarDrawerToggle mDrawerToggle = mNavigationDrawerFragment.getDrawerToggle();
                if (mDrawerToggle != null)
                    mDrawerToggle.onConfigurationChanged(newConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (mNavigationDrawerFragment!=null && !mNavigationDrawerFragment.isDrawerOpen()) {
                // Only show items in the action bar relevant to this screen
                // if the drawer is not showing. Otherwise, let the drawer
                // decide what to show in the action bar.
                getMenuInflater().inflate(R.menu.main, menu);
                restoreActionBar();
                return true;
            }
            else
                restoreActionBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClickGMF(Long spotId) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SpotInfoFragment fragment = SpotInfoFragment.newInstance(spotId);
            fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragment.getClass().getName()).commit();
            Log.d(TAG, String.format("prev fragment replaced with %s", fragment.getClass().getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            if(mNavigationDrawerFragment!=null && mNavigationDrawerFragment.isDrawerOpen())
                mNavigationDrawerFragment.close();
            else
                super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmTitle(String title){
        mTitle = title;
    }

    public boolean isSpotsSynced() {
        return isSpotsSynced;
    }

    public void setIsSpotsSynced(boolean isSpotsSynced) {
        this.isSpotsSynced = isSpotsSynced;
    }
}
