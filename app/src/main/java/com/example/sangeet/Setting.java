package com.example.sangeet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class Setting extends AppCompatActivity {
    Switch Facerecognition,Voicecommand;
    Button savesetting;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Facerecognition=findViewById(R.id.faceRecognition);
        Voicecommand=findViewById(R.id.VoiceCommand);
        savesetting=findViewById(R.id.SaveSetting);
        progressBar=findViewById(R.id.progressBar3);



        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        Voicecommand.setChecked(sharedPreferences.getBoolean("value",true));

        Voicecommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Voicecommand.isChecked()){
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",true);
                    editor.apply();
                    Voicecommand.setChecked(true);

                    Dexter.withContext(Setting.this)
                            .withPermission(Manifest.permission.RECORD_AUDIO)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            })
                            .check();
                }
                else{
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    Voicecommand.setChecked(false);
                }
            }
        });

    }

}