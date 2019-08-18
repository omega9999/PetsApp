package com.example.android.petsapp.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public final class PetContract {

    // see AndroidManifest.xml for CONTENT_AUTHORITY
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    private PetContract() {
    }


    public static abstract class PetEntry implements BaseColumns {
        // Uri for ContentProvider to access table pets
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        static final String TABLE_NAME = "pets";
        static final String _ID = BaseColumns._ID;
        static final String COLUMN_PET_NAME = "name";
        static final String COLUMN_PET_BREED = "breed";
        static final String COLUMN_PET_GENDER = "gender";
        static final String COLUMN_PET_WEIGHT = "weight";


        @Retention(SOURCE) // check on compile time
        @IntDef({GENDER_UNKNOWN, GENDER_MALE, GENDER_FEMALE}) // values allowed
        public @interface Gender {} // name new annotation
        
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        static void onCreate(@NonNull final SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        static void onDrop(@NonNull final SQLiteDatabase db) {
            db.execSQL(SQL_DELETE_ENTRIES);
        }

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PetEntry.TABLE_NAME + " (" +
                        PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
                        PetEntry.COLUMN_PET_BREED + " TEXT, " +
                        PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, " +
                        PetEntry.COLUMN_PET_WEIGHT + " INTEGER" +
                        ")";

        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
