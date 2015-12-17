package com.team03.coursewarehub.UploadUrl;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.NetworkOnMainThreadException;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UploadActivity extends Activity {
    boolean validUrl = false;

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

    // @Override
    protected void doInBackground() {

    }

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
                    if (type.equals("Videos")) {
                        //if (Patterns.WEB_URL.matcher(url).matches()) {
                        if (url.length() == 11) {
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
                    } else {
                        MyTask task = new MyTask();
                        task.execute(url);
                        System.out.println("right");

                        if (validUrl) {
                            //if (exists(url)) {
                            /*ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Name").setValue(name);
                            ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Url").setValue(url);

                            Context context = getApplicationContext();
                            CharSequence text = "Your material is successfully uploaded";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            Intent activityMain = new Intent(UploadActivity.this,
                                    MainActivity.class);
                            UploadActivity.this.startActivity(activityMain);
                            finish();*/

                            // Toast.makeText(UploadActivity.this, "True!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Toast.makeText(UploadActivity.this, "False", Toast.LENGTH_SHORT).show();
                        }
                        // checkURLExists(url);
                    }
                }
            }
        });
    }

    public void uploadUrl() {

        // Connection to firebase
        Firebase.setAndroidContext(this);
        String baseurl = "https://team03-coursewarehub.firebaseio.com/";
        final Firebase ref = new Firebase(baseurl);

        ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Name").setValue(name);
        ref.child(topic).child(type).child(Integer.toString(firebaseIdx)).child("Url").setValue(url);

        System.out.println(topic);
        System.out.println(type);
        System.out.println(name);
        System.out.println(url);

        Context context = getApplicationContext();
        CharSequence text = "Your material is successfully uploaded";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent activityMain = new Intent(UploadActivity.this,
                MainActivity.class);
        UploadActivity.this.startActivity(activityMain);
        finish();
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

    public void checkURLExists(String URLName) {

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();

            HttpURLConnection.setFollowRedirects(false);
            con.setRequestMethod("HEAD");
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getBaseContext(), "URL Exist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "URL not Exists", Toast.LENGTH_SHORT).show();
            }
        } catch (UnknownHostException unknownHostException) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean exists(String URLName) {

        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
/*
class RetrieveFeedTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected void onPreExecute() {

    }

    // private Exception exception;
    String url;

    protected Boolean doInBackground(String... urls) {

        try {
            HttpURLConnection.setFollowRedirects(false);
            int i = urls.length;
            url = urls[0];
            // url = urls.toString();
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            con.setRequestMethod("HEAD");
            //con.connect();
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void onPostExecute(Void feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}*/
//public class MainActivity extends Activity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String customURL = "http://www.desicomments.com/dc3/08/273858/273858.jpg";

        MyTask task = new MyTask();
        task.execute(customURL);
    }
*/

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;

            if (bResponse) {
                uploadUrl();
                //Toast.makeText(UploadActivity.this, "Valid!", Toast.LENGTH_SHORT).show();
                validUrl = true;
            } else {
                Toast.makeText(UploadActivity.this, "Invalid Url!", Toast.LENGTH_SHORT).show();
                validUrl = false;
            }
        }
    }
}