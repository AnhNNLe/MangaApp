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

public class ChapterListActivity extends AppCompatActivity implements ParseChapter.Callback {

    private ListView lvChapters;
    private ChapterItemAdapter chapterAdapter;
    public List<MangaChapter> chapterList;
    private Bookmark resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list_viewer);

        getSupportActionBar().setTitle("The New Gate Chapter List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvChapters = (ListView) findViewById(R.id.chapter_list);
        new ParseChapter(this);
    }

    public void updateChapterList(List<MangaChapter> chapterList) {
//        System.out.println("update chapter list");
        this.chapterList = chapterList;
        chapterAdapter = new ChapterItemAdapter(this, this.chapterList);
        lvChapters.setAdapter(chapterAdapter);
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
//            Toast.makeText(getApplicationContext(), "settings hit", Toast.LENGTH_SHORT).show();
            Intent set = new Intent(getApplicationContext(), Settings.class);
            startActivity(set);
            return true;
        } else if (id == android.R.id.home) {
//            Toast.makeText(this, "chapter list home thing pressed", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            resume = (Bookmark) extras.get("resume");
        }
    }

    @Override
    public void onBackPressed() {
        Intent goBack = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("resume", resume);
        goBack.putExtras(bundle);
        setResult(RESULT_OK, goBack);
        finish();
    }


}
