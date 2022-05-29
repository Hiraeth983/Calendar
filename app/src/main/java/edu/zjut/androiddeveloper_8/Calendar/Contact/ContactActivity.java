package edu.zjut.androiddeveloper_8.Calendar.Contact;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;

import edu.zjut.androiddeveloper_8.Calendar.Contact.adapter.MyCursorAdapter;
import edu.zjut.androiddeveloper_8.Calendar.Contact.db.Contact;
import edu.zjut.androiddeveloper_8.Calendar.R;

public class ContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public ListView listView;
    public MyCursorAdapter adapter;
    public static final int CONTACTLOADER = 0;
    public Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // 设置浮动添加按钮
        FloatingActionButton floatButton = findViewById(R.id.fab);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        // 列表
        listView = findViewById(R.id.list);
        adapter = new MyCursorAdapter(this, null);
        listView.setAdapter(adapter);

        // 点击列表项的事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactActivity.this, UserActivity.class);
                Uri newUri = ContentUris.withAppendedId(Contact.ContactEntry.CONTENT_URI, id);
                Log.i("info", id + "");
                Log.i("newUri", newUri + "");

                intent.setData(newUri);
                startActivity(intent);
            }
        });

        // 激活Loader
        getLoaderManager().initLoader(CONTACTLOADER, null, this);

    }



    // 搜索栏及其逻辑
    // 模糊搜索名字
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("🔍姓名搜索");

        String[] projection = {Contact.ContactEntry._ID,
                Contact.ContactEntry.COLUMN_NAME,
                Contact.ContactEntry.COLUMN_TELEPHONE,
                Contact.ContactEntry.COLUMN_WORKPLACE,
                Contact.ContactEntry.COLUMN_ADDRESS,
                Contact.ContactEntry.COLUMN_TIME
        };
        Context thisContext = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = Contact.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%" + s + "%"};
                }
                adapter = new MyCursorAdapter(thisContext,
                        getContentResolver().query(Contact.ContactEntry.CONTENT_URI,
                                projection,
                                selection,
                                args,
                                null));
                listView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String selection;
                String[] args;
                if (TextUtils.isEmpty(s)) {
                    selection = null;
                    args = null;
                } else {
                    selection = Contact.ContactEntry.COLUMN_NAME + " like ?";
                    args = new String[]{"%" + s + "%"};
                }
                adapter = new MyCursorAdapter(thisContext,
                        getContentResolver().query(Contact.ContactEntry.CONTENT_URI,
                                projection,
                                selection,
                                args,
                                null));
                if (args == null) {
                    Log.i("Query", "selection=" + "null" + " args=" + "null" + " ");
                } else {
                    Log.i("Query", "selection=" + selection + " args[0]=" + args[0] + " ");
                }
                listView.setAdapter(adapter);
                return false;
            }
        });

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Contact.ContactEntry._ID,
                Contact.ContactEntry.COLUMN_NAME,
                Contact.ContactEntry.COLUMN_TELEPHONE,
                Contact.ContactEntry.COLUMN_WORKPLACE,
                Contact.ContactEntry.COLUMN_ADDRESS,
                Contact.ContactEntry.COLUMN_TIME
        };

        return new CursorLoader(this, Contact.ContactEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}