package com.example.squawker.data;

import android.net.Uri;
import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;

public class MyContract {


    public static final String AUTHORITY = "com.example.squawker.data";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final class SquawkEntry implements BaseColumns {

        public static final String TABLE_NAME = "squawk";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(TABLE_NAME).build();


        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_AUTHOR_KEY = "authorKey";

        public static final String COLUMN_MESSAGE = "message";

        public static final String COLUMN_DATE = "date";


    }
}
