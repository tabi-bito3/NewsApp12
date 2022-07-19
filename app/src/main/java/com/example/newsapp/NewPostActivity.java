package com.example.newsapp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 200;
    ProgressBar progressPost;
    ImageView imgView;
    ImageButton imgBtn;
    EditText postTitle;
    EditText postDesc;
    Button postBtn;
    // String image_uri = "";
    Uri postImageUri = null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        imgView = findViewById(R.id.new_post_image);
        imgBtn =findViewById(R.id.img_edit);
        postTitle = findViewById(R.id.post_title_text);
        postDesc = findViewById(R.id.post_desc_text);
        postBtn = findViewById(R.id.post_new_btn);
        progressPost = findViewById(R.id.progressPost);

        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

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

                    StorageReference filePath = storageReference.child("post_images/"+ UUID.randomUUID().toString());
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), postImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baOs = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baOs);
                    byte[] data = baOs.toByteArray();
                    filePath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> fireBaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            fireBaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();


                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("thumb_img", downloadUrl);
                                    postMap.put("title", title);
                                    postMap.put("desc", desc);
                                    postMap.put("user_id", current_user_id);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());


                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                                Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(NewPostActivity.this, "Post upload unsuccessful", Toast.LENGTH_LONG).show();
                                            }
                                            progressPost.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressPost.setVisibility(View.INVISIBLE);
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
                }
                else{
                    Toast.makeText(NewPostActivity.this, "Post upload unsuccessful", Toast.LENGTH_LONG).show();
                    progressPost.setVisibility(View.INVISIBLE);
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
        if (postImageUri != null){
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
        showBitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
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