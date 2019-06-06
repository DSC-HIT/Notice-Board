package dschik.noticeboard;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;
    //static String[] url;
    static  int range = 0;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView label;
        TextView dateTime;
        TextView sendername;
        TextView urldata;
        TextView desc;
        ImageView preview;
        MaterialButton b;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            final Context context = itemView.getContext();
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.dateTime);
            sendername = (TextView) itemView.findViewById(R.id.sendername);
            urldata = (TextView) itemView.findViewById(R.id.urldata);
            preview = (ImageView) itemView.findViewById(R.id.imageView);
            desc = (TextView) itemView.findViewById(R.id.postDescription);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);



            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("aa",pos+"");
                    String ull = urldata.getText().toString();
                    //String ull = url[pos];
                    Uri uri = Uri.parse(ull);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,uri);
                    context.startActivity(browserIntent);
                }
            });


            b=itemView.findViewById(R.id.carder_button2);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link=urldata.getText().toString();
                    String title = label.getText().toString();
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Your Subject");
                    intent.putExtra(Intent.EXTRA_TEXT,title+"\n"+link);
                    itemView.getContext().startActivity(Intent.createChooser(intent,"Share text via"));
                }
            });

            itemView.findViewById(R.id.carder_button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = urldata.getText().toString();
                    DownloadManager d = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(link);
                    DownloadManager.Request request= new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                    Long referrence = d.enqueue(request);
                    Log.d("aa","down clicked");
                }
            });
        }

        @Override
        public void onClick(View v) {
            try {
                Log.d("aa","clicked");
                label.setMaxLines(3);
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
        //url = new String[myDataset.size()];
        //Log.d("aa",myDataset.size()+"kk");

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        String sendername = "Sender: "+mDataset.get(position).getmText3();
        String datetime = "Date: "+mDataset.get(position).getmText4();
        String url = mDataset.get(position).getmText2();
        holder.label.setText(mDataset.get(position).getmText1());
        holder.sendername.setText(sendername);
        holder.dateTime.setText(datetime);
        holder.urldata.setText(url);
        holder.desc.setText(mDataset.get(position).getDescription());
        if(mDataset.get(position).getBmp() != null)//if it is a image bitmap will have data and that is set in preview
            holder.preview.setImageBitmap(mDataset.get(position).getBmp());
        else{                                       //else the preview is set according to file type
            if(url.contains(".pdf"))
            {
                holder.preview.setImageResource(R.drawable.pdf_logo1);
            }
            else if(url.contains(".jpeg") || url.contains(".png")){
                holder.preview.setImageResource(R.drawable.image_logo);
            }
            else {
                holder.preview.setImageResource(R.drawable.link);
            }
        }
        //url[position] = mDataset.get(position).getmText2();

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

}
