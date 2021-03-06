package com.berezich.sportconnector.PersonProfile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.berezich.sportconnector.InputValuesValidation;
import com.berezich.sportconnector.R;

public class ChangeEmailFragment extends DialogFragment {
    private static final String EMAIL = "oldEmail";
    private OnActionEmailDialogListener listener;
    private View rootView;

    public ChangeEmailFragment() {
    }

    public static ChangeEmailFragment newInstance(String oldEmail) {
        ChangeEmailFragment frag = new ChangeEmailFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, oldEmail);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            if(getTargetFragment()==null)
                throw new NullPointerException("Need to set targetFragment for ChangeEmailFragment");
            try {
                listener = (OnActionEmailDialogListener)getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement OnActionEmailDialogListener for ChangeEmailFragment");
            }
            final String oldEmail = getArguments().getString(EMAIL);
            Dialog dialog = getDialog();
            dialog.setTitle(R.string.changeEmail_dialogTitle);

            rootView = inflater.inflate(R.layout.fragment_change_email, null);
            if(rootView!=null) {
                TextView txtView = (TextView) rootView.findViewById(R.id.changeEmail_txtEdt_old);
                if(txtView!=null) {
                    txtView.setText(oldEmail);
                }
                EditText txtEdt = (EditText) rootView.findViewById(R.id.changeEmail_txtEdt_new);
                if(txtEdt!=null)
                    txtEdt.requestFocus();
                rootView.findViewById(R.id.changeEmail_btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                rootView.findViewById(R.id.changeEmail_btnOk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            EditText edtNew,edtOld;
                            String newEmailStr ="";
                            if(rootView!=null) {

                                if ((edtNew = (EditText) rootView.findViewById(R.id.changeEmail_txtEdt_new)) != null) {
                                    newEmailStr = edtNew.getText().toString();
                                    edtNew.setError(null);
                                }
                                if((edtOld = (EditText) rootView.findViewById(R.id.changeEmail_txtEdt_old))!=null) {
                                    if(edtOld.getText()!=null)
                                        if (newEmailStr.equals(edtOld.getText().toString())) {
                                            if(edtNew!=null)
                                                edtNew.setError(getString(R.string.changeEmail_errOldNew_matches));
                                            return;
                                        }
                                }
                                if(!InputValuesValidation.isValidEmail(newEmailStr)) {
                                    if (edtNew!=null)
                                        edtNew.setError(getString( R.string.changeEmail_errNew_invalid));
                                    return;
                                }
                                if(edtNew!=null)
                                    listener.onChangeEmailClick(edtNew.getText().toString());
                            }
                            dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });



            }
            return rootView;
        } catch (Exception e) {
            e.printStackTrace();
            return rootView=null;
        }
    }

    public interface OnActionEmailDialogListener
    {
        void onChangeEmailClick(String newEmail);
    }
}
