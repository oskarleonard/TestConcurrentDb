package com.fransson.leonard.oskar.testconcurrentdb.database;

import android.database.sqlite.SQLiteDatabase;

public interface QueryExecutor {

    public void run(SQLiteDatabase database);
}
