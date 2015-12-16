package com.team03.coursewarehub.Assignments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.team03.coursewarehub.R;
import com.team03.coursewarehub.Youtube.VideoDisplay;

import java.util.ArrayList;
import java.util.List;

public class AssignmentListActivity extends Activity {

    // Listview Data
    private List<String> sampleAssignments = new ArrayList<String>();
    private List<String> AssignmentUrl = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        // Connection to firebase
        Firebase.setAndroidContext(this);

        String baseurl = "https://team03-coursewarehub.firebaseio.com/";

        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgHeader);
        Firebase ref = new Firebase(baseurl + courseTitle);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().toString() == "Image") {
                    String tempHeaderImage = (String) dataSnapshot.getValue();
                    // Assigning Image to Image view
                    UrlImageViewHelper.setUrlDrawable(imgHeader, tempHeaderImage);
                }
                if (dataSnapshot.getKey().toString() == "Assignments") {
                    // display list to listview.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        sampleAssignments.add(snapshot.child("Name").getValue().toString());
                        // System.out.println("List is here  ->>>>>>>>>>>" + sampleAssignments.add(snapshot.child("Name").getValue().toString()));
                        AssignmentUrl.add(snapshot.child("Url").getValue().toString());
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

        // ListView
        ListView lv = (ListView) findViewById(R.id.assignment_list_view);

        // ArrayList for Listview
        ArrayList<String> sampleVideoList;

        ArrayAdapter<String> adapter;

        sampleVideoList = new ArrayList<String>(sampleAssignments);
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.main_search_list_item, R.id.product_name, sampleAssignments);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), AssignmentView.class);
                i.putExtra("assignmentUrl", AssignmentUrl.get(position));
                i.putExtra("assignmentName", sampleAssignments.get(position));
                i.putExtra("courseTitle", courseTitle);
                startActivity(i);
            }
        });

    }
}
