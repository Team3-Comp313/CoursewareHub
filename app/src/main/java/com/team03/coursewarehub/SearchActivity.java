package com.team03.coursewarehub;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SearchActivity extends Activity {

    private ListView lv;
    private EditText et;

    ArrayAdapter<String> adapter;

    // ArrayList for Listview
    private ArrayList<String> courseList;
    int textlength = 0;

    // Listview Data
    String courses[] = {"Java (The New Boston)", "Java (Standford)", "Android (The New Boston)", "HTC Sense", "HTC Sensation XE",
            "iPhone 4S", "Samsung Galaxy Note 800",
            "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Fire Base
        Firebase.setAndroidContext(this);

        lv = (ListView) findViewById(R.id.list_view);
        et = (EditText) findViewById(R.id.inputSearch);

        courseList = new ArrayList<String>(Arrays.asList(courses));

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, courses);
        lv.setAdapter(adapter);

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                SearchActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String base_url = "https://team03-coursewarehub.firebaseio.com/";

                Firebase ref = new Firebase(base_url + courseList.get(position));
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Intent activityIntent = new Intent(SearchActivity.this,
                                SearchSecondActivity.class);
                        activityIntent.putExtra("courseTitle",
                                courseList.get(position));
                        startActivity(activityIntent);
                        finish();
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
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent activityIntent = new Intent(SearchActivity.this,
                MainActivity.class);
        SearchActivity.this.startActivity(activityIntent);
        finish();
    }
}