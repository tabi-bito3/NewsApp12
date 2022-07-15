package com.example.newsapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NewPostActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 200;
    ImageView imgBtn;
    Button postBtn;
    String image_uri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        imgBtn = (ImageView) findViewById(R.id.new_post_image);
        postBtn = (Button) findViewById(R.id.post_new_btn);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    image_uri = uri.toString();
                    try {
                        Bitmap showBitmap = getBitmapFromUri(uri);
                        saveBitmapToCache(showBitmap);
                        imgBtn.setImageBitmap(showBitmap);
                    } catch (IOException e){
                        Log.e("tag", e.toString());
                    }
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        if (!image_uri.equals("")){
            imgBtn.setImageBitmap(getBitmapFromCache());
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