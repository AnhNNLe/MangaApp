package jsl2449.TheNewGateReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joe on 12/3/2016.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TheNewGateDB";
    public static final String TABLE_NAME = "bookmarks_table";
    public static final String ID = "id";
    public static final String CURRENT_CHAPTER = "currentChapter";
    public static final String CURRENT_PAGE = "currentPAGE";
    public static final String TOTAL_PAGES = "totalPages";
    public static final String PAGE_URL = "pageURL";
    public static final int VERSION = 1;

    private final String createDb = "create table if not exists " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CURRENT_CHAPTER + " INTEGER, "
            + CURRENT_PAGE + " INTEGER, "
            + TOTAL_PAGES + " INTEGER, "
            + PAGE_URL + " text)";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("drop table " + TABLE_NAME);
    }
}
