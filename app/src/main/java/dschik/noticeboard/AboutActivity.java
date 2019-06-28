package dschik.noticeboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setView(R.drawable.daw,R.id.daw_pic, "Suranjan Daw","Android Full Stack Dev","https://www.linkedin.com/in/suranjan-daw-07901b15b/");
        setView(R.drawable.adhikary,R.id.adhi_pic,"Souhardya Adhikary","Android Dev","https://www.linkedin.com/in/souhardya-adhikary-a10a10156/");
        setView(R.drawable.das,R.id.das_pic,"Subhasis Das", "Server Backend Dev" ,"https://www.linkedin.com/in/subhasis-das-6a00aa172/");

    }

    private void setView(int draw, int id, String name, String des, final String url)
    {
        View v1 = findViewById(id);


        v1.findViewById(R.id.carder_button2).setVisibility(View.GONE);
        v1.findViewById(R.id.carder_button3).setVisibility(View.GONE);
        v1.findViewById(R.id.urldata).setVisibility(View.GONE);
        v1.findViewById(R.id.dateTime).setVisibility(View.GONE);
        v1.findViewById(R.id.sendername).setVisibility(View.GONE);

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(myIntent);
            }
        });



        TextView t = v1.findViewById(R.id.textView);
        t.setText(name);
        t.setPadding(0,10,0,10);
        TextView t2 = v1.findViewById(R.id.postDescription);
        t2.setText(des);
        t2.setPadding(0,10,0,10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            t2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        ImageView i = v1.findViewById(R.id.imageView);
        i.setImageResource(draw);


    }



}
