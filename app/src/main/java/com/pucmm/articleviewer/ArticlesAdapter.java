package com.pucmm.articleviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticlesViewHolder> {

    private List<Articles> articlesList;
    private Context context;

    public ArticlesAdapter() {
    }
    public void setList(List<Articles> articlesList){
        this.articlesList = articlesList;
        notifyDataSetChanged();
    }
    public void deleteElement(int position){
        this.articlesList.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position) {
        Articles articles = articlesList.get(position);

        String image = articles.getImage();
        String name = articles.getName();
        String description = articles.getDescription();
        String price = articles.getPrice();
        downloadImage(image,holder.image);
        //holder.image.setImageBitmap();
        holder.name.setText(name);
        holder.description.setText(description);
        holder.price.setText(price);



        //FirebaseStorage mStorage = FirebaseStorage.getInstance();
       //StorageReference storageRef = mStorage.child(imgPath);

//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ image + "jpg");
//        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Glide.with(context)
//                        .load(storageReference)
//                        .into(holder.image);
//            }
//        });



        holder.edit.setOnClickListener(view-> {

//            AppCompatActivity activity = (AppCompatActivity ) view.getContext();
//            AddItem addItem = new AddItem();
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment,addItem)
//                    .addToBackStack(null)
//                    .commit();

            NavDirections navDirections = ItemDirections.actionItemToAddItem(image,position);
            NavController   navController = Navigation.findNavController(view);
            navController.navigate(navDirections);
//            NavHostFragment.findNavController()
//                    .navigate(R.id.action_item_to_addItem);
        });

        holder.addToCart.setOnClickListener(view->{
            Toast.makeText(view.getContext(),"Coming Soon",Toast.LENGTH_SHORT).show();
        });
        //
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    public static class ArticlesViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private EditText name;
        private EditText description;
        private EditText price;
        private Button addToCart;
        private Button edit;

        public ArticlesViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_cardview);
            name = itemView.findViewById(R.id.name_cardview);
            description = itemView.findViewById(R.id.description_cardview);
            price = itemView.findViewById(R.id.price_cardview);
            addToCart = itemView.findViewById(R.id.add_to_cart_bt);
            edit = itemView.findViewById(R.id.edit_bt);




        }
    }
    private void downloadImage(String image, ImageView imageView){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ image);
        try {
            File localfile = File.createTempFile("images","jpg");
            Toast.makeText(context.getApplicationContext(),image, Toast.LENGTH_SHORT).show();
            Toast.makeText(context,"Downloaded---process", Toast.LENGTH_SHORT).show();

            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                           Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                           imageView.setImageBitmap(bitmap);
                            Toast.makeText(context,"Downloaded", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Failed To Download", Toast.LENGTH_SHORT).show();


                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



