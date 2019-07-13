package dschik.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

public class ProfileActivity extends AppCompatActivity implements DialogProfileActivity.DialogListerner {
    private SharedPreferences sh;
    TextView detail;
    private SharedPreferences.Editor shedit;
    private DatabaseReference dbref;
    TextView dept_1;
    TextView year_1;
    String email, name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbref = db.getReference();


        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);
        shedit = sh.edit();

        TextView username = findViewById(R.id.user_name);
        TextView useremail = findViewById(R.id.email_address);
        dept_1 = findViewById(R.id.dept_1);
        year_1 = findViewById(R.id.year_1);
        ImageView settings = findViewById(R.id.settings);
        TextView terms=findViewById(R.id.terms_and_condition);
        TextView policy=findViewById(R.id.privacy_policy);
        TextView agreement=findViewById(R.id.eula);


        detail = findViewById(R.id.user_detail);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        name = sh.getString("dis_name", "name");
        email = sh.getString("dis_email", "email");
        String dept = sh.getString("dis_dept", "dept");
        String year = sh.getString("dis_year", "year");
        String details = dept + "-" + getYear(year);
        dept_1.setText(dept);
        year_1.setText(year);
        username.setText(name.toUpperCase());
        useremail.setText(email);
        //detail.setText(details);
        //usernumber.setText(number);
        //getData();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1nLbMQEdHOs7zEMTrHH5Pwat3RB1riAkq15gB1yT1Cuc/edit"));
                startActivity(intent);
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_VIEW,Uri.parse("https://docs.google.com/document/d/1p2h-ZmMXZopi24iDz9FlWNR_OEFRuWfz-sETteuVxic/edit"));
                startActivity(intent);
            }
        });
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://docs.google.com/document/d/1CbfOfpFXVAQZvd-iyqiyjJlMr60jclAXrs1kVOb0_zM/edit?usp=sharing"));
                startActivity(intent);
            }
        });
    }

    private void showdialog() {
        DialogProfileActivity dialog_profile_activity = new DialogProfileActivity();
        dialog_profile_activity.show(getSupportFragmentManager(), "info_dialog");
    }

    @Override
    public void applyData(String department, String year, String userType) {

        String s = department + "-" + getYear(year);
        dept_1.setText(department);
        year_1.setText(year);
        shedit.putString("dis_dept", department);
        shedit.putString("dis_year", year);
        shedit.apply();
        //detail.setText(s);
        updateProfile(department, year,userType);
    }

    public String getYear(String year) {
        String yr = "";
        switch (year) {
            case "1":
                yr = year + "st";
                break;
            case "2":
                yr = year + "nd";
                break;
            case "3":
                yr = year + "rd";
                break;
            default:
                yr = year + "th";
                break;
        }
        return yr;
    }

    private void updateProfile(String department, String year,String utype) {
        String name = sh.getString("dis_name", "name");
        String email = sh.getString("dis_email", "email");


        UserObj user1 = new UserObj(name, email, "", department, year);
        //insert user info in db here
        dbref.child("user").child(utype).child(getPath(email)).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(ProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @NonNull
    private String getPath(String email) {

        return email.replace(".", "");
    }

    private void showLog(String s)
    {
        Log.d("aa",s);
    }


}


