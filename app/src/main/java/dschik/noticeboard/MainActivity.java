package dschik.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener
        ,NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sh;


    GoogleApiClient mGoogleApiClient;
    private String USER_NAME = "username";
    private String PASS_WORD = "password";
    private String DEFAULT = "null";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sh = getSharedPreferences("shared",Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        final SwipeRefreshLayout swiper = findViewById(R.id.swiper);
        final NoticeAsyncTask noticeAsyncTask = new NoticeAsyncTask(MainActivity.this,mRecyclerView,shimmerFrameLayout,swiper);
        URL[] url = new URL[1];
        try {
            url[0]= new URL("https://scraping-noticeboard.herokuapp.com/links");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        noticeAsyncTask.execute(url);

        swiper.setColorSchemeColors(getResources().getColor(R.color.colorPrimary)
                , getResources().getColor(R.color.primarylight)
                , getResources().getColor(R.color.colorAccent)
                , getResources().getColor(R.color.colorPrimaryDark));

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swiper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final NoticeAsyncTask noticeAsyncTask = new NoticeAsyncTask(MainActivity.this,mRecyclerView,shimmerFrameLayout,swiper);
                        URL[] url = new URL[1];
                        try {
                            //url[0]= new URL("https://raw.githubusercontent.com/DSCHeritage/Notice-Board/master/app/src/main/assets/heridata.json");
                            url[0]= new URL("https://scraping-noticeboard.herokuapp.com/links");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        noticeAsyncTask.execute(url);
                    }
                },2000);
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView usr = header.findViewById(R.id.userText);

        FirebaseUser user= mAuth.getCurrentUser();

        usr.setText(sh.getString("dis_name","user"));

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        else if(id==R.id.profile){
            Intent i=new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notice) {
           goToDrawerPage(getApplicationContext(),NoticeViewer.class);
        } else if (id == R.id.nav_announcement) {
            goToDrawerPage(getApplicationContext(),AnnouncementActivity.class);

        } else if (id == R.id.nav_notes) {
           goToDrawerPage(getApplicationContext(),NotesDownload.class);

        } else if (id == R.id.nav_share_announcements) {
            Intent intent=new Intent(MainActivity.this,UploadActivity.class);
            intent.putExtra("flag",true);
            startActivity(intent);
        } else if (id == R.id.nav_share_notes) {
            Intent intent=new Intent(MainActivity.this,UploadActivity.class);
            intent.putExtra("flag",false);
            startActivity(intent);
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

        Toast.makeText(this,"Log In Please",Toast.LENGTH_SHORT).show();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        /*if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Intent intent= new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }*/

    }

    @Override
    public void onStart()
    {
        super.onStart();
        /*if(mAuth.getCurrentUser() ==  null)
        {
            Intent intent= new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }*/
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void goToDrawerPage(Context present, Class toPage)
    {
        Intent intent = new Intent(present, toPage);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}

