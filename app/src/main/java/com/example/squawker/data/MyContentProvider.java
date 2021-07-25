package com.example.squawker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class MyContentProvider extends ContentProvider {

    private static  final int TABLE_ID= 100;
    private static  final int RAW_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MyDatabase myDatabase;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MyContract.AUTHORITY, MyContract.SquawkEntry.TABLE_NAME, TABLE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        myDatabase = new MyDatabase(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = myDatabase.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor reCursor;
        if (match == TABLE_ID) {
            reCursor = db.query(MyContract.SquawkEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        reCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return reCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnedUri;
        final SQLiteDatabase db = myDatabase.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        if (match == TABLE_ID) {
            long id = db.insert(MyContract.SquawkEntry.TABLE_NAME, null, values);
            if (id > 0) {
                //success
                returnedUri = ContentUris.withAppendedId(MyContract.SquawkEntry.CONTENT_URI, id);
            } else {
                throw new SQLException("Failed to inset row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the Content Resolver that a change has been occured :)
        getContext().getContentResolver().notifyChange(uri, null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
