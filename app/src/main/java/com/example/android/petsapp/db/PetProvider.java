package com.example.android.petsapp.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.petsapp.db.PetContract.PetEntry;

public class PetProvider extends ContentProvider {
    public PetProvider() {
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        if (getContext() != null) {
            this.mDbHelper = new PetDbHelper(getContext());
            this.mDatabase = mDbHelper.getWritableDatabase();
        }
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.mDatabase != null){
            this.mDatabase.close();
        }
        super.finalize();
    }

    @Override
    public Cursor query(@NonNull final Uri uri, @Nullable final String[] projection, @Nullable final String selection,
                        @Nullable final String[] selectionArgs, @Nullable final String sortOrder) {
        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                cursor = mDatabase.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                String selectionLocal = PetEntry._ID + "=?";
                String[] selectionArgsLocal = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = mDatabase.query(PetEntry.TABLE_NAME, projection, selectionLocal, selectionArgsLocal, null, null, sortOrder);
                break;
            default:
                // URI unknown
                throw new IllegalArgumentException(String.format("Cannot query unknown URI: %1$s", uri));
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        int match = URI_MATCHER.match(uri);
        if (match == PETS) {
            validation(values);

            long id = mDatabase.insert(PetEntry.TABLE_NAME, null, values);
            // return Uri with id appended
            return ContentUris.withAppendedId(uri, id);
        } else {
            // URI unknown
            throw new IllegalArgumentException(String.format("Cannot query unknown URI: %1$s", uri));
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match = URI_MATCHER.match(uri);
        if (match == PET_ID) {
            //TODO
        } else {
            // URI unknown
            throw new IllegalArgumentException(String.format("Cannot query unknown URI: %1$s", uri));
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:

                break;
            case PET_ID:

                break;

            default:
                // URI unknown
                throw new IllegalArgumentException(String.format("Cannot query unknown URI: %1$s", uri));
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void validation(@Nullable final ContentValues values){
        if (values == null){
            throw new IllegalArgumentException("Missing data of pet");
        }

        final String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        final String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        if (TextUtils.isEmpty(breed)) {
            throw new IllegalArgumentException("Pet requires a breed");
        }

        final Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null) {
            throw new IllegalArgumentException("Pet requires a gender");
        }
        if (!PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Pet requires a allowed gender");
        }

        final Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight == null) {
            throw new IllegalArgumentException("Pet requires a weight");
        }
        if (weight < 0){
            throw new IllegalArgumentException("Pet requires a weight not negative: " + weight);
        }


    }

    private PetDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int PETS = 100;
    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int PET_ID = 101;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // int code MUST be unique into same URI_MATCHER
        URI_MATCHER.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        URI_MATCHER.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    private static final String TAG = PetProvider.class.getSimpleName();
}
