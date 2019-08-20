package com.example.android.petsapp.exception;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class InvalidArgumentException extends IllegalArgumentException {

    //FIXME
    public InvalidArgumentException(@NonNull final Context context, @StringRes final int resId, Object ... args){
        super(context.getString(resId, args));
    }

    public InvalidArgumentException(@NonNull final Resources resources, @StringRes final int resId, Object ... args){
        super(resources.getString(resId, args));
    }

    public InvalidArgumentException(@NonNull final String message, Object ... args){
        super(String.format(message,args));
    }

}
