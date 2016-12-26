package jsl2449.TheNewGateReader;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 12/1/2016.
 */

public class ParseChapter implements URLFetch.Callback {
    private Callback caller;


    public interface Callback {
        void updateChapterList(List<MangaChapter> list);
    }

    public ParseChapter(Callback caller) {
        this.caller = caller;
//        System.out.println("ParseChapter constructor " + caller);
        try {
            new URLFetch(this, new URL("http", "www.mangahere.co", "/manga/the_new_gate/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchStart() {
        System.out.println("ParseChapter fetchStart");
    }

    private void parseForChapterList(String result) {
        if (result == null) {
            try {
                new URLFetch(this, new URL("http", "www.mangahere.co", "/manga/the_new_gate/"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return;
        }

        List<MangaChapter> chapterList = new ArrayList<MangaChapter>();
        result = result.substring(result.indexOf("<div class=\"title\"><span></span><h3>Read The New Gate Online</h3></div>"));
        result = result.substring(result.indexOf("href"));
        result = result.substring(0, result.indexOf("</ul><ul class=\"tab_comment clearfix\">"));
//        System.out.println(result)
        String strURL = null;
        String strTitle = null;

        do {
            int indexHref = result.indexOf("href");
            int indexFirstQuote = result.indexOf("\"", indexHref + 1);
            int indexLastQuote = result.indexOf("\"", indexFirstQuote + 1);
            if (indexHref == -1 || indexFirstQuote == -1 || indexLastQuote == -1) {
                break;
            }
            strURL = result.substring(indexFirstQuote + 1, indexLastQuote);
            int indexFirstAngle = result.indexOf(">");
            int indexLastAngle = result.indexOf("</a>");
//            System.out.println(indexFirstAngle + " FGHJKL " + indexLastAngle);
            if (indexFirstAngle == -1 || indexLastAngle == -1) {
                break;
            }
            strTitle = result.substring(indexFirstAngle + 1, indexLastAngle);
            strTitle = strTitle.trim();
//            System.out.println(strTitle);

            MangaChapter newChapter = new MangaChapter();
            newChapter.chapterURL = strURL;
            newChapter.title = strTitle;

            chapterList.add(newChapter);

            if (result.indexOf("href", indexHref + 1) == -1) {
                break;
            }
            result = result.substring(result.indexOf("href", indexHref + 1));
        } while (strURL != null && strTitle != null);

        System.out.println(chapterList);
        caller.updateChapterList(chapterList);
//        if (caller.equals("PageViewerActivity")) {
//            System.out.println("pageViewerActivity");
//            caller.updateChapterList(chapterList);
//        } else if (caller.equals("ChapterListActivity")) {
//            ((ChapterListActivity) context).updateChapterList(chapterList);
//        }
    }

    @Override
    public void fetchComplete(String result) {
        parseForChapterList(result);
    }
}
