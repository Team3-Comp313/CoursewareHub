package com.team03.coursewarehub.Example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.team03.coursewarehub.R;

import java.util.ArrayList;
import java.util.List;

public class ExampleListActivity extends Activity {

    // Listview Data
    List<String> sampleExample = new ArrayList<String>();
    List<String> exampleUrl = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_list);

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
                if (dataSnapshot.getKey().toString() == "Examples") {
                    // display list to listview.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        sampleExample.add(snapshot.child("Name").getValue().toString());
                        exampleUrl.add(snapshot.child("Url").getValue().toString());
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
        ListView lv = (ListView) findViewById(R.id.example_list_view);

        // ArrayList for Listview
        ArrayList<String> sampleVideoList;

        ArrayAdapter<String> adapter;

        sampleVideoList = new ArrayList<String>(sampleExample);
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.main_search_list_item, R.id.product_name, sampleExample);
        lv.setAdapter(adapter);

        /*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), VideoDisplay.class);
                i.putExtra("AssignmentUrl", AssignmentUrl.get(position));
                i.putExtra("exampleName", sampleAssignments.get(position));
                i.putExtra("courseTitle", courseTitle);
                startActivity(i);
            }
        });
        */
    }
}
