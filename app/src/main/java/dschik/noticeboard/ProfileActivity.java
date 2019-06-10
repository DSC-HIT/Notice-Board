package dschik.noticeboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView username;
    private TextView useremail;
    private TextView usernumber;
    private SharedPreferences sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);

        username = findViewById(R.id.user_name);
        useremail = findViewById(R.id.email_address);
        usernumber = findViewById(R.id.phone_number);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String name = sh.getString("dis_name", "name");
        String email = sh.getString("dis_email", "email");
        //String number = user.getPhoneNumber();

        assert name != null;
        username.setText(name.toUpperCase());
        useremail.setText(email);
        //usernumber.setText(number);

    }


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

