package com.firstapplication.aditya.theftdetection;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
public class Video extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

        static private final String DEVELOPER_KEY = "add your own key here!";
        String username = "", videoID = "";

       @Override
       protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              setContentView(R.layout.video);

              Intent intent = getIntent();

              username = intent.getStringExtra("Username");
              videoID = intent.getStringExtra("Video ID");

              YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
              youTubeView.initialize(DEVELOPER_KEY, this);
          }
         

         @Override
         public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
                Toast.makeText(this, "Error playing video", Toast.LENGTH_LONG).show();
        }
       

       @Override
       public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
              player.loadVideo(videoID);
       }
}