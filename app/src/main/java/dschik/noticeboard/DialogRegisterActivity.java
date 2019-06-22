package dschik.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DialogRegisterActivity extends DialogFragment {
    private ProgressDialog progressDialog;

    private DialogRegisterActivity.DialogRegisterListener listener;

    DialogRegisterActivity(ProgressDialog progressDialog) {
        super();
        this.progressDialog = progressDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogRegisterActivity.DialogRegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final TextInputEditText usertext, userpass;

        final Context context = getActivity();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_login, null);

        usertext = view.findViewById(R.id.login_username);
        userpass = view.findViewById(R.id.login_password);

        TextView dont = view.findViewById(R.id.dont_have);
        dont.setText("Already have an Account!!! ");
        TextView signin = view.findViewById(R.id.sign_in);
        signin.setText(R.string.login1);

        TextView forget = view.findViewById(R.id.forget);
        forget.setVisibility(View.GONE);
        forget.setClickable(false);

        MaterialButton mb = view.findViewById(R.id.register_button);
        mb.setText(R.string.register);


        alertDialog.setView(view).setTitle("Login Info");

        view.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = usertext.getText().toString();
                String pass = userpass.getText().toString();
                if (TextUtils.isEmpty(user)) {
                    usertext.setError("Required");
                } else if (TextUtils.isEmpty(pass)) {
                    userpass.setError("Required");
                } else {
                    progressDialog.show();
                    listener.applyData(user, pass);
                }
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        return alertDialog.create();
    }

    public interface DialogRegisterListener {
        public void applyData(String username, String password);
    }
}
