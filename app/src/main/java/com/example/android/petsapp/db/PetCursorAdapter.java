package com.example.android.petsapp.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android.petsapp.R;

public class PetCursorAdapter extends CursorAdapter {

    // N.B. method getView() implemented by CursorAdapter that call newView() and bindView()

    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public PetCursorAdapter(@NonNull final Context context, @NonNull final Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * ONLY INFLATE
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(@NonNull final Context context, @NonNull final Cursor cursor, @NonNull final ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * POPULATE VIEW FROM OUTSIDE
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(@NonNull final View view, @NonNull final Context context, @NonNull final Cursor cursor) {
        // Find fields to populate in inflated template
        final TextView name = view.findViewById(R.id.name);
        final TextView summary = view.findViewById(R.id.summary);
        // Extract properties from cursor
        final Pet pet = DbUtils.convertCursor2Pet(context, cursor);

        // Populate fields with extracted properties
        name.setText(pet.getName());
        summary.setText(pet.getBreed());
    }
}