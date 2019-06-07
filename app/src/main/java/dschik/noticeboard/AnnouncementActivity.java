package dschik.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class AnnouncementActivity<search> extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , GoogleApiClient.OnConnectionFailedListener {

    private static String LOG_TAG = "CardViewActivity";
    GoogleApiClient mGoogleApiClient;
    FirebaseDatabase db;
    DatabaseReference dbref;

    ArrayList results = new ArrayList<DataObject>();

    SharedPreferences sh;
    ShimmerFrameLayout shimmerFrameLayout;
    SwipeRefreshLayout swiper;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppBarLayout appBar;
    private Toolbar toolbar;
    private EditText searchEditText;
    private View searchAppBarLayout;
    private TabLayout tabLayout;
    private Toolbar searchToolBar;
    private RatingBar ratingBar;
    private ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        sh = getSharedPreferences("shared", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        db = FirebaseDatabase.getInstance();
        dbref = db.getReference();

        getContent();

        shimmerFrameLayout = findViewById(R.id.shimmer);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerViewAdapter(getDataSet(), AnnouncementActivity.this);

        mRecyclerView.setAdapter(mAdapter);

        ratingBar = findViewById(R.id.myRatingBar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AnnouncementActivity.this, UploadActivity.class);
                i.putExtra("flag", true);
                startActivity(i);
            }

        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        TextView usr = header.findViewById(R.id.userText);

        FirebaseUser user = mAuth.getCurrentUser();

        usr.setText(sh.getString("dis_name", "user"));


        swiper = findViewById(R.id.swiper);
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
                        refreshContent();
                    }
                }, 1000);
            }
        });


    }

    private void searchContent(String newText) {

        Query myquery = dbref.orderByKey().startAt(newText);

        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("aa", "added");
                shimmerFrameLayout.setVisibility(ShimmerFrameLayout.GONE);
                shimmerFrameLayout.stopShimmer();
                String file = dataSnapshot.getKey();
                Record data = dataSnapshot.getValue(Record.class);
                int index = 0;
                String url = data.getUrl();
                String sendername = data.getSender().equals("") ? "sender" : data.getSender();
                String datesent = data.getDate().equals("") ? "date" : data.getDate();
                Bitmap bmp = data.getBmp();
                String description = data.getDescription();
                String type = data.getType();
                Log.d("aa", file + "++" + url);
                if (type.equalsIgnoreCase("Announcement")) {
                    DataObject obj = new DataObject(file, url, sendername, datesent, bmp, description);
                    results.add(index, obj);
                    index++;
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void refreshContent() {
        shimmerFrameLayout.setVisibility(ShimmerFrameLayout.VISIBLE);
        shimmerFrameLayout.startShimmer();
        results = new ArrayList<DataObject>();
        mAdapter = new MyRecyclerViewAdapter(getDataSet(), AnnouncementActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        getContent();
    }

    private void getContent() {
        dbref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                swiper.setRefreshing(false);

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                String file = dataSnapshot.getKey();
                Record data = dataSnapshot.getValue(Record.class);
                int index = 0;
                String url = data.getUrl();
                String sendername = data.getSender().equals("") ? "sender" : data.getSender();
                String datesent = data.getDate().equals("") ? "date" : data.getDate();
                Bitmap bmp = data.getBmp();
                String description = data.getDescription();
                String type = data.getType();
                Log.d("aa", file + "++" + url);
                if (type.equalsIgnoreCase("Announcement")) {
                    DataObject obj = new DataObject(file, url, sendername, datesent, bmp, description);
                    results.add(index, obj);
                    index++;
                    mRecyclerView.setAdapter(mAdapter);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }


    private ArrayList<DataObject> getDataSet() {

        return results;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_item, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(ShimmerFrameLayout.VISIBLE);//starting the animation will be stoping when child arrives.

                results = new ArrayList<DataObject>();
                mAdapter = new MyRecyclerViewAdapter(getDataSet()/*returns value of result<ArrayList>*/, AnnouncementActivity.this);
                mRecyclerView.setAdapter(mAdapter);


                if (!query.equals(""))//if query string is empty DON'T query
                    searchContent(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });
        getMenuInflater().inflate(R.menu.announcement, menu);
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
            Intent i = new Intent(AnnouncementActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.profile) {
            Intent i = new Intent(AnnouncementActivity.this, ProfileActivity.class);
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
            Intent intent = new Intent(AnnouncementActivity.this, NoticeViewer.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcement) {
            Intent intent = new Intent(AnnouncementActivity.this, AnnouncementActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_notes) {
            Intent intent = new Intent(AnnouncementActivity.this, NotesDownload.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            signOut();

        } else if (id == R.id.nav_share_announcements) {
            Intent intent = new Intent(AnnouncementActivity.this, UploadActivity.class);
            intent.putExtra("flag", true);
            startActivity(intent);
        } else if (id == R.id.nav_share_notes) {
            Intent intent = new Intent(AnnouncementActivity.this, UploadActivity.class);
            intent.putExtra("flag", false);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut() {
        mAuth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        Toast.makeText(this, "Log In Please", Toast.LENGTH_SHORT).show();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(AnnouncementActivity.this, "Signed Out", Toast.LENGTH_LONG).show();
            }
        });
    }


}
