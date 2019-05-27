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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView label;
        TextView dateTime;
        Button view;
        Button b;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            final Context context = itemView.getContext();
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            view = itemView.findViewById(R.id.carder_button1);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(),"clicked",Toast.LENGTH_SHORT).show();
                }
            });

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.carder_button1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri uri = Uri.parse(dateTime.getText().toString());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,uri);
                    context.startActivity(browserIntent);

                }
            });


            b=itemView.findViewById(R.id.carder_button2);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text=dateTime.getText().toString();
                    String title = label.getText().toString();
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Your Subject");
                    intent.putExtra(Intent.EXTRA_TEXT,title+"\n"+text);
                    itemView.getContext().startActivity(Intent.createChooser(intent,"Share text via"));
                }
            });

            itemView.findViewById(R.id.carder_button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String link = dateTime.getText().toString();
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
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
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
        holder.label.setText(mDataset.get(position).getmText1());
        holder.dateTime.setText(mDataset.get(position).getmText2());
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
