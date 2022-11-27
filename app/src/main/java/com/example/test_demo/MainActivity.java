package com.example.test_demo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView ivPictureUpload,ivPictureUpload1;
    private Button btnUpload;
    private TextView txtLink;
    private Uri imagePath;
    private ArrayList<Uri> list;
    int PICK_IMAGE_MULTIPLE = 1;
    private HashMap config = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPictureUpload = findViewById(R.id.ivPictureUpload);
        ivPictureUpload1= findViewById(R.id.ivPictureUpload1);
        btnUpload = findViewById(R.id.btnUpload);
        txtLink = findViewById(R.id.txtLink);


        ivPictureUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                // setting type to select to be image
                intent.setType("image/*");

                // allowing multiple image to be selected
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        ivPictureUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy nội dung", txtLink.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Đã copy nội dung", Toast.LENGTH_SHORT).show();
            }
        });

        configCloudinary();
    }

    private void upload() {
        MediaManager.get().upload(imagePath).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("CHECK", "onStart");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d("CHECK", "onProgress");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                Log.d("CHECK", "onSuccess");
                txtLink.setText(resultData.get("url").toString());
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.d("CHECK", "onError: " + error);
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.d("CHECK", "onReschedule: " + error);
            }
        }).dispatch();
    }

    private void chooseImage() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");// if you want to you can use pdf/gif/video
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    list.add(imageurl);
                }
                // setting 1st selected image into image switcher
                ivPictureUpload.setImageURI(list.get(0));
                ivPictureUpload1.setImageURI(list.get(1));
            } else {
                Uri imageurl = data.getData();
                list.add(imageurl);
                ivPictureUpload.setImageURI(list.get(0));
            }
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                imagePath = data.getData();
                Glide.with(MainActivity.this).load(imagePath).into(ivPictureUpload);
                Glide.with(MainActivity.this).load(imagePath).into(ivPictureUpload1);
            }
        }
    });

    private void configCloudinary() {
        config.put("cloud_name", "mrsmci");
        config.put("api_key", "349364544734878");
        config.put("api_secret", "3jjrlkK2rWHzy71859iaJ9M1u-4");
        MediaManager.init(this, config);
    }
}
