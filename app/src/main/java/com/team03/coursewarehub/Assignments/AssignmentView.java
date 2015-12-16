package com.team03.coursewarehub.Assignments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.team03.coursewarehub.MainActivity;
import com.team03.coursewarehub.R;

public class AssignmentView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_view);

        // Connection to firebase
        Firebase.setAndroidContext(this);

        String baseurl = "https://team03-coursewarehub.firebaseio.com/";

        // move to next activity
        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgHeader);
        Firebase ref = new Firebase(baseurl + courseTitle);


        //get assignment name and url
        final String name = intent.getStringExtra("assignmentName");
        final String url = intent.getStringExtra("assignmentUrl");

        //textview to display assignment name
        final TextView tv = (TextView)findViewById(R.id.textView);
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

        //full PDF url as string
        //String docUrl = "<iframe src='http://docs.google.com/viewer?url=" + url + "' width='100%' height='100%' style='border: none;'></iframe>";
        WebView assignmentViewer = new WebView(AssignmentView.this);
        //WebView assignmentViewer = (WebView)findViewById(R.id.assignmentViewer);
        assignmentViewer.getSettings().setJavaScriptEnabled(true);
        assignmentViewer.getSettings().setPluginsEnabled(true);
        assignmentViewer.getSettings().setAllowFileAccess(true);
        assignmentViewer.loadUrl("http://docs.google.com/viewer?url=" + url);
        setContentView(assignmentViewer);
    }

}
