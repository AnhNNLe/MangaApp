package jsl2449.TheNewGateReader;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by Joe on 11/8/2016.
 */

public class MangaChapter implements Serializable {
    public String title;
    public String chapterURL;

    public String toString(){
        return title + " " + chapterURL;
    }

}
