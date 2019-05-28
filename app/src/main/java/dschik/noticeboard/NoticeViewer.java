package dschik.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class NoticeViewer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;

    SharedPreferences sh;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    SwipeRefreshLayout swiper;

    JSONObject jobj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_viewer);

        swiper = findViewById(R.id.swiper);

        sh = getSharedPreferences("shared",Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view1);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        final NoticeAsyncTask noticeAsyncTask = new NoticeAsyncTask(NoticeViewer.this,mRecyclerView,shimmerFrameLayout,swiper);
        URL url[] = new URL[1];
        try {
            //url[0]= new URL("https://raw.githubusercontent.com/DSCHeritage/Notice-Board/master/app/src/main/assets/heridata.json");
            url[0]= new URL("https://scraping-noticeboard.herokuapp.com/notices");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        noticeAsyncTask.execute(url);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView usr = header.findViewById(R.id.userText);

        usr.setText(sh.getString("dis_name","user"));


        swiper.setColorSchemeColors(getResources().getColor(R.color.colorPrimary)
                , getResources().getColor(R.color.primarylight)
                , getResources().getColor(R.color.colorPrimaryDark)
                , getResources().getColor(R.color.colorAccent));



        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swiper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final NoticeAsyncTask noticeAsyncTask = new NoticeAsyncTask(NoticeViewer.this,mRecyclerView,shimmerFrameLayout,swiper);
                        URL url[] = new URL[1];
                        try {
                            //url[0]= new URL("https://raw.githubusercontent.com/DSCHeritage/Notice-Board/master/app/src/main/assets/heridata.json");
                            url[0]= new URL("https://scraping-noticeboard.herokuapp.com/notices");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        noticeAsyncTask.execute(url);
                    }
                },2000);
            }
        });


    }




    @Override
    protected void onResume() {
        super.onResume();

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
        getMenuInflater().inflate(R.menu.notice_viewer, menu);
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
            Intent i = new Intent(NoticeViewer.this, AboutActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notice) {
            Intent intent=new Intent(NoticeViewer.this,NoticeViewer.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcement) {
            Intent intent=new Intent(NoticeViewer.this,AnnouncementActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notes) {
            Intent intent=new Intent(NoticeViewer.this,NotesDownload.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            signOut();

        } else if (id == R.id.nav_share_announcements) {
            Intent intent=new Intent(NoticeViewer.this,UploadActivity.class);
            intent.putExtra("flag",true);
            startActivity(intent);
        } else if (id == R.id.nav_share_notes) {
            Intent intent=new Intent(NoticeViewer.this,UploadActivity.class);
            intent.putExtra("flag",false);
            startActivity(intent);
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
                Toast.makeText(NoticeViewer.this, "Signed Out", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
