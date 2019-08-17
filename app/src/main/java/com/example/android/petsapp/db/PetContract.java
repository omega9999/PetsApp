package com.example.android.petsapp.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public final class PetContract {

    private PetContract() {
    }


    public static abstract class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";


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
