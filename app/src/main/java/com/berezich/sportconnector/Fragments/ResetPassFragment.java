package com.berezich.sportconnector.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.berezich.sportconnector.AlertDialogFragment;
import com.berezich.sportconnector.EndpointApi;
import com.berezich.sportconnector.ErrorVisualizer;
import com.berezich.sportconnector.MainActivity;
import com.berezich.sportconnector.R;

import java.io.IOException;

/**
 * Created by Sashka on 09.08.2015.
 */
public class ResetPassFragment extends Fragment implements EndpointApi.ResetPassAsyncTask.OnAction,
        AlertDialogFragment.OnActionDialogListener {

    private final String TAG = "MyLog_RPassFragment";
    private String pass;
    View rootView;

    ResetPassFragmentAction listenerResetPass = null;

    public ResetPassFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reset_pass, container, false);
        if(rootView!=null)
        {
            Button btn = (Button) rootView.findViewById(R.id.resetPass_btn_ok);
            if(btn!=null)
                btn.setOnClickListener(new OnClickResetPassListener());

            if(getActivity()!=null)
                ((MainActivity) getActivity()).setupUI(rootView);
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment targetFragment = getTargetFragment();
        if(targetFragment==null) {
            Log.e(TAG,"targetFragment should be set");
            throw new NullPointerException(String.format("For fragment %s targetFragment should be set", getFragment().toString()));
        }
        try {
            listenerResetPass =  (LoginFragment) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnActionListener for ResetPassFragment");
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }

    private class OnClickResetPassListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            String email="", name="";
            pass = "";
            EditText editTxt;
            if((editTxt = (EditText) rootView.findViewById(R.id.resetPass_email_value))!=null) {
                email = editTxt.getText().toString();
            }
            setVisibleProgressBar(true);
            new EndpointApi.ResetPassAsyncTask(getFragment()).execute(email);
        }
    }

    private void setVisibleProgressBar(boolean isVisible)
    {
        View view;
        if(rootView!=null) {
            if ((view = rootView.findViewById(R.id.resetPass_linearLayout))!=null)
                view.setVisibility(!isVisible ? View.VISIBLE : View.GONE);
            if ((view = rootView.findViewById(R.id.resetPass_frameLayout))!=null)
                view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResetPassAsyncTaskFinish(Exception result) {
        AlertDialogFragment dialog;
        Exception error = result;
        if(getActivity()==null)
        {
            Log.e(TAG, "current fragment isn't attached to activity");
            return;
        }
        if(error == null)
        {
            Log.d(TAG, "ResetPassReq was created");
            try {
                listenerResetPass.onResetPass(getString(R.string.resetPass_msgReqResetPass));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        Log.d(TAG,"error != null");

        String dialogMsg;
        Pair<ErrorVisualizer.ERROR_CODE,String> errTxtCode = ErrorVisualizer.getTextCodeOfRespException(getActivity().getBaseContext(),error);
        if(errTxtCode!=null && !errTxtCode.second.equals("")){
            dialogMsg = errTxtCode.second;
            Log.d(TAG,"resetPassError code = "+errTxtCode.first+" msg = "+errTxtCode.second);
        }
        else
            dialogMsg = getString(R.string.server_unknow_err);

        dialog = AlertDialogFragment.newInstance(dialogMsg, false);
        dialog.setTargetFragment(this, 0);
        FragmentManager ft = getActivity().getSupportFragmentManager();
        if(ft!=null)
            dialog.show(ft, "");
    }

    @Override
    public void onPositiveClick() {
        setVisibleProgressBar(false);
    }

    @Override
    public void onNegativeClick() {
        setVisibleProgressBar(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        android.support.v7.app.ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(getString(R.string.resetPass_fragmentTitle));
    }

    private Fragment getFragment(){
        return  this;
    }

    public interface ResetPassFragmentAction
    {
        void onResetPass(String msgResult);
    }

}