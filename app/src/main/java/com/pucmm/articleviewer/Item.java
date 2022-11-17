package com.pucmm.articleviewer;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pucmm.articleviewer.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

public class Item extends Fragment {

    FragmentItemBinding binding;
    static List<Articles> list = new ArrayList<>();
    ArticleViewModel articleViewModel;
    ArticlesAdapter articlesAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         binding = binding.inflate(inflater, container, false);
        View viewItem = binding.getRoot();
        return viewItem;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        articlesAdapter = new ArticlesAdapter();
        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        binding.addB.setOnClickListener(viewButton ->{

            NavDirections navDirections = ItemDirections.actionItemToAddItem(null);
            NavHostFragment.findNavController(Item.this)
                    .navigate(navDirections);
        });
        int spanCount = 1;
        binding.recycleview.setHasFixedSize(true);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2;
        }
        //recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.recycleview.setLayoutManager(new GridLayoutManager(view.getContext(), spanCount));
        articleViewModel.getAllArticles().observe(getViewLifecycleOwner(), articles ->  {
            articlesAdapter.setList(articles);
            binding.recycleview.setAdapter(articlesAdapter);
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}