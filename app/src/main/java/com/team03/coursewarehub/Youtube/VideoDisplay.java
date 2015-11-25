package com.team03.coursewarehub.Youtube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.regex.*;

import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.team03.coursewarehub.R;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
public class VideoDisplay extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 10;
    public static final String API_KEY = "AIzaSyC29Cn2stiksjnP33HeegXMNCCAoMRzgnc";
    String videoId;
    private RatingBar ratingBar;
    private TextView txtRatingValue;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        // Connection to firebase
        Firebase.setAndroidContext(this);

        String baseurl = "https://team03-coursewarehub.firebaseio.com/";

        // move to next activity
        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgHeader);
        Firebase ref = new Firebase(baseurl + courseTitle);

        //get url from previous activity
        final String url = intent.getStringExtra("videoUrl");
        //videoId = extractYTId(url);
        videoId = url;

        //Get video name from previous activity
        final String name = intent.getStringExtra("videoName");

        //find textview
        final TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(name);


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().toString() == "Image") {
                    String tempHeaderImage = (String) dataSnapshot.getValue();
                    // Assigning Image to Image view
                    UrlImageViewHelper.setUrlDrawable(imgHeader, tempHeaderImage);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(API_KEY, this);

        addListenerOnRatingBar();
        addListenerOnButton();
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));

            }
        });
    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(VideoDisplay.this,
                        String.valueOf(ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();

            }

        });

    }

//    public String extractYTId(String ytUrl) {
//        //String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
//        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
//
//        Pattern compiledPattern = Pattern.compile(pattern);
//        Matcher matcher = compiledPattern.matcher(ytUrl);
//
//        if(matcher.find()){
//            return matcher.group();
//        }
//        else
//            return "";
//    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("YouTube Error (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(videoId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    private class FullScreenListener implements YouTubePlayer.OnFullscreenListener{

        @Override
        public void onFullscreen(boolean isFullscreen) {
            //Called when fullscreen mode changes.

        }

    }

    private class PlaybackListener implements YouTubePlayer.PlaybackEventListener{

        @Override
        public void onBuffering(boolean isBuffering) {
            // Called when buffering starts or ends.

        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to pause() or user action.

        }

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to play() or user action.

        }

        @Override
        public void onSeekTo(int newPositionMillis) {
            // Called when a jump in playback position occurs,
            //either due to the user scrubbing or a seek method being called

        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.

        }

    }

    private class PlayerStateListener implements YouTubePlayer.PlayerStateChangeListener{

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.

        }

        @Override
        public void onError(ErrorReason reason) {
            // Called when an error occurs.

        }

        @Override
        public void onLoaded(String arg0) {
            // Called when a video has finished loading.

        }

        @Override
        public void onLoading() {
            // Called when the player begins loading a video and is not ready to accept commands affecting playback

        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.

        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.

        }

    }

    private class PlayListListener implements YouTubePlayer.PlaylistEventListener{

        @Override
        public void onNext() {
            // Called before the player starts loading the next video in the playlist.

        }

        @Override
        public void onPlaylistEnded() {
            // Called when the last video in the playlist has ended.

        }

        @Override
        public void onPrevious() {
            // Called before the player starts loading the previous video in the playlist.

        }

    }
}
