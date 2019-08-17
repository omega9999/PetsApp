package com.example.android.petsapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.example.android.petsapp.db.PetContract.PetEntry;

class PetDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Pet.db";

    public PetDbHelper(@NonNull final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull final SQLiteDatabase db) {
        PetEntry.onCreate(db);
    }

    @Override
    public void onUpgrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        PetEntry.onDrop(db);
        onCreate(db);
    }

    public void onDowngrade(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
