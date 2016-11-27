package jsl2449.TheNewGateReader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChapterListActivity extends AppCompatActivity implements URLFetch.Callback {

    private ListView lvChapters;
    private ChapterItemAdapter chaptersAdapter;
    public List<MangaChapter> listChapters;
    private URL currentChapter;
    private URL currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list_viewer);

        getSupportActionBar().setTitle("The New Gate Chapter List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvChapters = (ListView) findViewById(R.id.chapter_list);

        getChapterList();

        System.out.println("chapter list activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chapter_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Toast.makeText(getApplicationContext(), "settings hit", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home){
            Toast.makeText(this, "chapter list home thing pressed", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getChapterList() {
        try {
            new URLFetch(this, new URL("http", "www.mangahere.co", "/manga/the_new_gate/"));
        } catch (MalformedURLException e) {
            Toast.makeText(this, "Unable to get chapter list", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            String pageURL = extras.getString("currentPage");
            String chapterURL = extras.getString("currentChapter");
            System.out.println("chapter activity result");
            System.out.println(pageURL);
            System.out.println(chapterURL);
            try {
                currentPage = new URL(pageURL);
                currentChapter = new URL(chapterURL);
            } catch (MalformedURLException e) {
            }
            System.out.println("onActivityResult chapter list " + currentPage.toString());
        }
        System.out.println("back to chapter list activity");
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent();
        if (currentPage != null) {
            goBack.putExtra("currentPage", currentPage.toString());
            goBack.putExtra("currentChapter", currentChapter.toString());
            System.out.println("chapter list back button pressed " + currentPage.toString());
            List<String> listStrChapters = new ArrayList<String>();
            for (int i = 0; i < listChapters.size(); i++) {
                MangaChapter x = (listChapters.get(listChapters.size() - i - 1));
                listStrChapters.add(x.chapterURL.toString());
            }
            goBack.putExtra("chapterList", (ArrayList) listStrChapters);
        }
        setResult(RESULT_OK, goBack);
        finish();
    }

    @Override
    public void fetchStart() {
    }

    @Override
    public void fetchComplete(String result) {
        if (result == null) {
            System.out.println("couldn't hit the website, should handle");
            return;
        }
        result = result.substring(result.indexOf("<div class=\"title\"><span></span><h3>Read The New Gate Online</h3></div>"));
        result = result.substring(result.indexOf("href"));
        result = result.substring(0, result.indexOf("</ul><ul class=\"tab_comment clearfix\">"));
//        System.out.println(result)
        String strURL = null;
        String strTitle = null;
        listChapters = new ArrayList<MangaChapter>();
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
            try {
                newChapter.chapterURL = new URL(strURL);
                newChapter.title = strTitle;
                listChapters.add(newChapter);
            } catch (MalformedURLException e) {
            }

            if (result.indexOf("href", indexHref + 1) == -1) {
                break;
            }
            result = result.substring(result.indexOf("href", indexHref + 1));
//            System.out.println(strURL);
//            System.out.println(strTitle);
//            System.out.println(result);
        } while (strURL != null && strTitle != null);
        chaptersAdapter = new ChapterItemAdapter(this, listChapters);
        lvChapters.setAdapter(chaptersAdapter);
//        System.out.println(result);
    }
}
