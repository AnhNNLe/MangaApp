package jsl2449.TheNewGateReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static public String AppName = "TheNewGateReader";
    private Button btnResume;
    private Button btnChapterList;
    private Button btnBookmarks;
    private Bookmark resume;
    DbHelper dbH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("The New Gate Reader");
        setSupportActionBar(toolbar);

        // set up bookmarks database
        dbH = new DbHelper(getApplicationContext());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        BitmapCache.cacheSize = maxMemory / 4;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BitmapCache.maxW = size.x;
        BitmapCache.maxH = size.y;

        btnChapterList = (Button) findViewById(R.id.btnChapterList);
        btnChapterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chapterListIntent = new Intent(getApplicationContext(), ChapterListActivity.class);
                final int result = 1;
                startActivityForResult(chapterListIntent, result);
            }
        });

        btnResume = (Button) findViewById(R.id.btnResume);
        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resume == null) {
                    Toast.makeText(view.getContext(), "No current place to resume", Toast.LENGTH_SHORT).show();
                } else {
                    Intent viewPageIntent = new Intent(view.getContext(), PageViewerActivity.class);
                    Bundle myExtras = new Bundle();
                    myExtras.putSerializable("resume", resume);
                    viewPageIntent.putExtras(myExtras);
                    final int result = 1;
                    startActivityForResult(viewPageIntent, result);
                }
            }
        });

        btnBookmarks = (Button) findViewById(R.id.btnBookmarks);
        btnBookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewBookmarksIntent = new Intent(view.getContext(), BookmarksActivity.class);
                final int result = 1;
                startActivityForResult(viewBookmarksIntent, result);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            resume = (Bookmark) extras.get("resume");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit) {
            finish();
            return true;
        } else if (id == R.id.settings) {
//            Toast.makeText(getApplicationContext(), "settings hit", Toast.LENGTH_SHORT).show();
            Intent set = new Intent(this, Settings.class);
            startActivity(set);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
