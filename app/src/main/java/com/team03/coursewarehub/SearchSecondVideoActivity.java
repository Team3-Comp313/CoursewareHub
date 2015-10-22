package com.team03.coursewarehub;

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

import java.util.ArrayList;
import java.util.Arrays;

public class SearchSecondVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_second_video);

        // ListView
        ListView lv = (ListView) findViewById(R.id.list_view);

        ArrayAdapter<String> adapter;

        // ArrayList for Listview
        ArrayList<String> sampleVideoList;

        // Listview Data
        String sampleVideo[] = {"Dummy1", "Dummy2", "Dummy3", "Dummy4", "Dummy5",
                "Dummy6", "Dummy7", "Dummy8", "Dummy9", "Dummy10", "Dummy11"};

        sampleVideoList = new ArrayList<String>(Arrays.asList(sampleVideo));
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, sampleVideo);
        lv.setAdapter(adapter);

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
                if (dataSnapshot.getKey().toString() == "Image") {
                    String tempHeaderImage = (String) dataSnapshot.getValue();
                    // Assigning Image to Image view
                    UrlImageViewHelper.setUrlDrawable(imgHeader, tempHeaderImage);
                }
                if (dataSnapshot.getKey().toString() == "Video") {
                    final String video = dataSnapshot.getValue().toString();
                    /* display list to listview.  */
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_second_you_tube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
