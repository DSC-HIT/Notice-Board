package dschik.noticeboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;




public class UploadActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener
        , GoogleApiClient.OnConnectionFailedListener
        , NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 123;
    private final String type_ext[] = {".jpeg", ".png", ".pdf"};
    FirebaseDatabase db;
    DatabaseReference dbref;
    SharedPreferences sh;
    ImageView imagePreview;
    EditText file;
    EditText description;
    GoogleApiClient mGoogleApiClient;
    private StorageReference mStorageRef;
    private String type_name;
    private String motto;
    private int type_pos;
    private Uri filePath;
    private Button choose;
    private Button upload;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        //initialization of views and setting ClickListeners
        TextView usr, motto_view;
        file = findViewById(R.id.file_name);
        description = findViewById(R.id.postDescription);
        imagePreview = findViewById(R.id.preview_image);
        choose = findViewById(R.id.choose_button);
        upload = findViewById(R.id.upload_button);

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);

        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);


        //getting intent and setting the motto
        Intent i = this.getIntent();

        boolean flag = i.getBooleanExtra("flag", false);
        if (flag) {
            motto = "Announcement";
        } else {
            motto = "Notes";
        }

        motto_view = findViewById(R.id.uploadtext);
        motto_view.setText(motto);


        //getting firebase Auth

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //getting toolbar and setting up drawer layout

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //getting the view from drawer header and seting the user name.
        View header = navigationView.getHeaderView(0);
        usr = header.findViewById(R.id.userText);

        usr.setText(sh.getString("dis_name", "user"));


        // setting up the spinner.
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);

        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.file_type, R.layout.spinner_custom);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        //firebase storage

        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();


    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(UploadActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_about) {
            Intent i = new Intent(UploadActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.profile) {
            Intent i = new Intent(UploadActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_notice) {
            Intent intent = new Intent(UploadActivity.this, NoticeViewer.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcement) {
            Intent intent = new Intent(UploadActivity.this, AnnouncementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notes) {
            Intent intent = new Intent(UploadActivity.this, NotesDownload.class);
            startActivity(intent);

        } else if (id == R.id.nav_share_announcements) {
            Intent intent = new Intent(UploadActivity.this, UploadActivity.class);
            intent.putExtra("flag", true);
            startActivity(intent);
            Toast.makeText(this, "you are already in upload page", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share_notes) {
            Intent intent = new Intent(UploadActivity.this, UploadActivity.class);
            intent.putExtra("flag", false);
            startActivity(intent);
            Toast.makeText(this, "you are already in upload page", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.logout) {

            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        Toast.makeText(this, "Log In Please", Toast.LENGTH_SHORT).show();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(UploadActivity.this, "Signed Out", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        type_name = type_ext[position];
        type_pos = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

        if (v == choose) {
            openFileChooser();
        } else if (v == upload) {
            if (file.getText().toString().equals(""))
                file.setError("Invalid Name!!!");
            else
                uploadFile();
        }

    }

    private void openFileChooser() {
        Intent i = new Intent();
        if (type_pos <= 1) {
            i.setType("image/" + type_name.substring(1));//getting "jpeg" from ".jpeg"
        } else {
            i.setType("application/pdf");
        }
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                Toast.makeText(UploadActivity.this, "File ready to Upload!", Toast.LENGTH_SHORT).show();


                if (type_pos <= 1) {
                    imagePreview.setImageBitmap(bitmap);
                } else {
                    imagePreview.setImageResource(R.drawable.pdf_logo1);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(UploadActivity.this, "Error acquiring Uri!!!", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final String file_name = file.getText().toString();

            final StorageReference rRef = mStorageRef.child(motto + "/" + file_name + type_name);

            final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
            final Date date = new Date();
            FirebaseUser fuser = mAuth.getCurrentUser();
            assert fuser != null;
            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setContentType(type_name)
                    .setCustomMetadata("user", sh.getString("dis_name","name"))
                    .setCustomMetadata("date", dateFormat.format(date)).build();

            /*Context context = UploadActivity.this;
            Uri uri;
            uri = filePath;
            FirebaseVisionImage image = null;
            try {
                image = FirebaseVisionImage.fromFilePath(context, uri);
                Log.d("aa", "try");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("aa", "catch");
            }
            FirebaseVisionCloudImageLabelerOptions options = new FirebaseVisionCloudImageLabelerOptions.Builder()
                    .setConfidenceThreshold(0.7f).build();
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getCloudImageLabeler(options);

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {

                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            for (FirebaseVisionImageLabel label : labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                Log.d("aa", text + " " + entityId + " " + confidence);
                            }
                            Log.d("aa", "for");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("aa", "abcd");
                        }
                    });*/
            UploadTask uploadTask = rRef.putFile(filePath, storageMetadata);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //if the upload is successfull
                    //hiding the progress dialog

                    progressDialog.dismiss();

                    //and displaying a success toast
                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();


                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();//getting the download URL
                    task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri uri = task.getResult();
                            URL url = null;
                            try {
                                assert uri != null;
                                url = new URL(uri.toString());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                            assert url != null;
                            String rl = url.toString();//got firebase URL

                            final FirebaseUser fuser1 = mAuth.getCurrentUser();//getting details for Record database class object
                            Date date = new Date();
                            Bitmap bitmp = null;
                            InputStream is = null;                                  /* creating bitmap from file path uri
                             */
                            try {
                                is = getContentResolver().openInputStream(filePath);
                                bitmp = BitmapFactory.decodeStream(is);
                                assert is != null;
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Record r = null;//initializing the Record class
                            String desc = description.getText().toString();//getting desccription of upload
                            assert fuser1 != null;
                            r = new Record(rl, sh.getString("dis_name","name"), dateFormat.format(date), desc, motto, null);//setting values
                            //Log.d("aa",filePath);
                            dbref.child(file_name).setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        /*
                                        gets the bitmap of the local file using its local uri
                                        */


                                        Toast.makeText(UploadActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                        //Snackbar.make(get, "Upload successful",Snackbar.LENGTH_SHORT).show();
                                        //imagePreview.setImageBitmap(bitmp);
                                    } else {
                                        Toast.makeText(UploadActivity.this, "Upload NOT successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    //String url = "test";

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            progressDialog.setProgress((int)progress);
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(UploadActivity.this, "Error in uploading!!!", Toast.LENGTH_LONG).show();
        }
    }


}