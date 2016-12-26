package jsl2449.TheNewGateReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Joe on 12/1/2016.
 */

public class BookmarkItemAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Bookmark> list;
    private Context context;
    DbHelper dbH;

    public BookmarkItemAdapter(Context context, List<Bookmark> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        dbH = new DbHelper(context);
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (list != null) {
            return list.get(position).hashCode();
        } else {
            return 0;
        }
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Bookmark data = (Bookmark) getItem(i);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null blaralshdgl");
        }

        if (view == null) {
            view = inflater.inflate(R.layout.bookmark_item, viewGroup, false);
        }
        final int position = i;
        bindView(data, view, viewGroup, position);
        return view;
    }

    public void bindView(final Bookmark data, View view, ViewGroup parent, final int position) {
        TextView chapterTextBox = (TextView) view.findViewById(R.id.tvChapterNum);
        chapterTextBox.setText("Chapter " + data.currentChapter);
        TextView pagesTB = (TextView) view.findViewById(R.id.tvPagesStuff);
        pagesTB.setText("" + (data.currentPage + 1) + " / " + data.totalPages);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPageIntent = new Intent(context, PageViewerActivity.class);
                Bundle myExtras = new Bundle();
                myExtras.putSerializable("resume", data);
                viewPageIntent.putExtras(myExtras);
                final int result = 1;
                ((Activity) context).startActivityForResult(viewPageIntent, result);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                list.remove(position);
                SQLiteDatabase db = dbH.getWritableDatabase();
                db.delete(DbHelper.TABLE_NAME, "id = " + data.id, null);
                db.close();
                notifyDataSetChanged();
                return true;
            }
        });
    }
}
