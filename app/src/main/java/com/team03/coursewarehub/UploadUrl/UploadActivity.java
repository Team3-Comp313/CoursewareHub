package com.team03.coursewarehub.UploadUrl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.team03.coursewarehub.MainActivity;
import com.team03.coursewarehub.R;

import java.util.ArrayList;

public class UploadActivity extends Activity {

    // Listview Data
    ArrayList<String> topics = new ArrayList<String>();
    ArrayList<String> types = new ArrayList<String>();

    //values for all fields
    String topic;
    String type;
    String name;
    String url;
    EditText name_edit;
    EditText url_edit;
    int firebaseIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        setTitle("Upload Your Material");

        types.add("Please select the material type");
        types.add("Assignments");
        types.add("Videos");
        types.add("Handouts");
        types.add("Examples");

        // Connection to firebase
        Firebase.setAndroidContext(this);
        String baseurl = "https://team03-coursewarehub.firebaseio.com/";
        final Firebase ref = new Firebase(baseurl);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snapshotChild : snapshot.getChildren()) {
                    topics.add(snapshotChild.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        name_edit = (EditText) findViewById(R.id.name);
        url_edit = (EditText) findViewById(R.id.url);


        // ListView
        Spinner topic_spinner = (Spinner) findViewById(R.id.topic_spinner);
        topics.add("Please select a topic");
        ArrayAdapter<String> topic_adapter = new ArrayAdapter<String>(this, R.layout.main_upload_topic_list_item, R.id.topic_items, topics) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
        };
        topic_spinner.setAdapter(topic_adapter);
        topic_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                topic = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner type_spinner = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<String> type_adapter = new ArrayAdapter<String>(this, R.layout.main_upload_type_list_item, R.id.type_items, types) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
        };
        ;
        type_spinner.setAdapter(type_adapter);
        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Videos")) {
                    url_edit.setHint("Enter Video ID");
                } else {
                    url_edit.setHint("Enter Material Url");
                }
                type = parent.getItemAtPosition(position).toString();

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        firebaseIdx = (int) snapshot.child(topic).child(type).getChildrenCount();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BootstrapButton btnUpload = (BootstrapButton) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_edit.getText().toString();
                url = url_edit.getText().toString();
                System.out.println(name);
                System.out.println(url);
                if (topic.equals("Please select a topic") || type.equals("Please select the material type") || name.equals("") || url.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please fill all the fields.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    if (Patterns.WEB_URL.matcher(url).matches()) {
                        // Toast.makeText(UploadActivity.this, "Valid!", Toast.LENGTH_SHORT).show();

                        ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Name").setValue(name);
                        ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Url").setValue(url);

                        Context context = getApplicationContext();
                        CharSequence text = "Your material is successfully uploaded";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent activityMain = new Intent(UploadActivity.this,
                                MainActivity.class);
                        UploadActivity.this.startActivity(activityMain);
                        finish();
                    } else {
                        Toast.makeText(UploadActivity.this, "Invalid Url!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Intent activityIntent = new Intent(UploadActivity.this,
                MainActivity.class);
        UploadActivity.this.startActivity(activityIntent);
        finish();
    }
}
