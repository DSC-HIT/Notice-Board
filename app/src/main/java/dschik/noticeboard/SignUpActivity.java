package dschik.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity {

    Button signin;
    EditText username,password;
    TextView register;

    @Override
    public void onCreate(Bundle savedinstances) {
        super.onCreate(savedinstances);
        setContentView(R.layout.sign_up);
        signin=(Button)findViewById(R.id.signin_button);
        username=(EditText)findViewById(R.id.signin_username);
        password=(EditText)findViewById(R.id.signin_password);
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
                    if(user_name.equals("heritage")&pass_word.equals("123")){
                        Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Sign In successful",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Sign In failed",Toast.LENGTH_SHORT).show();
                    }
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
}
