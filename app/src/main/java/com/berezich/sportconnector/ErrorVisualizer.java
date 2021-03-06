package com.berezich.sportconnector;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.net.UnknownHostException;


public class ErrorVisualizer {
    public enum  ERROR_CODE {UNKNOWN_SRV_ERR,HOST_UNREACHABLE,AUTH_FAILED,REGISTRATION_FAILED,APP_VERSION_FAILED,OAUTH_FAILED};
    public static void showErrorAfterReq(Context context, FrameLayout layout, Exception e, String TAG)
    {
        ProgressBar progressBar;
        TextView textView;
        LinearLayout linearLayout;
        if(e!=null) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        Pair<ERROR_CODE,String> errCodeTxt = getTextCodeOfRespException(context,e);
        if(layout!=null)
        {
            layout.setVisibility(View.VISIBLE);
            if((progressBar = (ProgressBar) layout.getChildAt(0))!=null)
                progressBar.setVisibility(View.GONE);
            if((linearLayout = (LinearLayout) layout.getChildAt(1))!=null)
            {
                linearLayout.setVisibility(View.VISIBLE);
                if((textView = (TextView) linearLayout.getChildAt(0))!=null) {
                    textView.setText(errCodeTxt.second);
                    textView.setVisibility(View.VISIBLE);
                }
                if((textView = (TextView) linearLayout.getChildAt(1))!=null)
                    if((errCodeTxt.first == ERROR_CODE.HOST_UNREACHABLE)) {
                        textView.setText(context.getString(R.string.try_again));
                        textView.setVisibility(View.VISIBLE);
                    }
                    else
                        textView.setVisibility(View.GONE);

            }
        }
    }
    public static void showProgressBar(FrameLayout layout)
    {
        ProgressBar progressBar;
        LinearLayout linearLayout;
        if(layout!=null)
        {
            layout.setVisibility(View.VISIBLE);
            if((progressBar = (ProgressBar) layout.getChildAt(0))!=null)
                progressBar.setVisibility(View.VISIBLE);
            if((linearLayout = (LinearLayout) layout.getChildAt(1))!=null)
                linearLayout.setVisibility(View.GONE);
        }
    }
    public static Pair<ERROR_CODE,String> getTextCodeOfRespException(Context context, Exception exception)
    {
        String errMsg=context.getString(R.string.server_unknown_err);
        ERROR_CODE error_code = ERROR_CODE.UNKNOWN_SRV_ERR;
        try {
            if (exception != null)
                if (exception instanceof UnknownHostException) {
                    errMsg = context.getString(R.string.host_unreachable_err);
                    error_code = ERROR_CODE.HOST_UNREACHABLE;
                } else if (exception instanceof TokenResponseException) {
                    TokenResponseException ex = (TokenResponseException) exception;
                    error_code = ERROR_CODE.OAUTH_FAILED;
                    String msg = ex.getDetails().getError();
                    if (msg.equals("invalid_grant")) {
                        errMsg = context.getString(R.string.oauthFailed_invalidGrand);
                    }

                } else if (exception instanceof GoogleJsonResponseException) {
                    GoogleJsonResponseException appError = (GoogleJsonResponseException) exception;
                    String errExceptMsg = appError.getDetails().getMessage();
                    if (errExceptMsg.indexOf("AuthFailed@:") == 0) {
                        errMsg = context.getString(R.string.login_err_authorized);
                        error_code = ERROR_CODE.AUTH_FAILED;
                    } else if (errExceptMsg.indexOf("needUpdateApp@:") == 0) {
                        errMsg = context.getString(R.string.server_needUpdateApp_err);
                        error_code = ERROR_CODE.APP_VERSION_FAILED;
                    } else if (errExceptMsg.indexOf("loginExists@:") == 0) {
                        errMsg = context.getString(R.string.registration_err_loginExists);
                        error_code = ERROR_CODE.REGISTRATION_FAILED;
                    } else if (errExceptMsg.indexOf("idNull@:") == 0) {
                        errMsg = context.getString(R.string.registration_err_idNull);
                        error_code = ERROR_CODE.REGISTRATION_FAILED;
                    } else if (errExceptMsg.indexOf("nameNull@:") == 0) {
                        errMsg = context.getString(R.string.registration_err_nameNull);
                        error_code = ERROR_CODE.REGISTRATION_FAILED;
                    } else if (errExceptMsg.indexOf("passNull@:") == 0) {
                        errMsg = context.getString(R.string.registration_err_pass_empty);
                        error_code = ERROR_CODE.REGISTRATION_FAILED;
                    } else if (errExceptMsg.indexOf("emailNotExists@:") == 0) {
                        errMsg = context.getString(R.string.resetPass_err_loginNotExists);
                        error_code = ERROR_CODE.REGISTRATION_FAILED;
                    }
                }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  new Pair<>(error_code,errMsg);

    }
    public static String getDebugMsgOfRespException(Exception exception)
    {
        String errMsg="exception == null";
        try {
            if(exception!=null)
                if (exception instanceof GoogleJsonResponseException) {
                    GoogleJsonResponseException appError = (GoogleJsonResponseException) exception;
                    errMsg = appError.getDetails().getMessage();

                }
                else
                    errMsg = exception.getMessage();

        }
        catch (Exception ex){
            errMsg = "get error msg failed";
        }
        return  errMsg;

    }
}
