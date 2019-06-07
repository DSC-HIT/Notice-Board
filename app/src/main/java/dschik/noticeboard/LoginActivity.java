package dschik.noticeboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    MaterialButton register;
    EditText username, password;
    //private String[] userRoleString = new String[]{"admin", "student"};
    //String userrole = "admin";
    TextView signin;
    SharedPreferences sh;
    SharedPreferences.Editor shedit;
    String user_name;
    String pass_word;
    GoogleApiClient mGoogleApiClient;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("766540027212-2aprl8n1hp19j29q6olnjijfhn5oca11.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        register = (MaterialButton) findViewById(R.id.register_button);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_name = username.getText().toString();
                pass_word = password.getText().toString();

                if (TextUtils.isEmpty(pass_word)) {
                    password.setError("Invalid password");
                } else {
                    signin(user_name, pass_word);
                }

            }
        });


        signin = (TextView) findViewById(R.id.sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        findViewById(R.id.g_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        findViewById(R.id.forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setTitle("Enter your Email");
                final EditText input = new EditText(LoginActivity.this);
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
                                    Toast.makeText(LoginActivity.this, "Password Reset Email Sent!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Email Required!", Toast.LENGTH_LONG).show();

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

    }

    @Override
    public void onActivityResult(int req, int resultCode, Intent data) {
        super.onActivityResult(req, resultCode, data);
        if (req == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("aa", result.getStatus().getStatusCode() + "**");
            if (result.isSuccess()) {
                Log.d("aa", "pp");
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } else {
                Log.d("aa", "qq");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("aa", "firebaseAuthWithGoogle:" + acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("aa", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();

                            String name = "";
                            try {
                                name = user.getDisplayName();

                                shedit.putString("dis_email",user.getEmail());
                                shedit.putString("dis_name", name);
                                shedit.apply();


                                user_name = user.getEmail();
                                pass_word = user.getUid();//using ID as password


                                createUserAccountInFirestore();

                                //sending it to next activity
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);


                            } catch (NullPointerException n) {
                                n.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, name, Toast.LENGTH_LONG).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("aa", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }

    private void signin(final String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("aa", "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            if (user != null) {

                                shedit.putString("dis_email",username);
                                shedit.putString("dis_name", username.substring(0, username.indexOf('@')));
                                shedit.apply();


                                createUserAccountInFirestore();


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("aa", "signInWithEmail:failure" + task.getException().getMessage());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),

                                    Toast.LENGTH_SHORT).show();
                            Log.d("aa", task.getException() + "");
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void createUserAccountInFirestore()
    {
        //creating a user in DB and popullating it.

        String name = sh.getString("dis_name","name");
        String email = sh.getString("dis_email","email");

        Map<String,Object> user1 =new HashMap<>();
        user1.put("name",name);
        user1.put("email",email);
        //user1.put("fbuser",user);
        firestore.collection("userr").add(user1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(LoginActivity.this,"Successfully siged in!",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });   // DB operation complete
    }

}
