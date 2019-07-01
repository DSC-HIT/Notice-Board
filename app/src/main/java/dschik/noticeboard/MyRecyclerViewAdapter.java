package dschik.noticeboard;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {

    //static String[] url;
    static int range = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static MyClickListener myClickListener;
    Context context;
    private ArrayList<DataObject> mDataset;
    private int lastPosition = -1;

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
        //url = new String[myDataset.size()];
        //Log.d("aa",myDataset.size()+"kk");

    }

    void setOnItemClickListener(MyClickListener myClickListener) {
        MyRecyclerViewAdapter.myClickListener = myClickListener;
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        return new DataObjectHolder(view,context);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        setAnimation(holder.card, position);//setting animation
        String sendername = "Sender: " + mDataset.get(position).getmText3();
        String datetime = "";
        if(context instanceof NotesDownload || context instanceof AnnouncementActivity) {
            datetime = "Date: " + mDataset.get(position).getmText4();

        } else if( context instanceof MainActivity)
        {
            holder.md.setClickable(false);
            holder.md.setVisibility(View.GONE);
        }
        String url = mDataset.get(position).getmText2();
        holder.label.setText(mDataset.get(position).getmText1());
        holder.sendername.setText(sendername);
        holder.dateTime.setText(datetime);
        holder.urldata.setText(url);
        holder.desc.setText(mDataset.get(position).getDescription());
        if (mDataset.get(position).getBmp() != null)//if it is a image bitmap will have data and that is set in preview
            holder.preview.setImageBitmap(mDataset.get(position).getBmp());
        else {                                       //else the preview is set according to file type
            if (url.contains(".pdf")) {
                holder.preview.setImageResource(R.drawable.pdf_logo1);
            } else if (url.contains(".jpeg") || url.contains(".png")) {
                holder.preview.setImageResource(R.drawable.image_logo);
            } else {
                holder.preview.setImageResource(R.drawable.link);
            }
        }

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils
                    .loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView label;
        TextView dateTime;
        TextView sendername;
        TextView urldata;
        TextView desc;
        ImageView preview;
        MaterialButton b;
        MaterialButton md;
        View card;
        //RatingBar ratingBar;
        FirebaseAuth mAuth;
        FirebaseUser mUser;
        Context contextActivity;
        SharedPreferences sh;

        DataObjectHolder(final View itemView,Context contextActivity) {
            super(itemView);
            this.contextActivity = contextActivity;
            final Context context = itemView.getContext();
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            sendername = (TextView) itemView.findViewById(R.id.sendername);
            urldata = (TextView) itemView.findViewById(R.id.urldata);
            preview = (ImageView) itemView.findViewById(R.id.imageView);
            desc = (TextView) itemView.findViewById(R.id.postDescription);
            card = itemView.findViewById(R.id.card_view);
            sh = context.getSharedPreferences("shared",Context.MODE_PRIVATE);
            //ratingBar = (RatingBar) itemView.findViewById(R.id.myRatingBar);

            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


                }
            });*/

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);


            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("aa", pos + "");
                    String ull = urldata.getText().toString();
                    //String ull = url[pos];
                    Uri uri = Uri.parse(ull);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(browserIntent);
                }
            });


            b = itemView.findViewById(R.id.carder_button2);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link = urldata.getText().toString();
                    String title = label.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject");
                    intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + link);
                    itemView.getContext().startActivity(Intent.createChooser(intent, "Share text via"));
                }
            });
            md = itemView.findViewById(R.id.carder_button3);
            md.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String link = urldata.getText().toString();
                    String title = label.getText().toString();
                    String ext = getExtension(link);
                    //downloadfile(link,title);
                    DownloadManager d = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(link);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(title);
                    if(ext.equals(".pdf"))
                        request.setMimeType("application/pdf");
                    else
                        request.setMimeType("image/"+ext.substring(1));
                    if(sh.getBoolean("storage_permission",false))
                    {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title+ext);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        assert d != null;
                        Long referrence = d.enqueue(request);
                    } else {
                        Toast.makeText(context,"Storage permission not provided. Cannot Download!!!",Toast.LENGTH_LONG).show();
                    }

                    Log.d("aa", "down clicked"+link);
                }
            });

        }
        private String getExtension(String url)
        {
            if(url.contains(".jpeg") || url.contains("jpg"))
            {
                return ".jpeg";
            } else if (url.contains("png"))
            {
                return ".png";
            } else
                return ".pdf";
        }

        @Override
        public void onClick(View v) {
            try {
                Log.d("aa", "clicked");
                label.setMaxLines(3);
                desc.setMaxLines(10);
                myClickListener.onItemClick(getAdapterPosition(), v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


