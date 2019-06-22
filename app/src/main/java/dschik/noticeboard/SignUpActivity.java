package dschik.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DialogRegisterActivity.DialogRegisterListener {

    private static final int RC_SIGN_IN = 9001;
    Button signin;
    EditText username, password;
    TextView register;
    String user_name;
    String pass_word;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences sh;
    SharedPreferences.Editor shedit;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference dbref;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedinstances) {
        super.onCreate(savedinstances);
        setContentView(R.layout.sign_up);
        signin = (Button) findViewById(R.id.sign_in_button);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();
        mAuth = FirebaseAuth.getInstance();

        progressDialog =new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Signing In...");
        progressDialog.setIndeterminate(true);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //default_web_client_id
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogRegisterActivity dialogRegisterActivity = new DialogRegisterActivity(progressDialog);
                dialogRegisterActivity.show(getSupportFragmentManager(), "register info");
            }
        });

        findViewById(R.id.reg_google).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                 startActivityForResult(intent, RC_SIGN_IN);
             }
         }
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    private void createAccount(final String username, String password) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("aa", "createUserWithEmail:success");

                            shedit.putString("dis_email",username);
                            shedit.putString("dis_name", username.substring(0, username.indexOf('@')));
                            shedit.apply();

                            createUserAccountInDB();
                            progressDialog.cancel();

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            Toast.makeText(SignUpActivity.this, "Authentication success. Now Login Using these Credentials",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("aa", "createUserWithEmail:failure" + task.getException().getMessage());
                            progressDialog.cancel();
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int req, int resultCode, Intent data) {
        super.onActivityResult(req, resultCode, data);
        Log.d("aa", "test");
        if (req == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("aa", "hell");
            if (result.isSuccess()) {
                Log.d("aa", "hello");
                GoogleSignInAccount account = result.getSignInAccount();
                String name = "";
                try {
                    progressDialog.show();
                    firebaseAuthWithGoogle(account);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                    Log.d("aa", "packman");
                }
                //Toast.makeText(this, name, Toast.LENGTH_LONG).show();
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
                            FirebaseUser user = mAuth.getCurrentUser();

                            String name = "";
                            try {
                                name = user.getDisplayName();
                                shedit.putString("dis_email",user.getEmail());
                                shedit.putString("dis_name", name);
                                shedit.apply();


                                user_name = user.getEmail();
                                pass_word = user.getUid();
                                Log.d("aa", name + "==" + user_name + "==" + pass_word);

                                createUserAccountInDB();

                                progressDialog.cancel();
                                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);


                            } catch (NullPointerException n) {
                                n.printStackTrace();
                            }
                            Toast.makeText(SignUpActivity.this, name, Toast.LENGTH_LONG).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("aa", "signInWithCredential:failure" + task.getException());
                            progressDialog.cancel();
                            Toast.makeText(SignUpActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
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

                Toast.makeText(SignUpActivity.this, "Authentication Passed", Toast.LENGTH_SHORT).show();

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

        return email.replace(".", "");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void applyData(String username, String password) {
        createAccount(username, password);
    }
}
