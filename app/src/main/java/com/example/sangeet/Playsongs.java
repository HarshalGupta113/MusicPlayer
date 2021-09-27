package com.example.sangeet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;


public class Playsongs extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView play, previous, next,mic,displayimage;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    Switch VoiceCommand;
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsongs);
        textView = findViewById(R.id.textView2);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        mic = findViewById(R.id.mic);
        VoiceCommand = findViewById(R.id.VoiceCommand);
        displayimage = findViewById(R.id.DisplayImage);
        videoView=findViewById(R.id.videoView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pervioussongs();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextsongs();
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Onspeak();
            }
        });


        displayimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withContext(Playsongs.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse)
                            {
                                Facerecoginzer();
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
        });
    }

    
    public void Facerecoginzer(){
        Intent intent1=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1,10);
        Intent intent2=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent2,100);
    }

    // Code For Voice Command
    public void Onspeak(){
        Intent intentrecognizer=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentrecognizer.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Commands");
        startActivityForResult(intentrecognizer,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            ArrayList<String> commands=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (commands.get(0).toString().equals("play")){
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
            if (commands.get(0).toString().equals("stop")){
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            }
            if (commands.get(0).toString().equals("next")){
                nextsongs();
            }
            if (commands.get(0).toString().equals("previous")){
              pervioussongs();
            }
        }
        if (requestCode==10){
            Bitmap cameracapture= (Bitmap) data.getExtras().get("data");
            displayimage.setImageBitmap(cameracapture);
        }
        if (requestCode==100){
            Bitmap videocapture= (Bitmap) data.getExtras().get("data");
            videoView.setVideoPath(String.valueOf(videocapture));
        }
    }


    public void nextsongs(){
        mediaPlayer.stop();
        mediaPlayer.release();
        if(position!=songs.size()-1){
            position = position + 1;
        }
        else{
            position = 0;
        }
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent = songs.get(position).getName().toString();
        textView.setText(textContent);
    }

    public void pervioussongs(){
        mediaPlayer.stop();
        mediaPlayer.release();
        if(position!=0){
            position = position - 1;
        }
        else{
            position = songs.size() - 1;
        }
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent = songs.get(position).getName().toString();
        textView.setText(textContent);
    }

}