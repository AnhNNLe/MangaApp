package jsl2449.TheNewGateReader;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageViewerActivity extends AppCompatActivity implements URLFetch.Callback {


    private ImageView pageIV;
    private URL prevChapter;
    private URL nextChapter;
    private URL currentChapter;
    private URL currentPage;
    private URL prevPage;
    private URL nextPage;
    private URL imageURL;
    private List<URL> pageList;
    private List<String> chapterList;
    private int curPageIndex;
    private int curChapterIndex;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_viewer);
        getSupportActionBar().setTitle("The New Gate Reader");

        pageIV = (ImageView) findViewById(R.id.pageIV);

        pageIV.setOnTouchListener(new OnSwipeTouchListener(this));

        pageList = null;
        curPageIndex = -1;
        curChapterIndex = -1;
        update = true;


        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        if (callingBundle != null) {

            chapterList = callingBundle.getStringArrayList("chapterURLs");
            String pageURL = callingBundle.getString("pageURL");
            String strCurrentChapter = callingBundle.getString("currentChapter");
            try {
                currentPage = new URL(pageURL);
                currentChapter = new URL(strCurrentChapter);
                updateChapters();
                fetch(currentPage);
            } catch (MalformedURLException e) {
            }
//            Toast.makeText(this, "page is " + pageURL, Toast.LENGTH_SHORT).show();
        }

    }

    public void fetch(URL url) {
        if (BitmapCache.getInstance().getBitmap(url.toString()) == null) {
            new URLFetch(this, url);
        } else {
            fetchComplete(url.toString());
        }
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent();
        goBack.putExtra("currentPage", currentPage.toString());
        goBack.putExtra("currentChapter", currentChapter.toString());
        goBack.putExtra("chapterList", (ArrayList) chapterList);
//        System.out.println("back button pressed " + currentPage.toString());
        setResult(RESULT_OK, goBack);
        finish();
    }

    public void changePagePrev() {
//        System.out.println("need to change to previous page");
        if (curPageIndex == 0) {
//            System.out.println("need to change to previous chapter");
            if (curChapterIndex == 0) {
                Toast.makeText(this, "Reached the beginning of the Manga", Toast.LENGTH_SHORT).show();
            } else {
                nextChapter = currentChapter;
                currentChapter = prevChapter;
                curChapterIndex--;
                curPageIndex = -1;
                update = false;
                pageList = null;
                fetch(currentChapter);
            }
        } else {
            currentPage = prevPage;
            curPageIndex--;
            updatePages();
            fetch(currentPage);
        }
    }

    public void changePageNext() {
//        System.out.println("need to change to next page");
        if (curPageIndex == pageList.size() - 1) {
//            System.out.println("next chapter");
            // handle change chapter and everything
            if (curChapterIndex == chapterList.size() - 1) {
                Toast.makeText(this, "Reached the end of the Manga", Toast.LENGTH_SHORT).show();
            } else {
                // get next chapter
                prevChapter = currentChapter;
                currentChapter = nextChapter;
                currentPage = currentChapter;
                curChapterIndex++;
                pageList = null;
                curPageIndex = 0;
                updateChapters();
                fetch(currentPage);
            }
        } else {
            currentPage = nextPage;
            curPageIndex++;
            updatePages();
            fetch(currentPage);
        }
    }

    @Override
    public void fetchStart() {
        // should play loading animation
        System.out.println("page viewer loading animation");
    }

    @Override
    public void fetchComplete(String result) {
        if (result.contains("<!DOCTYPE")) {
            parsePageHtml(result);
        } else {
            pageIV.setImageBitmap(BitmapCache.getInstance().getBitmap(result));
        }
    }

    public void parsePageHtml(String htmlPage) {
//        System.out.println("parsePageHTML");
//        System.out.println(htmlPage);
        String imageSection;
        if (pageList == null) {
            parsePageList(htmlPage);
        }
        updatePages();
        imageSection = htmlPage.substring(htmlPage.indexOf("read_img"));
        imageSection = imageSection.substring(imageSection.indexOf("http://h.mhcdn.net"));
        int indexLastQuote = imageSection.indexOf("\"");
//        System.out.println("image section " + imageSection);
        try {
            String strImageURL = imageSection.substring(0, indexLastQuote);
//            System.out.println("image url " + strImageURL);
            imageURL = new URL(strImageURL);
            if (update) {
//                System.out.println("update");
                fetch(imageURL);
            } else {
                update = true;
//                System.out.println("no update");
                curPageIndex = pageList.size() - 1;
//                System.out.println("right after " + curPageIndex);
                fetch(pageList.get(pageList.size() - 1));
            }
        } catch (MalformedURLException e) {
        }
    }

    public void parsePageList(String htmlPage) {
        System.out.println("parsePageList");
        String pageListSection;
        pageListSection = htmlPage.substring(htmlPage.indexOf("<section class=\"readpage_top\">"));
        pageListSection = pageListSection.substring(0, pageListSection.indexOf("</section>"));
        pageListSection = pageListSection.substring(pageListSection.indexOf("<option"));
//        System.out.println(pageListSection);
        pageList = new ArrayList<URL>();
        do {
            int firstIndex = pageListSection.indexOf("http://");
            int secondIndex = pageListSection.indexOf("\"", firstIndex + 1);
            String newURL = pageListSection.substring(firstIndex, secondIndex);
//            System.out.println("newURL " + newURL);
            try {
                pageList.add(new URL(newURL));
            } catch (MalformedURLException e) {
            }
            int nextOption = pageListSection.indexOf("<option", 1);
            if (nextOption == -1) {
                break;
            }
            pageListSection = pageListSection.substring(nextOption);
        } while (true);
//        System.out.println(pageListSection);
    }

    public void updateChapters() {
        System.out.println("updateChapters");
        if (curChapterIndex == -1) {
            System.out.println("-1");
            for (int k = 0; k < chapterList.size(); k++) {
                String curChap = chapterList.get(k);
                if (currentChapter.toString().equals(curChap)) {
                    if (k != 0) {
                        try {
                            prevChapter = new URL(chapterList.get(k - 1));
                        } catch (MalformedURLException e) {
                        }
                    }
                    curChapterIndex = k;
                    if (k != chapterList.size() - 1) {
                        try {
                            nextChapter = new URL(chapterList.get(k + 1));
                        } catch (MalformedURLException e) {
                        }
                    }
                    break;
                }
            }
        } else {
            System.out.println("else");
            if (curChapterIndex != 0) {
                try {
                    prevChapter = new URL(chapterList.get(curChapterIndex - 1));
                } catch (MalformedURLException e) {
                }
            }
//            try {
//                currentChapter = new URL(chapterList.get(curChapterIndex));
//            } catch (MalformedURLException e) {
//            }
            if (curChapterIndex != chapterList.size() - 1) {
                try {
                    nextChapter = new URL(chapterList.get(curChapterIndex + 1));
                } catch (MalformedURLException e) {
                }
            }
        }
        System.out.println("current chapter index " + curChapterIndex);
    }

    public void updatePages() {
        if (curPageIndex == -1) {
            for (int i = 0; i < pageList.size(); i++) {
                String curPage = pageList.get(i).toString();
                if (currentPage.toString().equals(curPage)) {
                    if (i != 0) {
                        prevPage = pageList.get(i - 1);
                    }
                    curPageIndex = i;
                    if (i != pageList.size() - 1) {
                        nextPage = pageList.get(i + 1);
                    }
                    break;
                }
            }
        } else {
            if (curPageIndex != 0) {
                prevPage = pageList.get(curPageIndex - 1);
            }
            currentPage = pageList.get(curPageIndex);
            if (curPageIndex != pageList.size() - 1) {
                nextPage = pageList.get(curPageIndex + 1);
            }
        }

        getSupportActionBar().setTitle("The New Gate (" + (curPageIndex + 1) + "/" + pageList.size() + ")");
    }
}
