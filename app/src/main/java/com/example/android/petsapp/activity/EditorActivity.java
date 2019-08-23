package com.example.android.petsapp.activity;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.android.petsapp.R;
import com.example.android.petsapp.db.DbUtils;
import com.example.android.petsapp.db.Pet;
import com.example.android.petsapp.db.PetContract.PetEntry;
import com.example.android.petsapp.exception.InvalidArgumentException;


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
                try {
                    savePet();
                } catch (InvalidArgumentException e) {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // go back to parent activity
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                else{
                    final DialogInterface.OnClickListener discardButtonClickListener =
                            (dialogInterface, i) -> NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnTouchListener mTouchListener = (view, motionEvent) -> {
        mPetHasChanged = true;
        view.performClick();
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        mUri = getIntent().getData();
        if (mUri != null) {
            setTitle(R.string.editor_activity_title_edit_pet);
            getLoaderManager().initLoader(PET_LOADER_ID, null, this);
            mId = ContentUris.parseId(mUri);
        } else {
            setTitle(R.string.editor_activity_title_new_pet);
            mId = null;
            // regenerate menu' invalidateOptionsMenu() -> onPrepareOptionsMenu()
            invalidateOptionsMenu();
        }
        setupSpinner();
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mUri == null) {
            final MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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

    private int string2Int(@Nullable final String str) {
        if (str == null || TextUtils.isEmpty(str.trim())) {
            return 0;
        } else {
            return Integer.valueOf(str.trim());
        }
    }

    private void savePet() {
        final Pet pet = new Pet();
        pet.setName(mNameEditText.getText().toString().trim())
                .setBreed(mBreedEditText.getText().toString().trim())
                .setGender(mGender)
                .setWeight(string2Int(mWeightEditText.getText().toString()));
        if (mId != null) {
            pet.setId(mId);
            int rows = DbUtils.updatePet(this, pet);
            if (rows == 1) {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_update_pet_successful), Toast.LENGTH_SHORT).show();
            } else {
                throw new InvalidArgumentException(this, R.string.editor_update_pet_failed);
            }
        } else {
            final Pet newPet = DbUtils.insertPet(this, pet);
            if (newPet.getId() > 0) {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_pet_successful, String.valueOf(newPet.getId())), Toast.LENGTH_SHORT).show();
            } else {
                throw new InvalidArgumentException(this, R.string.editor_insert_pet_failed);
            }
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
                mWeightEditText.setText(String.format("%1$s", pet.getWeight()));
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

    private void selectSpinner(int gender) {
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

    @Override
    public void onBackPressed() {
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }
        final DialogInterface.OnClickListener discardButtonClickListener = (dialogInterface, i) -> finish();
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showUnsavedChangesDialog(@NonNull final DialogInterface.OnClickListener discardButtonClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, (dialog, id) -> deletePet());
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (mId != null){
            final int rows = DbUtils.deletePet(this, mId);
            if (rows == 1) {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_delete_pet_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private int mGender;

    private Uri mUri = null;
    private boolean mPetHasChanged = false;


    private Long mId = null;

    private static final int PET_LOADER_ID = 1;
    private static final String TAG = EditorActivity.class.getSimpleName();

}