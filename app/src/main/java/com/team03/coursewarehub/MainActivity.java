package com.team03.coursewarehub;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.team03.coursewarehub.Search.SearchListActivity;
import com.team03.coursewarehub.UploadUrl.UploadActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BootstrapButton btnSearch = (BootstrapButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityCourse = new Intent(MainActivity.this,
                        SearchListActivity.class);
                MainActivity.this.startActivity(activityCourse);
                finish();
            }
        });

        BootstrapButton btnUpload = (BootstrapButton) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityUpload = new Intent(MainActivity.this,
                        UploadActivity.class);
                MainActivity.this.startActivity(activityUpload);
                finish();
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
}