package jsl2449.TheNewGateReader;

/**
 * Created by Joe on 11/7/2016.
 */

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URL;

public class Bookmark implements Serializable {
    public int pageNumber;
    public int chapterNumber;
    public int totalPages;
    public String mangaTitle;
    public URL imageURL;
    public URL pageURL;
    public URL chapterURL;
}

