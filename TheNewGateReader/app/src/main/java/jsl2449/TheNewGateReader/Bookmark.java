package jsl2449.TheNewGateReader;

/**
 * Created by Joe on 11/7/2016.
 */

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URL;

public class Bookmark implements Serializable {
    public int id;
    public int currentPage;
    public int totalPages;
    public int currentChapter;
    public String pageURL;

    public String toString() {
        return "id = " + id + " currentPage = " + currentPage + " totalPages = " + totalPages + " currentChapter = " + currentChapter + " pageURL = " + pageURL;
    }
}

