package com.example.newsapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NewPostActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 200;
    ProgressBar progressPost;
    ImageView imgView;
    ImageButton imgBtn;
    EditText postTitle;
    EditText postDesc;
    Button postBtn;
    String image_uri = "";
    Uri postImageUri = null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        imgView = (ImageView) findViewById(R.id.new_post_image);
        imgBtn = (ImageButton) findViewById(R.id.img_edit);
        postTitle = (EditText) findViewById(R.id.post_title_text);
        postDesc = (EditText) findViewById(R.id.post_desc_text);
        postBtn = (Button) findViewById(R.id.post_new_btn);
        progressPost = (ProgressBar) findViewById(R.id.progressPost);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = postDesc.getText().toString();
                String title = postTitle.getText().toString();

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && postImageUri != null){
                    progressPost.setVisibility(View.VISIBLE);

                    String randomName = FieldValue.serverTimestamp().toString();

                    StorageReference filePath = storageReference.child("post_images").child(randomName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                String downloadUrl = task.getResult().getUploadSessionUri().toString();

                            }
                        }
                    });
                }
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    postImageUri = uri;
                    try {
                        Bitmap showBitmap = getBitmapFromUri(uri);
                        saveBitmapToCache(showBitmap);
                        imgView.setImageBitmap(showBitmap);
                    } catch (IOException e){
                        Log.e("tag", e.toString());
                    }
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        if (postImageUri == null){
            imgView.setImageBitmap(getBitmapFromCache());
        }
    }

    private Bitmap getBitmapFromCache() {
        File cacheFile = new File(getApplicationContext().getCacheDir(), "final_image.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return myBitmap;
    }

    private void saveBitmapToCache(Bitmap showBitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        showBitmap.compress(Bitmap.CompressFormat.JPEG, (int)100, out);
        out.flush();
        out.close();
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}