package dschik.noticeboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashScreen extends Activity {
    SharedPreferences sh;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";
    private String DEFAULT = "null";
    private final int splash_display_length=1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        sh = getSharedPreferences("shared",Context.MODE_PRIVATE);

        final String username = sh.getString(USER_NAME,DEFAULT);
        final String password = sh.getString(PASS_WORD,DEFAULT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(username.equals("heritage") && password.equals("123"))
                {
                    Intent mainIntent =new Intent(SplashScreen.this,MainActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }else {
                    Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    SplashScreen.this.startActivity(mainIntent);
                    SplashScreen.this.finish();
                }
            }
        },splash_display_length);
    }
}