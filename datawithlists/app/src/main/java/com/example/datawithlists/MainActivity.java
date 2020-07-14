package com.example.datawithlists;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    int flag=0;
    Intent dialogIntent;
    ArrayAdapter arrayAdapter;
    ArrayList titles=new ArrayList();
  //  String jsontitle;
    //SQLiteDatabase mydb;
   // String jsonurlsinfo;
    String stringnew;
    ProgressDialog p;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_top,menu);
        return super.onCreateOptionsMenu(menu);

    }


  //  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent= new Intent(this,savedactivity.class);
       SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.datawithlists",MODE_PRIVATE);
        // sharedPreferences.edit().putString("urls",stringnew).apply();
        startActivity(intent);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button showbtn=(Button)findViewById(R.id.showbtn);
        listView=(ListView)findViewById(R.id.lists);

        if (haveNetworkConnection()) {
            listView.setVisibility(View.VISIBLE);
            showbtn.setVisibility(View.INVISIBLE);

            Downtask downtask = new Downtask();
            flag=1;
            try {
                downtask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");


            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        else{
           new AlertDialog.Builder(this)
                   .setMessage("Connect to the internet")
                   .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                           dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           getApplicationContext().startActivity(dialogIntent);
                       }
                   }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   System.exit(0);
               }
           }).show();
        }

        showbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Downtask downtask = new Downtask();
                try {
                    downtask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");


                } catch (Exception e) {

                    e.printStackTrace();
                }
                showbtn.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);

            }
        });

    }

    public class Wrapper{

        ArrayList titleslist= new ArrayList();
        ArrayList urlslist= new ArrayList();

    }
    public  class Downtask extends AsyncTask<String, Void, Wrapper> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             p = new ProgressDialog(MainActivity.this);
            p.setMessage("Please wait...News is loading");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }


        @Override
        protected Wrapper doInBackground(String... strings) {
            Wrapper wrap= new Wrapper();
            URL url;
            String res="";
            try {
                wrap.urlslist.clear();
                wrap.titleslist.clear();
                titles.clear();
                url= new URL(strings[0]);
                HttpURLConnection httpURLConnection=null;
                httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream= httpURLConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while (data!=-1){

                    char current=(char)data;
                    res+=current;
                    data=reader.read();
                }
            //    Log.i("res",res);

                JSONArray jsonArray=new JSONArray(res);
                int items=20;
                if (items<20){

                    items=jsonArray.length();
                }

                for (int i=0;i<items;i++){

                    String ids=jsonArray.getString(i);
                    url=new URL("https://hacker-news.firebaseio.com/v0/item/"+ids+".json?print=pretty");
                    httpURLConnection=(HttpURLConnection)url.openConnection();
                     inputStream=httpURLConnection.getInputStream();
                     reader=new InputStreamReader(inputStream);
                    int date=reader.read();
                    String infotitle="";
                    while(date!=-1){

                        char curr=(char)date;
                        infotitle+=curr;
                        date=reader.read();

                    }
                    JSONObject jsonObject=new JSONObject(infotitle);


                    if (!jsonObject.isNull("title")&&!jsonObject.isNull("url")) {


                     //   mydb.execSQL("INSERT INTO sets values('"+jsontitle+"','"+jsonurlsinfo+"')");
                        String jsontitle=jsonObject.getString("title");
                        String jsonurlsinfo=jsonObject.getString("url");

                        wrap.titleslist.add(jsontitle);
                        wrap.urlslist.add(jsonurlsinfo);

                   }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {


                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            }


            return wrap;

        }



      @Override
        protected void onPostExecute(final Wrapper wrapper) {
            p.hide();

          arrayAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1, titles);
          for (int i = 0; i < wrapper.titleslist.size(); i++) {


              titles.add(wrapper.titleslist.get(i));
              arrayAdapter.notifyDataSetChanged();
              listView.setAdapter(arrayAdapter);

          }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                    intent.putExtra("url", String.valueOf(wrapper.urlslist.get(position)));
                    startActivity(intent);

                }
            });
          listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
              @Override
              public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                  new AlertDialog.Builder(MainActivity.this)
                          .setIcon(R.drawable.ic_save_black_24dp)
                          .setTitle("Do you want to save?")
                          .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {

                            savedactivity.saved.add(wrapper.urlslist.get(position));
                            //savedactivity.saveadapter.notifyDataSetChanged();
                            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("com.example.datawithlists",MODE_PRIVATE);
                            HashSet hashSet= new HashSet(savedactivity.saved);
                            sharedPreferences.edit().putStringSet("url",hashSet).apply();

                              }
                          }).setNegativeButton("No",null)
                          .show();
                  return true;
              }
          });
      }


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
