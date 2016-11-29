package jsl2449.TheNewGateReader;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Joe on 11/27/2016.
 */

public class ParsePage implements URLFetch.Callback {

    private String imageKey;
    private String pageHTML;
    private URL pageURL;

    public ParsePage(String strPage) {
        try {
            this.pageURL = new URL(strPage);
            pageHTML = null;
            imageKey = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void fetch(URL url) {
        if (BitmapCache.getInstance().getBitmap(url.toString()) == null || pageHTML == null) {
            new URLFetch(this, url);
        } else {
            fetchComplete(url.toString());
        }
    }

    private void parse() {
        String imageSection;
        imageSection = pageHTML.substring(pageHTML.indexOf("read_img"));
        imageSection = imageSection.substring(imageSection.indexOf("http://h.mhcdn.net"));
        int indexLastQuote = imageSection.indexOf("\"");

        try {
            imageKey = imageSection.substring(0, indexLastQuote);
//            System.out.println("image url " + strImageURL);
            URL imageURL = new URL(imageKey);
            fetch(imageURL);
        } catch (MalformedURLException e) {
            System.out.println("malformed URL excaption ParsePage.java");
        }
    }

    @Override
    public void fetchStart() {
        System.out.println("Caching image from " + pageURL.toString());
    }

    @Override
    public void fetchComplete(String result) {
        if (result.contains("<!DOCTYPE")) {
            pageHTML = result;
            parse();
        } else {
            imageKey = result;
        }
    }
}
