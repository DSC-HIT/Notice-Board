package dschik.noticeboard;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        TextView email = (TextView) findViewById(R.id.email_address);
        email.setText(user.getEmail());

        TextView name = (TextView) findViewById(R.id.tv_name);
        name.setText(user.getDisplayName());


        /*String number = getMyPhoneNO();
        Toast.makeText(getApplicationContext(), "My Phone Number is: "
                + number, Toast.LENGTH_SHORT).show();

        TextView textView = (TextView) findViewById(R.id.phone_number);
        Log.d("aa","number"+number);
        textView.setText("My Phone number is: " + number);*/

    }
    /*private String getMyPhoneNO() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }*/
}
