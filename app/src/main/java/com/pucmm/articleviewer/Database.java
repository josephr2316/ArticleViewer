package com.pucmm.articleviewer;

import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Articles.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract ArticlesDao articlesDao();
    public static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static Database getDatabase (Context context){
        if (INSTANCE == null){
            synchronized (Database.class){
                INSTANCE = Room.databaseBuilder(context, Database.class,"articles.db")
                        .build();
            }
        }
        return INSTANCE;
    }


}
