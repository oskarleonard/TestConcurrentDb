package com.fransson.leonard.oskar.testconcurrentdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fransson.leonard.oskar.testconcurrentdb.database.DatabaseHelper;
import com.fransson.leonard.oskar.testconcurrentdb.database.DatabaseManager;
import com.fransson.leonard.oskar.testconcurrentdb.database.QueryExecutor;
import com.fransson.leonard.oskar.testconcurrentdb.database.User;
import com.fransson.leonard.oskar.testconcurrentdb.database.UserDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager.initializeInstance(new DatabaseHelper(getApplicationContext()));



        Button btnConcurrent = (Button)findViewById(R.id.button);
        btnConcurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getInstance().executeQueryTask(new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        UserDAO dao = new UserDAO(database, getApplicationContext());
                        dao.insert(generateDummyUserList(300));
                    }
                });
                DatabaseManager.getInstance().executeQueryTask(new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        UserDAO dao = new UserDAO(database, getApplicationContext());
                        dao.insert(generateDummyUserList(200)); //5000
                    }
                });
            }
        });

        Button btnNormal = (Button)findViewById(R.id.button2);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<User> userList = generateDummyUserList(300);
                        for (User user : userList) {
                            String[] bindArgs = {
                                    user.getName(),
                                    String.valueOf(user.getAge())
                            };
                            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                            SQLiteDatabase database= helper.getWritableDatabase();
                            database.execSQL(getApplicationContext().getString(R.string.insert_user), bindArgs);
                            database.close();
                        }

                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<User> userList = generateDummyUserList(200);
                        for (User user : userList) {
                            String[] bindArgs = {
                                    user.getName(),
                                    String.valueOf(user.getAge())
                            };
                            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                            SQLiteDatabase database= helper.getWritableDatabase();
                            database.execSQL(getApplicationContext().getString(R.string.insert_user), bindArgs);
                            database.close();
                        }

                    }
                }).start();
            }
        });
        Button button3 = (Button)findViewById(R.id.btnListSize);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        UserDAO dao = new UserDAO(new DatabaseHelper(getApplicationContext()).getWritableDatabase(), getApplicationContext());
                        List<User> listFromDB = dao.selectAll();
                        Log.v("SEBBE IT", "listFromDB.size()  " + listFromDB.size());
                    }
                });
            }
        });
        Button button4 = (Button)findViewById(R.id.btnReset);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
                    @Override
                    public void run(SQLiteDatabase database) {
                        Log.v("Thread UI", "is it?  " + (Looper.myLooper() == Looper.getMainLooper()));
                        new UserDAO(database, getApplicationContext()).deleteAll();
                    }
                });
            }
        });
    }
    public void testInsertUserList() {
        DatabaseManager.getInstance().executeQueryTask(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                UserDAO dao = new UserDAO(database, getApplicationContext());
                dao.insert(generateDummyUserList(300));
            }
        });
        DatabaseManager.getInstance().executeQueryTask(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                UserDAO dao = new UserDAO(database, getApplicationContext());
                dao.insert(generateDummyUserList(200));
            }
        });
    }
    private List<User> generateDummyUserList(int itemsCount) {
        List<User> userList = new ArrayList<User>();
        for (int i = 0; i < itemsCount; i++) {
            User user = new User();
            user.setAge(i);
            user.setName("Jon Doe");
            userList.add(user);
        }
        return userList;
    }


    private void normalHelper(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = generateDummyUserList(300);
                for (User user : userList) {
                    String[] bindArgs = {
                            user.getName(),
                            String.valueOf(user.getAge())
                    };
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    SQLiteDatabase database= helper.getWritableDatabase();
                    database.execSQL(getApplicationContext().getString(R.string.insert_user), bindArgs);
                    database.close();
                }

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = generateDummyUserList(200);
                for (User user : userList) {
                    String[] bindArgs = {
                            user.getName(),
                            String.valueOf(user.getAge())
                    };
                    DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                    SQLiteDatabase database= helper.getWritableDatabase();
                    database.execSQL(getApplicationContext().getString(R.string.insert_user), bindArgs);
                    database.close();
                }

            }
        }).start();

    }

}
