package dschik.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends Activity {

    Button signin;
    EditText username,password;
    TextView register;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";

    private FirebaseAuth mAuth;

    SharedPreferences sh;
    SharedPreferences.Editor shedit;
    @Override
    public void onCreate(Bundle savedinstances) {
        super.onCreate(savedinstances);
        setContentView(R.layout.sign_up);
        signin=(Button)findViewById(R.id.signin_button);
        username=(EditText)findViewById(R.id.signin_username);
        password=(EditText)findViewById(R.id.signin_password);

        mAuth = FirebaseAuth.getInstance();

        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name=username.getText().toString();
                String pass_word=password.getText().toString();

                if (TextUtils.isEmpty(pass_word)) {
                    password.setError("Invalid password");
                }
                else if (TextUtils.isEmpty(user_name)){
                    username.setError("Invalid username");
                }
                else {
                    createAccount(user_name,pass_word);
                }
            }
        });


        register=(TextView)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    private void createAccount(String username, String password)
    {
        mAuth.createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("aa", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            Toast.makeText(SignUpActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("aa", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }
}
