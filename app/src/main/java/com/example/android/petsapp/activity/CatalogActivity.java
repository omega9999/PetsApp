package com.example.android.petsapp.activity;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.petsapp.R;
import com.example.android.petsapp.db.DbUtils;
import com.example.android.petsapp.db.Pet;
import com.example.android.petsapp.db.PetContract;
import com.example.android.petsapp.db.PetCursorAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public CatalogActivity() {
        super();
        Log.i(TAG, "init HandlerThread");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            final Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        final ListView petListView = findViewById(R.id.list);
        mCursorAdapter = new PetCursorAdapter(this, null);
        final View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);
        petListView.setAdapter(mCursorAdapter);
        petListView.setOnItemClickListener((adapterView, view, position, id) -> {
            final Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            final Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);
            Log.d(TAG,"Uri send to Editor: " + uri);
            intent.setData(uri);
            startActivity(intent);
        });

        getLoaderManager().initLoader(PET_LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
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
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, (dialog, id) -> deletePet());
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        final int rows = DbUtils.deleteAllPet(this);
        Toast.makeText(this, getString(R.string.delete_all_pet_successful, String.valueOf(rows)), Toast.LENGTH_SHORT).show();
    }


    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        final Pet dummy = Pet.getDummyInstance();
        Pet pet = DbUtils.insertPet(this, dummy);
        Log.d(TAG, "Inserted pet with id = " + pet.getId());
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return DbUtils.getAllPetsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mPetNumber = data.getCount();
        mCursorAdapter.swapCursor(data);
        invalidateOptionsMenu();
    }


    @Override
    public boolean onPrepareOptionsMenu(@NonNull final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem menuItem = menu.findItem(R.id.action_delete_all_entries);
        if (mPetNumber == 0) {
            menuItem.setVisible(false);
        }
        else{
            menuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private CursorAdapter mCursorAdapter;

    private int mPetNumber = 0;

    private static final int PET_LOADER_ID = 1;
    private static final String TAG = CatalogActivity.class.getSimpleName();

}