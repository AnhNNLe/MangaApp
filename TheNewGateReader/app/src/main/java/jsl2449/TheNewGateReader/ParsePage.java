package jsl2449.TheNewGateReader;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 11/27/2016.
 */

public class ParsePage implements URLFetch.Callback {

    private Bookmark bm;
    private Context context;
    private Boolean getPageList;
    private Boolean cache;

    // used to fetch, not do anything else
    public ParsePage(Bookmark bm, Context context) {
        this.bm = bm;
        this.context = context;
        this.cache = true;
        this.getPageList = false;
        try {
            fetch(new URL(clean(this.bm.pageURL)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // used for main page to display
    public ParsePage(Bookmark bm, Context context, boolean getPageList) {
//        System.out.println("parsepage constructor");
        this.bm = bm;
        this.context = context;
        this.getPageList = getPageList;
        this.cache = false;
        try {
            fetch(new URL(clean(this.bm.pageURL)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String clean(String x) {
        if (x.contains("\'")) {
            x = x.replaceAll("\'", "");
        }
        return x;
    }

    private void fetch(URL url) {
//        System.out.println("fetch");
        // url = pageURL
        // or url = imageURL
        Bitmap b = BitmapCache.getInstance().getBitmap(url.toString());
        if (b == null) {
//            System.out.println("b == null " + url.toString());
            new URLFetch(this, url);
        } else {
//            System.out.println("b != null");
            fetchComplete(url.toString());
        }
    }

    private void parseForImage(String result) {
        String imageSection;
        imageSection = result.substring(result.indexOf("read_img"));
        imageSection = imageSection.substring(imageSection.indexOf("http://h.mhcdn.net"));
        int indexLastQuote = imageSection.indexOf("\"");

        try {
            String imageKey = imageSection.substring(0, indexLastQuote);
            URL imageURL = new URL(imageKey);
            fetch(imageURL);
        } catch (MalformedURLException e) {
            System.out.println("malformed URL excaption ParsePage.java");
        }
    }

    private void parseForPageList(String htmlPage) {
//        System.out.println("parsePageList");
        String pageListSection;
        pageListSection = htmlPage.substring(htmlPage.indexOf("<section class=\"readpage_top\">"));
        pageListSection = pageListSection.substring(0, pageListSection.indexOf("</section>"));
        pageListSection = pageListSection.substring(pageListSection.indexOf("<option"));
//        System.out.println(pageListSection);
        List<String> pageList = new ArrayList<String>();
        int pageCount = -1;
        do {
            pageCount++;
            int firstIndex = pageListSection.indexOf("http://");
            int secondIndex = pageListSection.indexOf("\"", firstIndex + 1);
            String newURL = pageListSection.substring(firstIndex, secondIndex);
//            System.out.println("newURL " + newURL);
            pageList.add(newURL);
            if (newURL.equals(bm.pageURL)) {
                bm.currentPage = pageCount;
            }
            int nextOption = pageListSection.indexOf("<option", 1);
            if (nextOption == -1) {
                break;
            }
            pageListSection = pageListSection.substring(nextOption);
        } while (true);

        bm.totalPages = pageList.size();
        ((PageViewerActivity) context).updatePageList(pageList);
        ((PageViewerActivity) context).updatePageNumber(bm.currentPage, bm.totalPages);
//        System.out.println(pageListSection);
    }

    @Override
    public void fetchStart() {
//        System.out.println("parsepage fetchStart");
    }

    @Override
    public void fetchComplete(String result) {
//        System.out.println("result  " + result);
        if (result.contains("<!DOCTYPE")) { // page html
            parseForImage(result);
            if (getPageList) {
                parseForPageList(result);
            }
        } else { // from image url
//            System.out.println("image url");
            if (cache) {
                ((PageViewerActivity) context).cache();
            } else {
//                System.out.println("update view");
                ((PageViewerActivity) context).updateView(result);
            }
        }
    }


}
