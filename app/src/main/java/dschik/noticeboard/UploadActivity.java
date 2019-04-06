package dschik.noticeboard;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


import com.google.android.gms.auth.api.Auth;;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URL;


public class UploadActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener
        ,GoogleApiClient.OnConnectionFailedListener
        ,NavigationView.OnNavigationItemSelectedListener
        ,View.OnClickListener  {

    private static final int PICK_IMAGE_REQUEST = 123;
    private StorageReference mStorageRef;
    FirebaseDatabase db;
    DatabaseReference dbref;

    private final String type_ext[]={".jpeg",".png",".pdf"};
    private String type_name;
    private String motto;
    private int type_pos;

    private Uri filePath;

    SharedPreferences sh;


    private Button choose;
    private Button upload;
    ImageView imagePreview;
    EditText file;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        //initialization of views and setting ClickListeners
        TextView usr,motto_view;
        file = findViewById(R.id.file_name);
        imagePreview = findViewById(R.id.preview_image);
        choose = findViewById(R.id.choose_button);
        upload = findViewById(R.id.upload_button);

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);

        sh = getSharedPreferences("shared",Context.MODE_PRIVATE);


        //getting intent and setting the motto
        Intent i = this.getIntent();

        boolean flag = i.getBooleanExtra("flag", false);
        if(flag)
        {
            motto = "Announcement";
        }else {
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

        usr.setText(sh.getString("dis_name","user"));



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
    public void onStart()
    {
        super.onStart();
        if(mAuth.getCurrentUser() ==  null)
        {
            Intent intent= new Intent(UploadActivity.this, LoginActivity.class);
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


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_notice) {
            Intent intent=new Intent(UploadActivity.this,NoticeViewer.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcement) {
            Intent intent=new Intent(UploadActivity.this,AnnouncementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notes) {
            Intent intent=new Intent(UploadActivity.this,NotesDownload.class);
            startActivity(intent);

        } else if (id == R.id.nav_share_announcements) {
            Intent intent=new Intent(UploadActivity.this,UploadActivity.class);
            intent.putExtra("flag",true);
            startActivity(intent);
            Toast.makeText(this,"you are already in upload page",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share_notes) {
            Intent intent=new Intent(UploadActivity.this,UploadActivity.class);
            intent.putExtra("flag",false);
            startActivity(intent);
            Toast.makeText(this,"you are already in upload page",Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.logout){

            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent i = new Intent(this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        Toast.makeText(this,"Log In Please",Toast.LENGTH_SHORT).show();

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

        if(v == choose)
        {
            openFileChooser();
        }
        else if(v == upload)
        {
            if(file.getText().toString().equals(""))
                file.setError("Invalid Name!!!");
            else
                uploadFile();
        }

    }

    private void openFileChooser()
    {
        Intent i = new Intent();
        if(type_pos<=1)
        {
            i.setType("image/"+type_name.substring(1));//getting "jpeg" from ".jpeg"
        }else{
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

                Toast.makeText(UploadActivity.this,"File ready to Upload!",Toast.LENGTH_SHORT).show();


                if(type_pos<=1)
                {
                    imagePreview.setImageBitmap(bitmap);
                }else {
                    imagePreview.setImageResource(R.drawable.pdf_logo);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(UploadActivity.this,"Error acquiring Uri!!!",Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final String file_name = file.getText().toString();

            StorageReference rRef = mStorageRef.child(motto+"/"+file_name+type_name);


            rRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            String url = taskSnapshot.getDownloadUrl().toString();
                            dbref.child(file_name).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(UploadActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(UploadActivity.this,"Upload NOT successful",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

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
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            Toast.makeText(UploadActivity.this,"Error in uploading!!!",Toast.LENGTH_LONG).show();
        }
    }


}