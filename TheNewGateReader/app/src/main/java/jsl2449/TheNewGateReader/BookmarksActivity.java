package jsl2449.TheNewGateReader;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {

    private List<Bookmark> bookmarks;
    private ListView lvBookmarks;
    private SQLiteDatabase db;
    private BookmarkItemAdapter bookmarkAdapter;
    private Bookmark resume;
    DbHelper dbH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvBookmarks = (ListView) findViewById(R.id.lvBookmarks);

        dbH = new DbHelper(getApplicationContext());
        db = dbH.getWritableDatabase();

        String[] columns = {DbHelper.ID, DbHelper.CURRENT_CHAPTER, DbHelper.CURRENT_PAGE, DbHelper.TOTAL_PAGES, DbHelper.PAGE_URL};
        db.beginTransaction();
        Cursor cursor = db.query(DbHelper.TABLE_NAME, columns, null, null, null, null, null);
        db.endTransaction();
        if (cursor.moveToFirst()) {
            bookmarks = new ArrayList<Bookmark>();
            while (cursor.moveToNext()) {
                Bookmark newBookmark = new Bookmark();
                newBookmark.id = cursor.getInt(cursor.getColumnIndex(DbHelper.ID));
                newBookmark.currentChapter = cursor.getInt(cursor.getColumnIndex(DbHelper.CURRENT_CHAPTER));
                newBookmark.currentPage = cursor.getInt(cursor.getColumnIndex(DbHelper.CURRENT_PAGE));
                newBookmark.totalPages = cursor.getInt(cursor.getColumnIndex(DbHelper.TOTAL_PAGES));
                newBookmark.pageURL = cursor.getString(cursor.getColumnIndex(DbHelper.PAGE_URL));
                bookmarks.add(newBookmark);
                System.out.println("new bookmark");
                System.out.println(newBookmark.toString());
            }
            cursor.close();
            bookmarkAdapter = new BookmarkItemAdapter(this, this.bookmarks);
            lvBookmarks.setAdapter(bookmarkAdapter);
//            Toast.makeText(this, "should update bookmarks", Toast.LENGTH_SHORT).show();
            if (bookmarks.size() <= 0){

                Toast.makeText(this, "No bookmarks", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No bookmarks", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            resume = (Bookmark) extras.get("resume");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
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
        setResult(RESULT_OK, goBack);
        finish();
    }

}
