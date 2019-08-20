package com.example.android.petsapp.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.CursorAdapter;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.example.android.petsapp.db.PetContract.PetEntry;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {

    @CheckResult
    @NonNull
    public static Pet insertPet(@NonNull final Context context, @NonNull final Pet pet) {

        final ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, pet.getName());
        values.put(PetEntry.COLUMN_PET_BREED, pet.getBreed());
        values.put(PetEntry.COLUMN_PET_GENDER, pet.getGender());
        values.put(PetEntry.COLUMN_PET_WEIGHT, pet.getWeight());

        Uri res = context.getContentResolver().insert(PetEntry.CONTENT_URI, values);
        long newRowId = ContentUris.parseId(res);
        Log.d(TAG, String.format("New row id = %1$s", newRowId));
        Pet petRes = (Pet) pet.clone();
        petRes.setId(newRowId);
        return petRes;
    }

    static Pet convertCursor2Pet(@NonNull final Context context, @NonNull final Cursor cursor) {
        final Pet pet = new Pet();
        int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

        final long currentID = cursor.getLong(idColumnIndex);
        final String currentName = cursor.getString(nameColumnIndex);
        final String currentBreed = cursor.getString(breedColumnIndex);
        final int currentGender = cursor.getInt(genderColumnIndex);
        final int currentWeight = cursor.getInt(weightColumnIndex);

        if (idColumnIndex >= 0) {
            pet.setId(currentID);
        }
        if (nameColumnIndex >= 0) {
            pet.setName(currentName);
        }
        if (breedColumnIndex >= 0) {
            pet.setBreed(currentBreed);
        }
        if (genderColumnIndex >= 0) {
            pet.setGender(currentGender);
        }
        if (weightColumnIndex >= 0) {
            pet.setWeight(currentWeight);
        }
        return pet;
    }

    private static Cursor getAllPets(@NonNull final Context context) {
        Cursor cursor = null;
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = null;

        // db to query may be in other apps with this method: context.getContentResolver()
        cursor = context.getContentResolver().query(PetEntry.CONTENT_URI, projection,
                selection, selectionArgs, orderBy);
        return cursor;
    }

    public static void reloadPetCursorAdapter(@NonNull final Context context, @NonNull final CursorAdapter petCursorAdapter) {
        Cursor cursor = getAllPets(context);
        petCursorAdapter.changeCursor(cursor);
    }

    public static PetCursorAdapter getPetCursorAdapter(@NonNull final Context context) {
        PetCursorAdapter petCursorAdapter = null;

        petCursorAdapter = new PetCursorAdapter(context, getAllPets(context));
        return petCursorAdapter;
    }

    public static String getPlainTextPets(@NonNull final Context context) {
        StringBuilder builder = new StringBuilder();
        Cursor cursor = null;
        try {
            cursor = getAllPets(context);

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
        }
        return builder.toString();
    }

    public static int getNumberOfPets(@NonNull final Context context) {
        int count;
        Cursor cursor = null;
        try {

            String[] projection = null;
            String selection = null;
            String[] selectionArgs = null;
            String orderBy = null;

            cursor = context.getContentResolver().query(PetEntry.CONTENT_URI, projection,
                    selection, selectionArgs,
                    orderBy);
            count = cursor.getCount();
        } finally {
            if (cursor != null) {
                cursor.close();
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

        List<Pet> pets = new ArrayList<>();

        String[] projection = {PetEntry.COLUMN_PET_BREED, PetEntry.COLUMN_PET_WEIGHT};
        String selection = PetEntry.COLUMN_PET_GENDER + "=?";
        String[] selectionArgs = new String[]{String.valueOf(PetEntry.GENDER_FEMALE)};
        String orderBy = null;

        Cursor c = context.getContentResolver().query(PetEntry.CONTENT_URI, projection,
                selection, selectionArgs,
                orderBy);
        while (c.moveToNext()) {
            Pet pet = new Pet();
            pet.setBreed(c.getString(c.getColumnIndex(PetEntry.COLUMN_PET_BREED)))
                    .setWeight(c.getInt(c.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)));

            pets.add(pet);
        }

        c.close();
        return pets;
    }

    private static final String TAG = DbUtils.class.getSimpleName();
}
