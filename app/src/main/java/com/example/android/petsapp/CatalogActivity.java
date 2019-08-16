package com.example.android.petsapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.petsapp.db.PetContract.PetEntry;
import com.example.android.petsapp.db.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        this.mHandlerThread = new HandlerThread(CatalogActivity.class + ".Thread");
        this.mHandlerThread.start(); // close it with mHandlerThread.quit()
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mUIHandler = new Handler(Looper.getMainLooper());


        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        mHandler.post(()-> {
            PetDbHelper mDbHelper = new PetDbHelper(this);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            try (Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null)) {
                final int count = cursor.getCount();
                mUIHandler.post(()-> {
                    TextView displayView = findViewById(R.id.text_view_pet);
                    displayView.setText("Number of rows in pets database table: " + count);
                });
            }
        });
    }

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUIHandler;


    private static final String TAG = CatalogActivity.class.getSimpleName();
}