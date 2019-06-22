package dschik.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class DialogLoginActivity extends DialogFragment {
    private final ProgressDialog progressDialog;
    private DialogLoginListener listener;

    DialogLoginActivity(ProgressDialog progressDialog) {
        super();
        this.progressDialog = progressDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogLoginListener) context;
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

        TextView signin = (TextView) view.findViewById(R.id.sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Enter your Email");
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        if (!TextUtils.isEmpty(email)) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Password Reset Email Sent!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(context, "Email Required!", Toast.LENGTH_LONG).show();

                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        return alertDialog.create();
    }

    public interface DialogLoginListener {
        public void applyData(String username, String password);
    }
}
