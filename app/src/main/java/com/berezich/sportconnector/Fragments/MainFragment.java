package com.berezich.sportconnector.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.berezich.sportconnector.EndpointApi.SyncSpots;
import com.berezich.sportconnector.ErrorVisualizer;
import com.berezich.sportconnector.MainActivity;
import com.berezich.sportconnector.R;

public class MainFragment extends Fragment implements SyncSpots.OnActionSyncSpots {

    public enum Filters {
        SPARRING_PARTNERS, COUCH, COURT
    };
    Activity activity;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final String TAG = "MyLog_mainFragment";
    int _sectionNumber;
    View rootView;
    OnActionListenerMainFragment listener;
    private static Long regionId = Long.valueOf(1);
    private SyncSpots syncSpots;

    public MainFragment setArgs(int sectionNumber) {
        try {
            _sectionNumber = sectionNumber;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            this.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public MainFragment() {
        try {
            syncSpots = new SyncSpots(this,TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button btn = (Button) rootView.findViewById(R.id.main_frg_btn1);
            btn.setOnClickListener(new BtnClickListener());
            btn = (Button) rootView.findViewById(R.id.main_frg_btn2);
            btn.setOnClickListener(new BtnClickListener());
            btn = (Button) rootView.findViewById(R.id.main_frg_btn3);
            btn.setOnClickListener(new BtnClickListener());

            TextView textView = (TextView) rootView.findViewById(R.id.main_frg_tryAgain_txtView);
            if(textView!=null) {
                textView.setOnClickListener(new TryAgainClickListener());
            }
            return rootView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            listener = (OnActionListenerMainFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnActionListener for MainFragment");
        }
        try {
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume()
    {
        try {
            super.onResume();
            MainActivity mainActivity = (MainActivity)activity;
            Log.d(TAG, "onResume isSpotsSynced = " + mainActivity.isSpotsSynced());
            mainActivity.setmTitle(activity.getString(R.string.mainSearch_fragmentTitle));
            ActionBar actionBar = mainActivity.getSupportActionBar();
            if(actionBar!=null)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            mainActivity.restoreActionBar();
            if(mainActivity.isSpotsSynced())
                setVisibleLayouts(true,false);
            else
                reqExecute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class BtnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            try {
                Button btn;
                try {
                    btn = (Button) view;
                }
                catch (ClassCastException e)
                {
                    throw new ClassCastException(view.toString() + "must be a Button");
                }
                switch (btn.getId())
                {
                    case R.id.main_frg_btn1:
                        listener.onBtnClickMF(Filters.SPARRING_PARTNERS, _sectionNumber);
                        break;
                    case R.id.main_frg_btn2:
                        listener.onBtnClickMF(Filters.COUCH, _sectionNumber);
                        break;
                    case R.id.main_frg_btn3:
                        listener.onBtnClickMF(Filters.COURT, _sectionNumber);
                        break;
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
    }

    public interface OnActionListenerMainFragment {
        void onBtnClickMF(Filters position, int section);
    }


    private void setVisibleLayouts(boolean relativeLayout, boolean frameLayout)
    {
        if(relativeLayout)
            rootView.findViewById(R.id.main_frg_relativeLayout).setVisibility(View.VISIBLE);
        else
            rootView.findViewById(R.id.main_frg_relativeLayout).setVisibility(View.GONE);
        if(frameLayout)
            rootView.findViewById(R.id.main_frg_frameLayout).setVisibility(View.VISIBLE);
        else
            rootView.findViewById(R.id.main_frg_frameLayout).setVisibility(View.GONE);
    }
    private void setVisibleProgressBar()
    {
        RelativeLayout mainLayout;
        FrameLayout frameLayout;
        if(rootView!=null) {
            if((frameLayout = (FrameLayout) rootView.findViewById(R.id.main_frg_frameLayout))!=null)
                ErrorVisualizer.showProgressBar(frameLayout);
            if((mainLayout = (RelativeLayout) rootView.findViewById(R.id.main_frg_relativeLayout))!=null)
                mainLayout.setVisibility(View.GONE);
        }
    }
    private class TryAgainClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {

            try {
                reqExecute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void reqExecute()
    {
        syncSpots.startSync(activity.getBaseContext(),regionId);
        setVisibleProgressBar();
    }

    @Override
    public void syncFinish(Exception ex, SyncSpots.ReqState reqState) {
        try {
            MainActivity mainActivity = (MainActivity) activity;
            if(ex!=null) {
                ErrorVisualizer.showErrorAfterReq(activity.getBaseContext(),
                        (FrameLayout) rootView.findViewById(R.id.main_frg_frameLayout), ex, TAG);
                mainActivity.setIsSpotsSynced(false);
            }
            else if(reqState== SyncSpots.ReqState.EVERYTHING_LOADED) {
                setVisibleLayouts(true, false);
                mainActivity.setIsSpotsSynced(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            super.onCreateOptionsMenu(menu, inflater);
            menu.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
