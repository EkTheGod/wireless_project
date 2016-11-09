package devteam.pokemon_know;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Handler;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.PokemonServer.RetrivePokemon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Handler mHandler;
    private AutoCompleteTextView search;
    private ArrayAdapter<String> adapter;
    private LinearLayout linear;
    private RetrivePokemon retrivePokemon;
    private ArrayList<Marker> markerArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        init();

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //sentRequest();
                getAutoComplete();
            }
        }, 1000);
    }

    private void getAutoComplete() {
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new DBHelper(this).getPokemonList());
        search.setAdapter(adapter);
    }

    private void init(){
        linear = (LinearLayout) findViewById(R.id.activity_main);
        linear.setBackgroundColor(Color.rgb(202, 101, 34));
        search = (AutoCompleteTextView) findViewById(R.id.search);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerArrayList = new ArrayList<Marker>();
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit Application");
        exitDialog.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        exitDialog.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        retrivePokemon = new RetrivePokemon(getApplicationContext(),mMap, markerArrayList);
        retrivePokemon.start();
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void sentRequest(){
        final String uri = "http://192.168.0.188:7777/addpokemon";
        //final String body = String.format("\"postId\": \"%s\",\"pokemonName\": \"%s\", \"lat\": \"%s\", \"long\": \"%s\", \"user\": \"%s\", \"aek\", \"pikachu\", \"lat\", \"long\", \"user\"");

        final OkHttpClient client = new OkHttpClient();
        //final HttpPost postMethod = new HttpPost(uri);

        RequestBody formBody = new FormBody.Builder()
                .add("postId", "12345")
                .add("pokemonName", "Rachanon")
                .add("lat", "-")
                .add("long", "-")
                .add("user", "noneiei")
                .build();
        Request request = new Request.Builder()
                .url(uri)
                .post(formBody)
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Test", e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }

            });
        }catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("postId", "12345"));
//            nameValuePairs.add(new BasicNameValuePair("pokemonName", "Rachanon"));
//            nameValuePairs.add(new BasicNameValuePair("lat", "-"));
//            nameValuePairs.add(new BasicNameValuePair("long", "-"));
//            nameValuePairs.add(new BasicNameValuePair("user", "noneiei"));
//            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//            // Execute HTTP Post Request
//            HttpResponse response = client.execute(postMethod);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(uri)
//                .build();
//        Response responses = null;
//
//        try {
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("postId", "12345"));
//            nameValuePairs.add(new BasicNameValuePair("pokemonName", "Rachanon"));
//            nameValuePairs.add(new BasicNameValuePair("lat", "-"));
//            nameValuePairs.add(new BasicNameValuePair("long", "-"));
//            nameValuePairs.add(new BasicNameValuePair("user", "noneiei"));
//            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//            // Execute HTTP Post Request
//            HttpResponse response = client.execute(postMethod);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("Test", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//
//        });


    }

}//end Activity
