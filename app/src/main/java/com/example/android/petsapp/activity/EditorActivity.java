package com.example.android.petsapp.activity;


import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.android.petsapp.R;
import com.example.android.petsapp.db.DbUtils;
import com.example.android.petsapp.db.Pet;
import com.example.android.petsapp.db.PetContract.PetEntry;

import java.util.Locale;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public EditorActivity() {
        super();
        Log.i(TAG, "init HandlerThread");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertPet();
                return true;
            case R.id.action_delete:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        mUri = getIntent().getData();
        if (mUri != null) {
            setTitle(R.string.editor_activity_title_edit_pet);
            getLoaderManager().initLoader(PET_LOADER_ID, null, this);

        } else {
            setTitle(R.string.editor_activity_title_new_pet);
        }

        setupSpinner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_string_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });
    }


    private void insertPet() {
        final Pet pet = new Pet();
        pet.setName(mNameEditText.getText().toString().trim())
                .setBreed(mBreedEditText.getText().toString().trim())
                .setGender(mGender)
                .setWeight(Integer.valueOf(mWeightEditText.getText().toString().trim()));
        final Pet newPet = DbUtils.insertPet(this, pet);
        if (newPet.getId() > 0) {
            Toast.makeText(EditorActivity.this, String.format("Pet saved with id: %1$s", newPet.getId()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditorActivity.this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        }
        EditorActivity.this.finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(final int id, @Nullable final Bundle args) {
        return DbUtils.getPetLoader(this, mUri);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            final Pet pet = DbUtils.convertCursor2Pet(this, data);
            if (pet != null) {
                mNameEditText.setText(pet.getName());
                mBreedEditText.setText(pet.getBreed());
                mWeightEditText.setText(String.format("%1$s",pet.getWeight()));
                mGender = pet.getGender();
                selectSpinner(mGender);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mWeightEditText.setText(null);
        mGender = PetEntry.GENDER_UNKNOWN;
        selectSpinner(mGender);
    }

    private void selectSpinner(int gender){
        switch (gender) {
            case PetEntry.GENDER_MALE:
                mGenderSpinner.setSelection(1);
                break;
            case PetEntry.GENDER_FEMALE:
                mGenderSpinner.setSelection(2);
                break;
            default:
                mGenderSpinner.setSelection(0);
                break;
        }
    }

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private int mGender;

    private Uri mUri = null;

    private static final String URI_BUNDLE = "URI_BUNDLE";

    private static final int PET_LOADER_ID = 1;
    private static final String TAG = EditorActivity.class.getSimpleName();

}