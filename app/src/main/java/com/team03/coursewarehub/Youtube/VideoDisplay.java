package com.team03.coursewarehub.Youtube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.team03.coursewarehub.R;

public class VideoDisplay extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 10;
    public static final String API_KEY = "AIzaSyC29Cn2stiksjnP33HeegXMNCCAoMRzgnc";
    String videoId;
    List<String> CommentList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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
        final Firebase ref = new Firebase(baseurl + courseTitle);

        //get url from previous activity
        final String url = intent.getStringExtra("videoUrl");
        //videoId = extractYTId(url);
        videoId = url;

        //Get video name from previous activity
        final String name = intent.getStringExtra("videoName");

        //find textview
        final TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText(name);

        //Get the selected position from the previous activity
        final int iPosition = intent.getIntExtra("Position", -1);

        BootstrapButton bbp = (BootstrapButton) findViewById(R.id.btnPost);
        BootstrapButton bbc = (BootstrapButton) findViewById(R.id.btnCancel);
        final BootstrapEditText betComment = (BootstrapEditText) findViewById(R.id.commentEditText);
        final BootstrapEditText betUser = (BootstrapEditText) findViewById(R.id.usernameEditText);
        final BootstrapEditText betEmail = (BootstrapEditText) findViewById(R.id.emailEditText);


        bbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //input validation
                if(!CheckValidation(betUser.getText().toString(), betComment.getText().toString(), betEmail.getText().toString()))
                {
                    return; // validation was wrong, exit this onClick method
                }

                //prepare data to set in Firebase
                Comments comment = new Comments (betUser.getText().toString(), betComment.getText().toString(), betEmail.getText().toString());

                //get Firebase reference, then set value to Firebase
                Firebase refFB = ref.child("Videos").child(String.valueOf(iPosition)).child("Comment");
                refFB.push().setValue(comment);

                // Clear the text fields
                betComment.setText("");
                betUser.setText("");
                betEmail.setText("");

                //refresh this page
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        bbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                betComment.setText("");
                betUser.setText("");
                betEmail.setText("");
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().toString() == "Image") {
                    String tempHeaderImage = (String) dataSnapshot.getValue();
                    // Assigning Image to Image view
                    UrlImageViewHelper.setUrlDrawable(imgHeader, tempHeaderImage);
                }
                if (dataSnapshot.getKey().toString() == "Videos") {
                    // display list to listview.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (iPosition == Integer.parseInt(snapshot.getKey().toString())) {
                            //Comment shows only selected the video from the previous activity
                            if (snapshot.child("Comment").exists()) {
                                // to avoid reset when "Comment" does not exist in layers
                                List<Comments> com = new ArrayList<Comments>();
                                for (DataSnapshot ds : snapshot.child("Comment").getChildren()) {
                                // this loop gets from the oldest comment to the latest comment
                                        com.add(ds.getValue(Comments.class));
                                }
                                for(long i = (snapshot.child("Comment").getChildrenCount() - 1) ; i >= 0 ; i--){
                                //  keep comments in ArrayList, then display reverse order
                                    String strText = com.get((int)i).getText();
                                    String strUser = com.get((int)i).getName();
                                    String strComment = strText + " by " + strUser;
                                    CommentList.add(strComment);
                                }
                                ListView lv = (ListView) findViewById(R.id.listViewComment);
                                lv.setAdapter(adapter);
                                setListViewHeightBasedOnItems(lv);
                            }
                        }
                    }
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

        ListView lv = (ListView) findViewById(R.id.listViewComment);
        adapter = new ArrayAdapter<String>(this, R.layout.main_search_list_item, R.id.product_name, CommentList);
        lv.setAdapter(adapter);
        setListViewHeightBasedOnItems(lv);
    }

    public boolean CheckValidation(String strName, String strText, String strEmail)
    {
        if (strText.trim().equals(""))
        {
            Toast.makeText(this, "Input your comment", Toast.LENGTH_LONG).show();
            return false;
        }
        if (strName.trim().equals(""))
        {
            Toast.makeText(this, "Input your name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (strEmail.trim().equals(""))
        {
            Toast.makeText(this, "Input your email address", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!strEmail.matches(email_pattern))
        {
            Toast.makeText(this, "Email format is wrong", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                //item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

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

    public static class Comments {
        String name;
        String text;
        String email;

        public Comments(){
        }

        public Comments(String name, String message, String email){
            this.name = name;
            this.text = message;
            this.email = email;
        }

        public String getName(){
            return name;
        }
        public String getText(){
            return text;
        }
        public String getEmail(){
            return email;
        }
    }
}
