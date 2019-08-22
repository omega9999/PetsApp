package com.example.android.petsapp.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.petsapp.db.PetContract.PetEntry;

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

    public static Pet convertCursor2Pet(@NonNull final Context context, @NonNull final Cursor cursor) {
        if (cursor.isAfterLast()){
            return null;
        }
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

    public static Loader<Cursor> getAllPetsLoader(@NonNull final Context context) {
        final String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        final String selection = null;
        final String[] selectionArgs = null;
        final String orderBy = null;

        // db to query may be in other apps with this method: context.getContentResolver()
        final CursorLoader cursorLoader = new CursorLoader(context, PetContract.PetEntry.CONTENT_URI,
        projection, selection,  selectionArgs,orderBy);
        Log.d(TAG,"Create cursor loader");
        return cursorLoader;
    }

    public static Loader<Cursor> getPetLoader(@NonNull final Context context, @Nullable final Uri uri) {
        final String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };
        final String selection = PetEntry._ID + " = ?";
        final String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
        final String orderBy = null;

        // db to query may be in other apps with this method: context.getContentResolver()
        final CursorLoader cursorLoader = new CursorLoader(context, PetContract.PetEntry.CONTENT_URI,
                projection, selection,  selectionArgs,orderBy);
        Log.d(TAG,"Create cursor loader");
        return cursorLoader;
    }



    private static final String TAG = DbUtils.class.getSimpleName();
}
