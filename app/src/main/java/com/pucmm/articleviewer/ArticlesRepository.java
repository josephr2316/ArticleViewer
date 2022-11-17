package com.pucmm.articleviewer;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ArticlesRepository {

    final Database database;
    final  ArticlesDao articlesDao;
    final LiveData<List<Articles>> articlesList;

    public ArticlesRepository (Application application) {
        database = Database.getDatabase(application);
        articlesDao = database.articlesDao();
        articlesList = articlesDao.getAllArticles();
    }

    public LiveData<List<Articles>> getAllArticles(){
        return articlesList;
    }

    public void insertArticles(final Articles articles){
       Database.databaseWriteExecutor.execute(() ->
               articlesDao.insertArticles(articles));
    }

    public void updateArticles(final Articles articles){
        Database.databaseWriteExecutor.execute(() ->
                articlesDao.updateArticles(articles));

    }

    public void deleteArticles(final Articles articles){
        Database.databaseWriteExecutor.execute(() ->
                articlesDao.deleteArticles(articles));
    }

}
