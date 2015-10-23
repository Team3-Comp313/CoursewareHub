package com.team03.coursewarehub;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SearchSecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_second);

        final BootstrapButton btnYoutube = (BootstrapButton) findViewById(R.id.btnYoutube);
        final BootstrapButton btnAssignments = (BootstrapButton) findViewById(R.id.btnAssignments);
        final BootstrapButton btnHandouts = (BootstrapButton) findViewById(R.id.btnHandouts);
        final BootstrapButton btnExamples = (BootstrapButton) findViewById(R.id.btnExamples);

        // Connection to firebase
        Firebase.setAndroidContext(this);

        String baseurl = "https://team03-coursewarehub.firebaseio.com/";

        // move to next activity
        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgHeader);
        Firebase ref = new Firebase(baseurl + courseTitle);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().toString() == "Videos") {
                    final String video = dataSnapshot.getValue().toString();
                    if (video.equals("")) {
                        btnYoutube.setVisibility(View.GONE);
                    } else {
                        //move to next activity for list of all videos
                        btnYoutube.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getApplicationContext(), SearchSecondVideoActivity.class);
                                i.putExtra("courseTitle", courseTitle);
                                startActivity(i);
                            }
                        });
                    }
                }
                if (dataSnapshot.getKey().toString() == "Image") {
                    String tempHeaderImage = (String) dataSnapshot.getValue();
                    // Assigning Image to Image view
                    UrlImageViewHelper.setUrlDrawable(imgHeader, tempHeaderImage);
                }
                if (dataSnapshot.getKey().toString() == "Assignments") {
                    final String assignments = dataSnapshot.getValue().toString();
                    if (assignments.equals("")) {
                        btnAssignments.setVisibility(View.GONE);
                    } else {
                        //move to next activity for list of all videos
                    }
                }
                if (dataSnapshot.getKey().toString() == "Handouts") {
                    final String handouts = dataSnapshot.getValue().toString();
                    if (handouts.equals("")) {
                        btnHandouts.setVisibility(View.GONE);
                    } else {
                        //move to next activity for list of all videos
                    }
                }
                if (dataSnapshot.getKey().toString() == "Examples") {
                    final String examples = dataSnapshot.getValue().toString();
                    if (examples.equals("")) {
                        btnExamples.setVisibility(View.GONE);
                    } else {
                        //move to next activity for list of all videos
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
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent activityIntent = new Intent(SearchSecondActivity.this,
                SearchActivity.class);
        SearchSecondActivity.this.startActivity(activityIntent);
        finish();
    }
}