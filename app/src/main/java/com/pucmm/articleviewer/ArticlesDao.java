package com.pucmm.articleviewer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface ArticlesDao {

    @Insert
    void insertArticles(Articles articles);

    @Update
    void updateArticles(Articles articles);

    @Delete
    void deleteArticles(Articles articles);

    @Query("SELECT * from articles_table")
    LiveData<List<Articles>> getAllArticles();

    @Query("SELECT * FROM articles_table WHERE image = :image")
    Articles findByImage(String image);






    //@Query("SELECT * from articles_table ORDER BY id ASC")
    //LiveData<List<Articles>> getAllArticles();
}
