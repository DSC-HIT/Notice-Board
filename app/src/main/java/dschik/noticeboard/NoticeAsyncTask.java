package dschik.noticeboard;

import android.content.Context;
import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NoticeAsyncTask extends AsyncTask<URL, String, String>{
    ArrayList<DataObject> dt;
    Context c;
    RecyclerView mrecyler;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    ShimmerFrameLayout sh;
    SwipeRefreshLayout swip;
    NoticeAsyncTask(Context context, RecyclerView recyclerView, ShimmerFrameLayout sh1,SwipeRefreshLayout ss)
    {
        mrecyler = recyclerView;
        c = context;
        sh = sh1;
        swip =ss;
    }

    @Override
    protected String doInBackground(URL... s) {

        String json = "";
        try{
            HttpURLConnection connection = (HttpURLConnection) s[0].openConnection();
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            json = br.readLine();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return json;

    }

    @Override
    protected void onPostExecute(String s)
    {
        String q1 = "";
        String q2 = "";
        String q3 = "";
        JSONObject jobj= null;
        if(c instanceof MainActivity)
        {
            q1 = "desc";
            q2 = "link";
            q3 = "";
        } else if(c instanceof NoticeViewer) {
            q1 = "title";
            q2 = "link";
            q3 = "http://heritageit.edu/";
        }
        sh.stopShimmer();
        swip.setRefreshing(false);
        sh.setVisibility(View.GONE);
        Log.d("aa","called");
        try {
            int i =0;
            dt = new ArrayList<>();
            //jobj = new JSONObject(s);
            JSONArray jarray = new JSONArray(s);
            int size = jarray.length();
            //Log.d("aa","test"+size);
            for (i=0; i< size; i++) {
                JSONObject j = jarray.getJSONObject(i);
                String head = j.getString(q1);
                String link = q3+j.getString(q2);
                if(link.contains(".pdf") && c instanceof MainActivity)
                {
                    link = "http://heritageit.edu/"+link;
                }
                DataObject obj = new DataObject(head, link, "Heritage Institute of Technology","01/01/19",null);
                dt.add(i, obj);
                //Log.d("aa","*--");
                myRecyclerViewAdapter= new MyRecyclerViewAdapter(dt);
                mrecyler.setAdapter(myRecyclerViewAdapter);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
