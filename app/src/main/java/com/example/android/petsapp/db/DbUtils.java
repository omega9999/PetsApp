package com.example.android.petsapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import com.example.android.petsapp.db.PetContract.PetEntry;

public class DbUtils {

    @CheckResult
    @NonNull
    public static Pet insertPet(@NonNull final Context context, @NonNull final Pet pet){
        final PetDbHelper mDbHelper = new PetDbHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,pet.getName());
        values.put(PetEntry.COLUMN_PET_BREED,pet.getBreed());
        values.put(PetEntry.COLUMN_PET_GENDER,pet.getGender());
        values.put(PetEntry.COLUMN_PET_WEIGHT,pet.getWeight());

        long newRowId = db.insert(PetEntry.TABLE_NAME,null, values);
        Log.d(TAG,String.format("New row id = %1$s",newRowId));
        pet.setId(newRowId);
        db.close();
        return pet;
    }

    private static final String TAG = DbUtils.class.getSimpleName();
}
