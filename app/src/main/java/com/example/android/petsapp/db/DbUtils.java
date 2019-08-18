package com.example.android.petsapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.example.android.petsapp.db.PetContract.PetEntry;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {

    @CheckResult
    @NonNull
    public static Pet insertPet(@NonNull final Context context, @NonNull final Pet pet) {
        final PetDbHelper mDbHelper = new PetDbHelper(context);
        try (final SQLiteDatabase db = mDbHelper.getWritableDatabase()) {

            final ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, pet.getName());
            values.put(PetEntry.COLUMN_PET_BREED, pet.getBreed());
            values.put(PetEntry.COLUMN_PET_GENDER, pet.getGender());
            values.put(PetEntry.COLUMN_PET_WEIGHT, pet.getWeight());

            long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
            Log.d(TAG, String.format("New row id = %1$s", newRowId));
            pet.setId(newRowId);
        }
        return pet;
    }

    public static String getPlainTextPets(@NonNull final Context context) {
        StringBuilder builder = new StringBuilder();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            PetDbHelper mDbHelper = new PetDbHelper(context);
            db = mDbHelper.getReadableDatabase();
            String[] projection = {
                    PetEntry._ID,
                    PetEntry.COLUMN_PET_NAME,
                    PetEntry.COLUMN_PET_BREED,
                    PetEntry.COLUMN_PET_GENDER,
                    PetEntry.COLUMN_PET_WEIGHT
            };
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;

            cursor = db.query(PetEntry.TABLE_NAME, projection,
                    selection, selectionArgs,
                    groupBy, having, orderBy);
            final int count = cursor.getCount();
            builder.append("The pets table contains ").append(count).append(" pets.\n\n")
                    .append(PetEntry._ID + " - " +
                            PetEntry.COLUMN_PET_NAME + " - " +
                            PetEntry.COLUMN_PET_BREED + " - " +
                            PetEntry.COLUMN_PET_GENDER + " - " +
                            PetEntry.COLUMN_PET_WEIGHT +
                            "\n");
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);

            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);

                String currentBreed = cursor.getString(breedColumnIndex);
                int currentGender = cursor.getInt(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);

                builder.append("\n").append(currentID).append(" - ").append(currentName).append(" - ").append(currentBreed).append(" - ").append(currentGender).append(" - ").append(currentWeight);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return builder.toString();
    }

    public static int getNumberOfPets(@NonNull final Context context) {
        int count;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            final PetDbHelper mDbHelper = new PetDbHelper(context);

            db = mDbHelper.getReadableDatabase();
            String[] projection = null;
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = null;

            cursor = db.query(PetEntry.TABLE_NAME, projection,
                    selection, selectionArgs,
                    groupBy, having, orderBy);
            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return count;
    }

    /**
     * example of query with selection / selectionArgs
     *
     * @param context
     * @return
     */
    private static List<Pet> getFemale(@NonNull final Context context) {
        final PetDbHelper mDbHelper = new PetDbHelper(context);
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        List<Pet> pets = new ArrayList<>();

        String[] projection = {PetEntry.COLUMN_PET_BREED, PetEntry.COLUMN_PET_WEIGHT};
        String selection = PetEntry.COLUMN_PET_GENDER + "=?";
        String[] selectionArgs = new String[]{String.valueOf(PetEntry.GENDER_FEMALE)};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Cursor c = db.query(PetEntry.TABLE_NAME, projection,
                selection, selectionArgs,
                groupBy, having, orderBy);
        while (c.moveToNext()) {
            Pet pet = new Pet();
            pet.setBreed(c.getString(c.getColumnIndex(PetEntry.COLUMN_PET_BREED)))
                    .setWeight(c.getInt(c.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)));

            pets.add(pet);
        }

        c.close();
        db.close();
        return pets;
    }

    private static final String TAG = DbUtils.class.getSimpleName();
}
