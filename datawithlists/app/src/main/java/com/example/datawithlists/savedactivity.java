package com.example.datawithlists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class savedactivity extends AppCompatActivity {
    static ArrayList saved= new ArrayList();
   static ArrayAdapter saveadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedactivity);
        ListView listViews=(ListView)findViewById(R.id.ram);

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.datawithlists",MODE_PRIVATE);
        final HashSet<String> hashSet=(HashSet<String>)sharedPreferences.getStringSet("url", null);
        if (hashSet==null){

            saved.add("Empty");
        }
        else {
            saved= new ArrayList<String>(hashSet);
        }
        saveadapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,saved);
       listViews.setAdapter(saveadapter);
       listViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent= new Intent(getApplicationContext(),Main2Activity.class);
               intent.putExtra("url", (String) saved.get(position));
               startActivity(intent);
           }
       });
       listViews.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

               new AlertDialog.Builder(savedactivity.this)
                       .setIcon(R.drawable.ic_delete_black_24dp)
                       .setTitle("Are you Sure want to delete it?")
                       .setMessage("This will remove permanently")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               saved.remove(position);
                               saveadapter.notifyDataSetChanged();
                               SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences("com.example.datawithlists",MODE_PRIVATE);
                               HashSet hashSet1= new HashSet(saved);
                               sharedPreferences.edit().putStringSet("url",hashSet1).apply();
                           }
                       }).setNegativeButton("No",null).show();

              // saved.
               return true;
           }
       });




    }
}
