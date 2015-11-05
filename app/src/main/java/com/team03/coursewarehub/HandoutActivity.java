package com.team03.coursewarehub;

import android.content.Intent;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class HandoutActivity extends ListActivity {
//ListView Data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handout);



    Firebase.setAndroidContext(this);

        String baseurl= "https://team03-coursewarehub.firebaseio.com/";
        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView)findViewById(R.id.imgHeader);
        Firebase ref = new Firebase(baseurl+courseTitle);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().toString()== "Image"){
                    String tempImageHeader= (String) dataSnapshot.getValue();
                    UrlImageViewHelper.setUrlDrawable(imgHeader,tempImageHeader);
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

        String[] itemname ={
                "C#",
                "PHP",
                "Java",
                "R",
                "Javascript",
                "CSS",
                "HTML",
                "C++",
        };
        //Takes list_item
        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.handout_list_row,
                R.id.Itemname, itemname));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handout, menu);
        return true;
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        //String selectedValue = (String) getListAdapter().getItem(position);


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
