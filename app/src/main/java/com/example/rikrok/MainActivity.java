package com.example.rikrok;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private GestureDetector detector;
    private VideoView video;

    private ImageButton buttonLike;
    private ImageButton buttonShare;
    private int videoNum = 1;

    private SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private class VideoListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            view.performClick();
            detector.onTouchEvent(event);
            return true;
        }
    }

    //TODO
    private class LikeListener implements View.OnClickListener {
        public void onClick(View view) {
            ArrayList<String> likedVideos = new ArrayList<>();
//            DocumentReference docRef = db.collection("LikedVideos").document("");
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        } else {
//                            Log.d(TAG, "No such document");
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });

            likedVideos.add("1.mp4");
            Map<String, Object> databaseItem = new HashMap<>();
            databaseItem.put("LikedVideos", likedVideos);
            db.collection("LikedVideos")
                    .add(databaseItem)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });
        }
    }

    void createNewVideo() {
        Uri uri = Uri.parse(String.format("https://storage.googleapis.com/rikrok-24942.appspot.com/Videos/%o.mp4", videoNum));
        videoNum++;
        editor.putInt("video_num", videoNum);
        editor.apply();
        video.setVideoURI(uri);
        video.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        prefs = getSharedPreferences("label", 0);
        editor = prefs.edit();
        videoNum = prefs.getInt("video_num", 1);

        video = findViewById(R.id.video_view);
        video.setOnTouchListener(new VideoListener());
        createNewVideo();

        detector = new GestureDetector(this, new VideoGestureListener());

//        buttonLike = findViewById(R.id.button_like);
//        buttonLike.setOnClickListener(new LikeListener());
//
//        buttonShare = findViewById(R.id.button_share);
//        buttonShare.setOnClickListener(new ShareListener());
//        buttonSettings = findViewById(R.id.button_settings);
//        buttonSettings.setOnClickListener(new SettingsListener());
    }

    class VideoGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("TAG", "singleTap: ");
            if (video.isPlaying()) {
                video.stopPlayback();
            }
            else {
                video.start();
            }
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "hi");
            createNewVideo();
            return true;
        }
    }
}