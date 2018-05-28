package com.example.firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    Button firebaseButton, selectFileButton, uploadButton;
    ImageView firebaseImage;
    private Boolean imageSelected = false;
    StorageReference imageRef;
    Uri uri;
    FirebaseStorage storageRef;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());
        storageRef = FirebaseStorage.getInstance("gs://uploada-fca84.appspot.com/");
        imageRef = storageRef.getReference();

        firebaseButton = findViewById(R.id.btn_firebase);
        firebaseImage = findViewById(R.id.image_from_firebase);
        selectFileButton = findViewById(R.id.btn_select_file);
        uploadButton = findViewById(R.id.btn_upload);
        constraintLayout = findViewById(R.id.layout_container);

        firebaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupFirebase();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectFile();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null && imageSelected) {
                    uploadFile(uri);
                } else {
                    Log.e("URI", "in upload is null");
                }
            }
        });
    }

    private void setupFirebase() {

        StorageReference pictureRef = imageRef.child("default.png");
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(pictureRef)
                .into(firebaseImage);
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*");
        startActivityForResult(intent, 100);
    }

    private void uploadFile(Uri uri) {
        if (uri == null) {
            Log.e("upload file", uri.toString());
        }

        if (imageSelected) {
            StorageReference uploadRef = imageRef.child(uri.getLastPathSegment());
            UploadTask uploadTask = uploadRef.putFile(uri);
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Snackbar.make(constraintLayout, "Upload successful", Snackbar.LENGTH_SHORT)
                            .show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Upload", "Fail");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                Log.e("data", uri.getPathSegments().toString());
                firebaseImage.setImageURI(uri);
                if (uri != null) {
                    imageSelected = true;
                }
            }
        }
    }
}
