package dschik.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DialogLoginActivity.DialogLoginListener {

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
    private FirebaseDatabase db;
    private DatabaseReference dbref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
            public void onClick(View v) {
                DialogLoginActivity dialogLoginActivity = new DialogLoginActivity();
                dialogLoginActivity.show(getSupportFragmentManager(), "login frag");
            }
        });




        findViewById(R.id.g_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
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


                                createUserAccountInDB();

                                //sending it to next activity
                                //Intent i = new Intent(LoginActivity.this, MainActivity.class);
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


                                createUserAccountInDB();


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


    private void createUserAccountInDB()
    {
        //creating a user in DB and popullating it.

        String name = sh.getString("dis_name","name");
        String email = sh.getString("dis_email","email");
        String dept = sh.getString("dis_dept", "dept");
        String year = sh.getString("dis_year", "year");

        UserObj user1 = new UserObj(name, email, "", dept, year);
        //insert user info in db here
        dbref.child("user").child(getPath(email)).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(LoginActivity.this, "Authentication Passed", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        // DB operation complete
    }

    @NonNull
    private String getPath(String email) {

        return email.replace(".","");
    }


    @Override
    public void applyData(String username, String password) {
        signin(username, password);
    }
}
