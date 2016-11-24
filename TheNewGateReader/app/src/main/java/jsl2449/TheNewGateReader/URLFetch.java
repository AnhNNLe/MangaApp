package jsl2449.TheNewGateReader;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Joe on 11/8/2016.
 */

public class URLFetch implements RateLimit.RateLimitCallback {

    @Override
    public void rateLimitReady() {
        new AsyncDownloader().execute(url);
    }

    public interface Callback {
        void fetchStart();

        void fetchComplete(String result);
    }

    protected Callback callback = null;
    protected URL url;

    public URLFetch(Callback callback, URL url) {
        this.callback = callback;
        this.url = url;
        RateLimit.getInstance().add(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return url.equals(((URLFetch) obj).url);
    }

    public class AsyncDownloader extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... params) {
            callback.fetchStart();
            HttpURLConnection urlConn = null;
            BufferedReader in;
            String result = null;
            try {
                urlConn = (HttpURLConnection) params[0].openConnection();
                urlConn.setRequestProperty("User-Agent", "android:edu.utexas.cs371m.jsl2449.TheNewGateReader:v1.0 by Joe Le");
                urlConn.setRequestMethod("GET");
                if (params[0].toString().contains("www.mangahere.co")) {
                    in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    result = response.toString();
                } else { // get the picture and put into bitmap cache
//                    System.out.println("Download the picture section of the if statement JHGHKLJ:DHFLK");
                    String key = params[0].toString();
                    if (BitmapCache.getInstance().getBitmap(key) != null){
                        return key;
                    }
                    InputStream is = urlConn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap value = BitmapFactory.decodeStream(bis);
                    if (value != null) {
                        BitmapCache.getInstance().setBitmap(key, value);
                    } else {
                        BitmapCache.getInstance().setBitmap(key, BitmapCache.errorImageBitmap);
                    }
                    result = key;
                }
            } catch (Exception e) {
            } finally {
                urlConn.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            callback.fetchComplete(result);
        }
    }
}
