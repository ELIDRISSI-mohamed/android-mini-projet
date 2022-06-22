package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActionActivity extends AppCompatActivity {
    ListView listView;
    MyDataBase mydb = new MyDataBase(ActionActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        listView = findViewById(R.id.listView);

        ArrayList<Activity> activities = new ArrayList<Activity>();
        activities = (ArrayList<Activity>) mydb.getActivities();

        ArrayAdapter<Activity> adapter = new ArrayAdapter<Activity>(this,
                android.R.layout.simple_list_item_1, activities);
        listView.setAdapter(adapter);

    }
    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    //clicked menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case R.id.idEditProfile:
                startActivity(new Intent(ActionActivity.this, EditProfileActivity.class));
                return true;
            case R.id.idMaps:
                startActivity(new Intent(ActionActivity.this, LocationActivity.class));
                return true;
            case R.id.idLogout:
                startActivity(new Intent(ActionActivity.this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}