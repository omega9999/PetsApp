package com.example.android.petsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.petsapp.R;
import com.example.android.petsapp.db.DbUtils;
import com.example.android.petsapp.db.Pet;
import com.example.android.petsapp.db.PetCursorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    public CatalogActivity() {
        super();
        Log.i(TAG, "init HandlerThread");
        this.mHandlerThread = new HandlerThread(this.getClass() + ".Thread");
        this.mHandlerThread.start(); // close it with mHandlerThread.quit()
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mUIHandler = new Handler(Looper.getMainLooper());
    }

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

        final ListView listView = findViewById(R.id.list);
        mCursorAdapter = DbUtils.getPetCursorAdapter(this);
        listView.setAdapter(mCursorAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DbUtils.reloadPetCursorAdapter(this, mCursorAdapter);
    }

    @Override
    protected void onDestroy() {
        this.mHandlerThread.quit();
        super.onDestroy();
    }

    /**
     * method to add menu items to the app bar.
     *
     * @param menu
     * @return
     */
    @CheckResult
    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /**
     * manage click on menu app bar
     *
     * @param item
     * @return
     */
    @CheckResult
    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet();

                return true;
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
            final Pet dummy = Pet.getDummyInstance();
            DbUtils.insertPet(this, dummy);
            DbUtils.reloadPetCursorAdapter(this, mCursorAdapter);
    }

    private CursorAdapter mCursorAdapter;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUIHandler;


    private static final String TAG = CatalogActivity.class.getSimpleName();
}