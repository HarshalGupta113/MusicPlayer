package com.example.sangeet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.view.PreviewView;

public final class Preview extends AppCompatActivity {
    private PreviewView previewView;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        setContentView(R.layout.activity_playsongs);
    }
}
