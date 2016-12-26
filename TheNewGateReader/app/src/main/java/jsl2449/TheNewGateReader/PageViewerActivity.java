package jsl2449.TheNewGateReader;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class PageViewerActivity extends AppCompatActivity implements ParseChapter.Callback {

    public class CacheString {
        String pageURL;
        String imageURL;

        public CacheString(String pageURL, String imageURL) {
            this.pageURL = pageURL;
            this.imageURL = imageURL;
        }
    }

    private List<String> pageList;
    private List<MangaChapter> chapterList;
    private Bookmark resume;
    private ImageView pageIV;
    private int currentChapter = -1;
    private boolean moveToEnd = false;
    private boolean caching = true;
    private int maxCache = 5;
    private int currentCached = 0;
    private SharedPreferences sharedPref;
    private Queue<CacheString> cacheQueue = new PriorityQueue<>();

    DbHelper dbH;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_viewer);
        getSupportActionBar().setTitle("The New Gate Reader");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageIV = (ImageView) findViewById(R.id.pageIV);
        pageIV.setOnTouchListener(new OnSwipeTouchListener(this));

        // get caching amount
        sharedPref = getSharedPreferences("TheNewGateReader", 0);
        maxCache = sharedPref.getInt("cacheAmount", 5);

        dbH = new DbHelper(getApplicationContext());
        db = dbH.getWritableDatabase();

        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        if (callingBundle != null) {
//            System.out.println("calling bundle != null");
            // update current page view
            resume = (Bookmark) callingBundle.get("resume");
//            new ParsePage(resume, this, true);
            // first get chapter list
            new ParseChapter(this);
        }
    }

    public void cache() {
        if (caching) {
//            System.out.println("cached " + currentCached);
            if (currentCached < maxCache && resume.currentPage + currentCached < resume.totalPages) {
                Bookmark x = new Bookmark();
                currentCached++;
                x.pageURL = pageList.get(resume.currentPage + currentCached);
                new ParsePage(x, this);
            }
        }
    }

    public void cache(String pageURL, String imageURL) {
        cacheQueue.add(new CacheString(pageURL, imageURL));
    }

    public void changePagePrev() {
//        System.out.println("changePagePrev " + currentChapter);
        if (resume.currentPage == 0) {
            if (currentChapter == chapterList.size() - 1) { // at first chapter, because the chapter list is backwards
                Toast.makeText(this, "Reached the start of manga", Toast.LENGTH_SHORT).show();
            } else {
                moveToEnd = true;
                currentChapter++;
                resume.pageURL = chapterList.get(currentChapter).chapterURL;
                new ParsePage(resume, this, true);
            }
        } else {
            resume.currentPage--;
            resume.pageURL = pageList.get(resume.currentPage);
            new ParsePage(resume, this, false);
            updatePageNumber(resume.currentPage, resume.totalPages);
        }
    }

    public void changePageNext() {
//        System.out.println("changePageNext " + currentChapter);
        if (resume.currentPage == resume.totalPages - 1) {
            System.out.println("next chapter " + currentChapter);
            if (currentChapter == 0) {
                Toast.makeText(this, "Reached the end of manga", Toast.LENGTH_SHORT).show();
            } else {
//                System.out.println("go to nexct chapter");
                currentChapter--;
                resume.pageURL = chapterList.get(currentChapter).chapterURL;
                new ParsePage(resume, this, true);
            }
        } else {
            resume.currentPage++;
            resume.pageURL = pageList.get(resume.currentPage);
            new ParsePage(resume, this, false);
            updatePageNumber(resume.currentPage, resume.totalPages);
            if (currentCached > 0) {
                currentCached--;
//                cacheQueue.remove();
            }
        }
    }

    public void updateView(String imageKey) {
//        System.out.println("updateView");
        Bitmap b = BitmapCache.getInstance().getBitmap(imageKey);
        if (b != null) {
            pageIV.setImageBitmap(BitmapCache.getInstance().getBitmap(imageKey));
            cache();
        } else {
            new ParsePage(resume, this, true);
        }
    }

    public void updateChapterList(List<MangaChapter> list) {
        System.out.println("updateChapterList");
        this.chapterList = list;
        // after chapter list updated, get the current page to display
        System.out.println("resume " + resume);
        new ParsePage(resume, this, true);
    }

    // chapter list is actually backwards
    // chapter 1 is = (pageList.size() - 1)
    // the lastest chapter is at index 0
    public void updateCurrentChapter() {
        String firstPage = this.pageList.get(0);
        for (int i = 0; i < this.chapterList.size(); i++) {
            if (firstPage.equals(this.chapterList.get(i).chapterURL)) {
                currentChapter = i;
                resume.currentChapter = this.chapterList.size() - i;
                break;
            }
        }

    }

    public void updatePageList(List<String> list) {
        this.pageList = list;
        updateCurrentChapter();
        if (moveToEnd) {
            moveToEnd = false;
            resume.pageURL = pageList.get(pageList.size() - 1);
            resume.currentPage = pageList.size() - 1;
            resume.totalPages = pageList.size();
            new ParsePage(resume, this, true);
        }
    }

    public void updatePageNumber(int pageNum, int totalPages) {
        resume.currentPage = pageNum;
        resume.totalPages = totalPages;
        getSupportActionBar().setTitle("The New Gate (" + (pageNum + 1) + "/" + totalPages + ")");
    }

    public void bookmark() {

        ContentValues cv = new ContentValues();
        cv.put(DbHelper.CURRENT_CHAPTER, resume.currentChapter);
        cv.put(DbHelper.CURRENT_PAGE, resume.currentPage);
        cv.put(DbHelper.TOTAL_PAGES, resume.totalPages);
        cv.put(DbHelper.PAGE_URL, DatabaseUtils.sqlEscapeString(resume.pageURL));
        db = dbH.getWritableDatabase();
        db.insert(DbHelper.TABLE_NAME, null, cv);
        db.close();

        Toast.makeText(getApplicationContext(), "Page bookmarked", Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
//            Toast.makeText(getApplicationContext(), "settings hit", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
//            Toast.makeText(this, "page home thing pressed", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("resume", resume);
        goBack.putExtras(bundle);
//        System.out.println("back button pressed " + currentPage.toString());
        setResult(RESULT_OK, goBack);
        finish();
    }

}
