package hades.smsuploader;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by hades on 23/06/2014.
 */
public class AccountDialog extends DialogFragment implements View.OnClickListener {

    Button btnOK, btnCancel;
    TextView txtUsername, txtPassword, txtURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Account Manager");
        View view = inflater.inflate(R.layout.dialog_account, null);

        txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtPassword = (TextView) view.findViewById(R.id.txtPassword);
        txtURL = (TextView) view.findViewById(R.id.txtURL);

        btnOK = (Button) view.findViewById(R.id.btnSaveAcc);
        btnCancel = (Button) view.findViewById(R.id.btnCancelAcc);

        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        SharedPreferences settings = getActivity().getSharedPreferences(
                SUPPORT_CONSTANTS.PREFS_NAME, Context.MODE_PRIVATE);
        String username = settings.getString("username", null);
        String password = settings.getString("password", null);
        String url = settings.getString("url", null);

        txtUsername.setText(username);
        txtPassword.setText(password);
        txtURL.setText(url);

        return view;
    }

    @Override
    public void onClick(View view) {

        //TextView txtUsername = (TextView)

        switch (view.getId()) {
            case R.id.btnSaveAcc:
                SharedPreferences settings = getActivity().getSharedPreferences(
                        SUPPORT_CONSTANTS.PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", txtUsername.getText().toString());
                editor.putString("password", txtPassword.getText().toString());
                editor.putString("url", txtURL.getText().toString());
                editor.commit();

                dismiss();
                break;
            case R.id.btnCancelAcc:
                dismiss();
                break;
        }
    }
}
