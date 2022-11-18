package com.pucmm.articleviewer;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.provider.MediaStore;
import android.telephony.RadioAccessSpecifier;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pucmm.articleviewer.databinding.FragmentAddItemBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddItem extends Fragment {

    FragmentAddItemBinding binding;
    ArticlesAdapter adapter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private final int CAMERA_PERMISSION_CODE = 200;
    private final int GALLERY_PERMISSION_CODE = 201;
    private final int FOLDER_PERMISSION_CODE = 202;
    private int controller = 0;
    private FirebaseStorage storage;
    Database db;
    private StorageReference storageReference;
    ArticleViewModel articleViewModel;
    int positionImage;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddItemBinding.inflate(inflater,container,false);
        View viewAddItem = binding.getRoot();
        return viewAddItem;
    }

    public void onViewCreated(@NonNull View viewAddItem, Bundle savedInstanceState) {
        super.onViewCreated(viewAddItem, savedInstanceState);
        String nameImage = AddItemArgs.fromBundle(getArguments()).getImage();
        positionImage = AddItemArgs.fromBundle(getArguments()).getElement();

        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        db = Room.databaseBuilder(getContext(),
                Database.class,"articles.db").allowMainThreadQueries().build();

        Articles articles = db.articlesDao().findByImage(nameImage);

        if(TextUtils.isEmpty(nameImage)){
            binding.deleteBt.setVisibility(viewAddItem.GONE);
        }
        else{
            binding.deleteBt.setVisibility(viewAddItem.getVisibility());

            if (articles.getImage().equals(nameImage))
            {
                Toast.makeText(getContext(),"bien",Toast.LENGTH_SHORT).show();
                selectPicture(articles);
            }
        }


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if(controller==1){
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        binding.imageImageview.setImageBitmap(bitmap);
                    }
                    if (controller == 2){
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        //updatePicture (imageUri);
                        binding.imageImageview.setImageURI(imageUri);

                    }
                }

            }
        });

        binding.imageImageview.setOnClickListener(view ->{
            //new FragmentDialogBox().show(requireActivity().getSupportFragmentManager(), "fragmentDialog");
            String [] cameraOptions = viewAddItem.getResources().getStringArray(R.array.cameraOptions);
            AlertDialog.Builder build = new AlertDialog.Builder(getContext());
            build.setTitle("Choose your category picture")
                    .setItems(cameraOptions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(cameraOptions[which]) {
                                case "Take Photo":
                                    // code block
                                    if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) ==
                                            PackageManager.PERMISSION_DENIED ||
                                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                    PackageManager.PERMISSION_DENIED){

                                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                        //show popup to request permissions
                                        requestPermissions(permission, CAMERA_PERMISSION_CODE );

                                    }
                                    else{
                                        Toast.makeText(getContext(), "you choose : " + cameraOptions[which],Toast.LENGTH_SHORT).show();
                                        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        controller = 1;
                                        activityResultLauncher.launch(intentCamera);
                                        //startActivityForResult(intentCamera,CAMERA_REQ_CODE);
                                    }

                                    break;
                                case "Choose from Gallery":
                                    // code block
                                    if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                            PackageManager.PERMISSION_DENIED ){

                                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                        //show popup to request permissions
                                        requestPermissions(permission,GALLERY_PERMISSION_CODE);

                                    }
                                    else{
                                        Toast.makeText(getContext(), "you choose : " + cameraOptions[which],Toast.LENGTH_SHORT).show();
                                        Intent intentFolder = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        controller = 2;
                                        activityResultLauncher.launch(intentFolder);
                                        //startActivityForResult(intentCamera,CAMERA_REQ_CODE);
                                    }

                                    break;
                                case "Choose from Folder":
                                    // code block
                                    if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                            PackageManager.PERMISSION_DENIED ){

                                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                        //show popup to request permissions
                                        requestPermissions(permission,FOLDER_PERMISSION_CODE );

                                    }
                                    else{
                                        Toast.makeText(getContext(), "you choose : " + cameraOptions[which],Toast.LENGTH_SHORT).show();
                                        Intent intentFolder = new Intent(Intent.ACTION_GET_CONTENT);
                                        intentFolder.setType("image/*");
                                        controller = 2;
                                        activityResultLauncher.launch(intentFolder);
                                        //startActivityForResult(intentCamera,CAMERA_REQ_CODE);
                                    }

                                    break;
                                case "Cancel":
                                    // code block
                                    Toast.makeText(getContext(), "you choose : " + cameraOptions[which],Toast.LENGTH_SHORT).show();

                                    break;
                            }
                        }
                    })
                    .show();
        });

        binding.saveBt.setOnClickListener(view->{
            if(TextUtils.isEmpty(binding.nameEdt.getText()) || TextUtils.isEmpty(binding.descriptionEdt.getText())
                    || TextUtils.isEmpty(binding.priceEdt.getText())
                    ||binding.imageImageview.getDrawable() == null){
                if (TextUtils.isEmpty(binding.nameEdt.getText()))
                    binding.nameEdt.setError("Digite el nombre del articulo");
                if (TextUtils.isEmpty(binding.descriptionEdt.getText()))
                    binding.descriptionEdt.setError("Digite la descripcion");
                if (TextUtils.isEmpty(binding.priceEdt.getText()))
                    binding.priceEdt.setError("Digite el precio");
                if(binding.imageImageview.getDrawable() == null)
                    Toast.makeText(getContext(),"Seleccione una imagen",Toast.LENGTH_SHORT).show();
            }
            else{
                uploadPicture();

            }

        });
        binding.deleteBt.setOnClickListener(view->{
            Toast.makeText(getContext(),"no funciona",Toast.LENGTH_SHORT).show();
            if (articles.getImage().equals(nameImage) &&
                    articles.getName().equals(binding.nameEdt.getText().toString())
                    && articles.getDescription().equals(binding.descriptionEdt.getText().toString())
                    && articles.getPrice().equals(binding.priceEdt.getText().toString())){
                Toast.makeText(getContext(),"funciona",Toast.LENGTH_SHORT).show();
                deletePicture(articles);

            }

        });
        binding.clearBt.setOnClickListener(view->{
            binding.imageImageview.setImageDrawable(null);
            binding.nameEdt.setText(null);
            binding.descriptionEdt.setText(null);
            binding.priceEdt.setText(null);
            binding.deleteBt.setVisibility(viewAddItem.GONE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode){
            case CAMERA_PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    controller = 1;
                    activityResultLauncher.launch(intentCamera);
                    //startActivityForResult(intentCamera,CAMERA_REQ_CODE);
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(getContext(), "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case GALLERY_PERMISSION_CODE:{

                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    Intent intentFolder = new Intent(Intent.ACTION_PICK);
                    intentFolder.setType("image/*");
                    controller = 2;
                    activityResultLauncher.launch(intentFolder);

                }

                else {
                    //permission from popup was denied
                    Toast.makeText(getContext(), "Permission 9denied...", Toast.LENGTH_SHORT).show();
                }

            }
            case FOLDER_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    Intent intentFolder = new Intent(Intent.ACTION_GET_CONTENT);
                    intentFolder.setType("image/*");
                    controller = 2;
                    activityResultLauncher.launch(intentFolder);

                }
                else {
                    //permission from popup was denied
                    Toast.makeText(getContext(), "Permission 9denied...", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }

    public void uploadPicture(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = format.format(now);

        binding.imageImageview.setDrawingCacheEnabled(true);
        binding.imageImageview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.imageImageview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = storageReference.child("images/" + fileName);

        UploadTask uploadTask = storageRef.putBytes(data);
        Snackbar.make(binding.getRoot(),"here.",Snackbar.LENGTH_SHORT).show();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(),"Failed To Upload", Toast.LENGTH_SHORT);

                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Snackbar.make(binding.getRoot(),"Image Uploaded.",Snackbar.LENGTH_SHORT).show();

                String image = fileName;
                String name = binding.nameEdt.getText().toString();
                String description = binding.descriptionEdt.getText().toString();
                String price = binding.priceEdt.getText().toString();
                articleViewModel.insertArticles(new Articles(image,name,description,price));
                binding.imageImageview.setImageDrawable(null);
                binding.nameEdt.setText(null);
                binding.descriptionEdt.setText(null);
                binding.priceEdt.setText(null);

            }
        });
    }

    public void deletePicture(Articles imageReceived){
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

// Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("images/"+imageReceived.getImage());

// Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                String image = imageReceived.getImage();
                String name = binding.nameEdt.getText().toString();
                String description = binding.descriptionEdt.getText().toString();
                String price = binding.priceEdt.getText().toString();

                articleViewModel.deleteArticles(new Articles(image,name,description,price));
                binding.imageImageview.setImageDrawable(null);
                binding.nameEdt.setText(null);
                binding.descriptionEdt.setText(null);
                binding.priceEdt.setText(null);
                articleViewModel.deleteArticles(imageReceived);
                binding.deleteBt.setVisibility(View.GONE);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    void selectPicture (Articles articles){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ articles.getImage());
        try {
            File localfile = File.createTempFile("images","jpg");
            Toast.makeText(getContext(),articles.getImage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(),"Downloaded---process", Toast.LENGTH_SHORT).show();

            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            binding.imageImageview.setImageBitmap(bitmap);
                            binding.nameEdt.setText(articles.getName());
                            binding.descriptionEdt.setText(articles.getDescription());
                            binding.priceEdt.setText(articles.getPrice());
                            Toast.makeText(getContext(),articles.getName(), Toast.LENGTH_SHORT).show();

                            Toast.makeText(getContext(),articles.getImage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(),"Downloaded", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Failed To Download", Toast.LENGTH_SHORT).show();


                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void updatePicture (Uri image){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = format.format(now);

        StorageReference storageRef = storageReference.child("images/" + fileName);
        storageRef.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(binding.getRoot(),"Image Uploaded.",Snackbar.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Failed To Upload", Toast.LENGTH_SHORT);

                    }
                });

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}