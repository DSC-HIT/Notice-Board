package dschik.noticeboard;

import android.graphics.Bitmap;

import androidx.annotation.Keep;

@Keep
public class Record {
    private String url;
    private String sender;
    private String date;
    private Bitmap bmp;
    private String description;
    private String type;

    public Record()
    {

    }

    public Record(String ul, String send, String d, String des, String t, Bitmap bm)
    {
        url = ul;
        sender = send;
        date = d;
        description = des;
        type = t;
        bmp = bm;
    }



    public String getUrl() {
        return url;
    }
    public  String getSender()
    {
        return sender;
    }
    public String getDate()
    {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Bitmap getBmp()
    {
        return bmp;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }
}
