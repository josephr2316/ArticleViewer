package com.pucmm.articleviewer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    private ArticlesRepository articlesRepository;
    private LiveData<List<Articles>> articlesList;

    public ArticleViewModel(@NonNull Application application) {
        super(application);

        articlesRepository = new ArticlesRepository(application);
        articlesList =articlesRepository.getAllArticles();

    }

    public LiveData<List<Articles>> getAllArticles(){
        return articlesList;
    }


    public void insertArticles(Articles articles){
        articlesRepository.insertArticles(articles);
    }

    public void updateArticles(Articles articles){
        articlesRepository.updateArticles(articles);
    }

    public void deleteArticles(Articles articles){
        articlesRepository.deleteArticles(articles);
    }


}
