package devteam.pokemon_know;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;
import devteam.pokemon_know.PokemonServer.PokemonWebService;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import devteam.pokemon_know.PokemonServer.RetrivePokemon;


import okhttp3.Response;

public class MainActivity extends DrawerActivity implements OnMapReadyCallback {
    private Random gen;
    private GoogleMap mMap;
    private Handler mHandler;
    private AutoCompleteTextView search, search_dialog;
    private ArrayAdapter<String> adapter;
    private LinearLayout linear;
    private RetrivePokemon retrivePokemon;
    private ArrayList<Marker> markerArrayList;
    private HashMap<String,PostPokemon> pokemonHashMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;

    private Button filterButton;
    private AutoCompleteTextView autoCompleteTextView;


    private DBHelper db;
    private ImageView image;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(PokemonWebService.getServer());
        } catch (URISyntaxException e) {}
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer();
        init();
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search);
        filterButton = (Button) findViewById(R.id.button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                retrivePokemon.filterPokemon(autoCompleteTextView.getText().toString());
            }
        });
        gen = new Random();
    }


    private ArrayAdapter<String> getAutoComplete() {
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, db.getPokemonList());
    }

    private void init() {
        db = new DBHelper(this);

//        linear = (LinearLayout) findViewById(R.id.activity_main);
//        linear.setBackgroundColor(Color.rgb(202, 101, 34));

        search = (AutoCompleteTextView) findViewById(R.id.search);
        search.setAdapter(getAutoComplete());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerArrayList = new ArrayList<Marker>();
        mSocket.on("getPokemon", onGetPokemon);
        mSocket.connect();
        pokemonHashMap = new HashMap<String, PostPokemon>();
    }

    private Emitter.Listener onGetPokemon = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject object = (JSONObject) args[0];
            try {
                Log.d("Get Pokemon",object.getString("pokemonName"));
                if(pokemonHashMap.containsKey(object.getString("_id").toString()))
                    return;
                PostPokemon postPokemon = new PostPokemon(
                        object.getString("postId").toString(),
                        object.getString("pokemonName").toString(),
                        object.getString("lat").toString(),
                        object.getString("long").toString(),
                        object.getString("startTime").toString(),
                        object.getString("endTime").toString(),
                        object.getString("user").toString()
                );
                pokemonHashMap.put(object.getString("_id").toString(),postPokemon);
                final Pokemon pokemon = db.getPokemonByName(object.getString("pokemonName").toString());
                if( pokemon != null ){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createMarker(pokemon.getId(),pokemon.getName(),object.getDouble("lat"),object.getDouble("long"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void createMarker(String pokemonId,String pokemonName,Double Lat,Double Long){
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier("pokemon"+pokemonId, "drawable",getPackageName());
        LatLng latLng = new LatLng(Lat, Long);
        Marker pokeMark = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(pokemonName)
//                        .snippet("I Love You.")
                .icon(BitmapDescriptorFactory.fromResource(resourceId)));
        pokeMark.setTag(pokemonId);
//        if( filterPokemonName != null ){
//            if( filterPokemonName.equals(pokemonName) )
//                pokeMark.setVisible(true);
//        }
        markerArrayList.add(pokeMark);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit Application");
        exitDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        exitDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
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
//        retrivePokemon = new RetrivePokemon(getApplicationContext(), mMap, markerArrayList,postPokemonArrayList);
//        retrivePokemon.start();
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                for(int i=0;i<markerArrayList.size();i++){
                    String id = markerArrayList.get(i).getTag()+"";
                    markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pokemon"+id,2,2)));
                }

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Camera",latLng.latitude+":"+latLng.longitude);
                final String lat = String.valueOf(latLng.latitude);
                final String longi = String.valueOf(latLng.longitude);

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.customdialog);
                dialog.setCancelable(true);

                search_dialog = (AutoCompleteTextView) dialog.findViewById(R.id.search_dialog);
                search_dialog.setAdapter(getAutoComplete());

                search_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                        Log.d("select", search_dialog.getText().toString());
                        Resources resources = getApplicationContext().getResources();
                        int resourceId = resources.getIdentifier(db.getPokemonID(search_dialog.getText().toString()), "drawable",
                                getApplicationContext().getPackageName());

                        if(image == null)
                            image = (ImageView) dialog.findViewById(R.id.imageDialog);
                        else
                            image.setImageResource(resourceId);
                    }
                });

                Button button1 = (Button)dialog.findViewById(R.id.sentbutton);
                button1.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View v) {
                        sentRequest(search_dialog.getText().toString(), lat, longi);
//                        retrivePokemon.getPostPokemon();
                        dialog.cancel();
                    }
                });

                Button button2 = (Button)dialog.findViewById(R.id.cancelbutton);
                button2.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();

            }
        });

    }

    private void sentRequest(String pokemonName, String lat, String longi){
        JSONObject addPokemon = new JSONObject();
        try {
            addPokemon.put("postId",gen.nextInt(100000)+"");
            addPokemon.put("user","noneiei");
            addPokemon.put("pokemonName",pokemonName);
            addPokemon.put("lat",lat);
            addPokemon.put("long",longi);
            mSocket.emit("addPokemon",addPokemon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()/width, imageBitmap.getHeight()/height, false);
        return resizedBitmap;
    }

}//end Activity
