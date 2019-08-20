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

import com.example.android.petsapp.R;
import com.example.android.petsapp.db.PetContract.PetEntry;
import com.example.android.petsapp.exception.IllegalUriException;
import com.example.android.petsapp.exception.InvalidArgumentException;

public class PetProvider extends ContentProvider {
    public PetProvider() {
    }

    @Override
    public boolean onCreate() {
        if (getContext() != null) {
            PetDbHelper mDbHelper = new PetDbHelper(getContext());
            Log.d(TAG, "Open connection (writable) to database");
            this.mDatabase = mDbHelper.getWritableDatabase();
        }
        else{
            throw new RuntimeException("Context null!!!");
        }
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.mDatabase != null) {
            Log.d(TAG, "Close connection to database");
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
                throw new IllegalUriException("Cannot query unknown URI: %1$s", uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull final Uri uri, @Nullable final ContentValues values) {
        int match = URI_MATCHER.match(uri);
        if (match == PETS) {
            validationInsert(values);

            long id = mDatabase.insert(PetEntry.TABLE_NAME, null, values);
            // return Uri with id appended
            return ContentUris.withAppendedId(uri, id);
        } else {
            // URI unknown
            throw new IllegalUriException("Cannot insert unknown URI: %1$s", uri);
        }
    }


    @Override
    public int update(@NonNull final Uri uri, @Nullable final ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                String selectionLocal = PetEntry._ID + "=?";
                String[] selectionArgsLocal = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selectionLocal, selectionArgsLocal);
            default:
                // URI unknown
                throw new IllegalUriException("Cannot update unknown URI: %1$s", uri);
        }
    }

    @Override
    public int delete(@NonNull final Uri uri, String whereClause, String[] whereArgs) {
        // Implement this to handle requests to delete one or more rows.
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                return mDatabase.delete(PetEntry.TABLE_NAME, whereClause, whereArgs);
            case PET_ID:
                String selectionLocal = PetEntry._ID + "=?";
                String[] selectionArgsLocal = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return mDatabase.delete(PetEntry.TABLE_NAME, selectionLocal, selectionArgsLocal);

            default:
                // URI unknown
                throw new IllegalUriException("Cannot delete unknown URI: %1$s", uri);
        }
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void validationInsert(@Nullable final ContentValues values) {
        if (values == null || values.size() == 0) {
            throw new InvalidArgumentException(getContext(),R.string.missin_data_pet);
        }

        final String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new InvalidArgumentException(getContext(),R.string.missing_name);
        }

        final String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        if (TextUtils.isEmpty(breed)) {
            throw new InvalidArgumentException(getContext(),R.string.missing_breed);
        }

        final Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null) {
            throw new InvalidArgumentException(getContext(),R.string.missing_gender);
        }
        if (!PetEntry.isValidGender(gender)) {
            throw new InvalidArgumentException(getContext(),R.string.invalid_gender);
        }

        final Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight == null) {
            throw new InvalidArgumentException(getContext(),R.string.missing_weight);
        }
        if (weight < 0) {
            throw new InvalidArgumentException(getContext(),R.string.invalid_weight , weight);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        if (values == null || values.size() == 0) {
            return 0;
        }
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            final String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new InvalidArgumentException(getContext(),R.string.missing_name);
            }
        }

        if (values.containsKey(PetEntry.COLUMN_PET_BREED)) {
            final String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
            if (TextUtils.isEmpty(breed)) {
                throw new InvalidArgumentException(getContext(), R.string.missing_breed);
            }
        }

        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            final Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null) {
                throw new InvalidArgumentException(getContext(),R.string.missing_gender);
            }
            if (!PetEntry.isValidGender(gender)) {
                throw new InvalidArgumentException(getContext(),R.string.invalid_gender);
            }
        }
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            final Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight == null) {
                throw new InvalidArgumentException(getContext(),R.string.missing_weight);
            }
            if (weight < 0) {
                throw new InvalidArgumentException(getContext(),R.string.invalid_weight , weight);
            }
        }
        return mDatabase.update(PetEntry.TABLE_NAME, values, whereClause, whereArgs);
    }


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
