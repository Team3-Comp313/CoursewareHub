package com.team03.coursewarehub.Comments;

import android.app.Activity;
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
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.team03.coursewarehub.R;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends Activity {

    List<String> CommentList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Connection to firebase
        Firebase.setAndroidContext(this);

        String baseurl = "https://team03-coursewarehub.firebaseio.com/";

        Intent intent = getIntent();
        final String courseTitle = intent.getStringExtra("courseTitle");
        setTitle(courseTitle);
        final ImageView imgHeader = (ImageView) findViewById(R.id.imgHeader);
        final Firebase ref = new Firebase(baseurl + courseTitle);

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
//                Firebase refFB = ref.child("Videos").child(String.valueOf(iPosition)).child("Comment");
                Firebase refFB = ref.child("Comments");
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
                if (dataSnapshot.getKey().toString() == "Comments") {
                    List<Comments> com = new ArrayList<Comments>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // this loop gets from the oldest comment to the latest comment
                        com.add(snapshot.getValue(Comments.class));
                    }
                    for(long i = (dataSnapshot.getChildrenCount() - 1) ; i >= 0 ; i--){
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

        ListView lv = (ListView) findViewById(R.id.listViewComment);
        adapter = new ArrayAdapter<String>(this, R.layout.main_search_list_item, R.id.product_name, CommentList);
        lv.setAdapter(adapter);
        setListViewHeightBasedOnItems(lv);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
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

}
