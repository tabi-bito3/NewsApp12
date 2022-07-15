package com.example.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CropActivity extends AppCompatActivity {
    CropUtils.CropImageView imageview1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Button button1 = findViewById(R.id.done_button);
        imageview1 = findViewById(R.id.cropimageview);

        imageview1.setImageBitmap(getBitmapFromCache());
        imageview1.setFixedAspectRatio(true);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = imageview1.getCroppedImage();
                try {
                    saveBitmapToCache(bitmap);
                } catch (IOException e) {
                    Log.e("tag", e.toString());
                }
                finish();
            }
        });
    }

    public void saveBitmapToCache(Bitmap bitmap) throws IOException {
        String filename = "final_image.jpg";
        File cacheFile = new File(getApplicationContext().getCacheDir(), filename);
        OutputStream out = new FileOutputStream(cacheFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int)100, out);
        out.flush();
        out.close();
    }

    public Bitmap getBitmapFromCache(){
        File cacheFile = new File(getApplicationContext().getCacheDir(), "final_image.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        return myBitmap;
    }

}