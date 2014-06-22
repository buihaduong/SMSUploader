package hades.smsuploader;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hades on 18/06/2014.
 */
public class AddNumberDialog extends DialogFragment implements View.OnClickListener {

    Button btnAddNumber, btnCancel;
    EditText txtNewNumber;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Add New Number");
        View view = inflater.inflate(R.layout.dialog_add_number, null);

        btnAddNumber = (Button)view.findViewById(R.id.btnAddNumber);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        txtNewNumber = (EditText) view.findViewById(R.id.txtNewNumber);

        btnAddNumber.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnAddNumber:
                communicator.onDialogMessage(txtNewNumber.getText().toString());
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }
    interface Communicator
    {
        public void onDialogMessage(String message);
    }
}
