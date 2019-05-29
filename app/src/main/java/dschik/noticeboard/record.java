package dschik.noticeboard;

import android.graphics.Bitmap;

public class record {
    private String url;
    private String sender;
    private String date;
    private Bitmap bmp;

    public record()
    {

    }

    public record(String ul,String send, String d,Bitmap bm)
    {
        url = ul;
        sender = send;
        date = d;
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

    public Bitmap getBmp()
    {
        return bmp;
    }
}
