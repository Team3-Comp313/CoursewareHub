package com.team03.coursewarehub.Search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.team03.coursewarehub.MainActivity;
import com.team03.coursewarehub.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchListActivity extends Activity {

    private ListView lv;
    private EditText et;

    ArrayAdapter<String> adapter;

    // ArrayList for Listview
    private ArrayList<String> courseList;
    int textlength = 0;

    // Listview Data
    String courses[] = {"Java (The New Boston)", "Java (Standford)", "Android (The New Boston)", "Android(Utom)", "Angular",
            "C#", "Http",
            "Sql", "NoSql", "Dataware", "MacBook Pro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        // Fire Base
        Firebase.setAndroidContext(this);

        lv = (ListView) findViewById(R.id.list_view);
        et = (EditText) findViewById(R.id.inputSearch);

        courseList = new ArrayList<String>(Arrays.asList(courses));

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.main_search_list_item, R.id.product_name, courses);
        lv.setAdapter(adapter);

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                SearchListActivity.this.adapter.getFilter().filter(cs);
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String base_url = "https://team03-coursewarehub.firebaseio.com/";

                Firebase ref = new Firebase(base_url + courseList.get(position));
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Intent activityIntent = new Intent(SearchListActivity.this,
                                SearchDisplayActivity.class);
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
        Intent activityIntent = new Intent(SearchListActivity.this,
                MainActivity.class);
        SearchListActivity.this.startActivity(activityIntent);
        finish();
    }
}
