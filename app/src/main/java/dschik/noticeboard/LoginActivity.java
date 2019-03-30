package dschik.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {

    Button register;
    EditText username,password;
    String userrole="admin";
    TextView signin;
    private String[] userRoleString=new String[]{"admin","student"};
    SharedPreferences sh;
    SharedPreferences.Editor shedit;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sh = getSharedPreferences("shared",Context.MODE_PRIVATE);
        shedit = sh.edit();
        register=(Button)findViewById(R.id.register_button);
        username=(EditText)findViewById(R.id.login_username);
        password=(EditText)findViewById(R.id.login_password);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userrole.equals("admin")){
                    String user_name=username.getText().toString();
                    String pass_word=password.getText().toString();
                    if(TextUtils.isEmpty(pass_word)) {
                        password.setError("Invalid password");
                    }
                    else{
                        if(user_name.trim().equals("heritage")&pass_word.equals("123")){
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Login successful",Toast.LENGTH_SHORT).show();
                            shedit.putString(USER_NAME,user_name);
                            shedit.apply();
                            shedit.putString(PASS_WORD,pass_word);
                            shedit.apply();

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        signin=(TextView)findViewById(R.id.sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
