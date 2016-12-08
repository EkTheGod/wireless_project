package devteam.pokemon_know;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;
import devteam.pokemon_know.Permission.PermissionUtils;
import devteam.pokemon_know.PokemonServer.PokemonWebService;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import devteam.pokemon_know.PokemonServer.RetrivePokemon;


import okhttp3.Response;

public class MainActivity extends DrawerActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Random gen;
    private GoogleMap mMap;
    private boolean mPermissionDenied = false;
    private Handler mHandler;
    private AutoCompleteTextView search, search_dialog;
    private ArrayAdapter<String> adapter;
    private LinearLayout linear;
    private RetrivePokemon retrivePokemon;
    private ArrayList<Marker> markerArrayList;
    private HashMap<String, PostPokemon> pokemonHashMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private ImageView filterButton;
    private AutoCompleteTextView autoCompleteTextView;
    private Boolean userLike;
    private int numberUserLike;
    private Dialog viewMarkerDialog;


    private DBHelper db;
    private ImageView image;
    private LatLng latLng;
    private Marker currLocationMarker;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(PokemonWebService.getServer());
        } catch (URISyntaxException e) {
        }
    }


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer();
        init();
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search);
        filterButton = (ImageView) findViewById(R.id.button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("getPokemonByName",autoCompleteTextView.getText());
            }
        });
        gen = new Random();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }


    private ArrayAdapter<String> getAutoComplete() {
        return new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, db.getPokemonList());
    }

    private void init() {
        db = new DBHelper(this);
        search = (AutoCompleteTextView) findViewById(R.id.search);
        search.setAdapter(getAutoComplete());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerArrayList = new ArrayList<Marker>();
        mSocket.on("getPokemon", onGetPokemon);
        mSocket.on("getPokemonAll", onGetPokemonAll);
        mSocket.on("showPokemonMarker", onShowPokemonMarker);
        mSocket.on("likePokemon", onLikePokemon);
        mSocket.on("getPokemonByName", onGetPokemonAll);
        mSocket.connect();
        mSocket.emit("clientConnection");
//        mSocket.emit("getPokemonAll");
        pokemonHashMap = new HashMap<String, PostPokemon>();

        viewMarkerDialog = new Dialog(MainActivity.this);
        viewMarkerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewMarkerDialog.setContentView(R.layout.custom_view_marker_dialog);
        viewMarkerDialog.setCancelable(true);
        setInterval();
    }

    private void setInterval(){
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // do stuff then
                // can call h again after work!
//                Log.d("TimerExample", "Going for... " + time);
                Log.d("Check Interval",autoCompleteTextView.getText()+"");
                if( !autoCompleteTextView.getText().toString().equals("") ){
                    mSocket.emit("getPokemonByName",autoCompleteTextView.getText());
                }else{
                    mSocket.emit("getPokemonAll");
                }
                h.postDelayed(this, 1000*60);
            }
        }, 0); // 1 Minute delay (takes millis)
    }

    private Emitter.Listener onGetPokemon = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject object = (JSONObject) args[0];
            try {
//                if (pokemonHashMap.containsKey(object.getString("_id").toString()))
//                    return;
                PostPokemon postPokemon = new PostPokemon(
                        object.getString("postId").toString(),
                        object.getString("pokemonName").toString(),
                        object.getString("lat").toString(),
                        object.getString("long").toString(),
                        object.getString("startTime").toString(),
                        object.getString("endTime").toString(),
                        object.getString("userId").toString()
                );
//                pokemonHashMap.put(object.getString("_id").toString(), postPokemon);
                final Pokemon pokemon = db.getPokemonByName(object.getString("pokemonName").toString());
                if (pokemon != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createMarker(object.getString("_id").toString(),pokemon.getId(), pokemon.getName(), object.getDouble("lat"), object.getDouble("long"));
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

    private Emitter.Listener onGetPokemonAll = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                }
            });
            final JSONArray jsonArray = (JSONArray) args[0];
            for(int i=0;i<jsonArray.length();i++){
                final JSONObject object;
                try {
                    object = jsonArray.getJSONObject(i);
//                    if (pokemonHashMap.containsKey(object.getString("_id").toString()))
//                        return;
                    PostPokemon postPokemon = new PostPokemon(
                            object.getString("postId").toString(),
                            object.getString("pokemonName").toString(),
                            object.getString("lat").toString(),
                            object.getString("long").toString(),
                            object.getString("startTime").toString(),
                            object.getString("endTime").toString(),
                            object.getString("userId").toString()
                    );
//                    pokemonHashMap.put(object.getString("_id").toString(), postPokemon);
                    final Pokemon pokemon = db.getPokemonByName(object.getString("pokemonName").toString());
                    if (pokemon != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    createMarker(object.getString("_id").toString(),pokemon.getId(), pokemon.getName(), object.getDouble("lat"), object.getDouble("long"));
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
        }
    };

    private Emitter.Listener onShowPokemonMarker = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSONObject object = (JSONObject) args[0];
                        ImageView pokemonImg = (ImageView) viewMarkerDialog.findViewById(R.id.imageDialog);
                        String pokemonResPath = null;
                        pokemonResPath = db.getPokemonByName(object.getString("pokemonName")).getImgPath();
                        int resId = getApplicationContext().getResources().getIdentifier(pokemonResPath,"drawable",getApplicationContext().getPackageName());
                        pokemonImg.setImageResource(resId);
                        //Text View Pokemon Name
                        TextView tvPokemonName = (TextView) viewMarkerDialog.findViewById(R.id.tvPokemonName);
                        tvPokemonName.setText( object.getString("pokemonName") );
                        //Text View Poster Name
                        TextView tvPosterName = (TextView) viewMarkerDialog.findViewById(R.id.tvPosterName);
                        tvPosterName.setText( object.getString("userName") );

                        //Image View Like
                        final ImageView likeImg = (ImageView) viewMarkerDialog.findViewById(R.id.ivLike);
                        JSONArray likeArray = object.getJSONArray("status");
                        numberUserLike = likeArray.length();
                        userLike = false;
                        likeImg.setImageResource(R.drawable.unlike_button);
                        for(int i=0;i<likeArray.length();i++){
                            if( likeArray.getString(i).equals(Profile.getCurrentProfile().getId()) ){
                                likeImg.setImageResource(R.drawable.like_button);
                                userLike = true;
                                break;
                            }
                        }
                        //Number Of User Like
                        final TextView tvNumberUserLike = (TextView) viewMarkerDialog.findViewById(R.id.tvLikeNumber);
                        tvNumberUserLike.setText(numberUserLike+"");
                        likeImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    JSONObject like = new JSONObject();
                                    like.put("_id",object.getString("_id"));
                                    like.put("userId",Profile.getCurrentProfile().getId());
                                    if( userLike ){
                                        likeImg.setImageResource(R.drawable.unlike_button);
                                        tvNumberUserLike.setText( (--numberUserLike)+"" );
                                        userLike = false;
                                    }else{
                                        likeImg.setImageResource(R.drawable.like_button);
                                        tvNumberUserLike.setText( (++numberUserLike)+"" );
                                        userLike = true;
                                    }
                                    mSocket.emit("likePokemon",like);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        //Submit Button
                        Button submitButtn = (Button) viewMarkerDialog.findViewById(R.id.submitButton);
                        submitButtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewMarkerDialog.cancel();
                            }
                        });
                        viewMarkerDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private Emitter.Listener onLikePokemon = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

        }
    };

    private void createMarker(String postId,String pokemonId, String pokemonName, Double Lat, Double Long) {
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier("pokemon" + pokemonId, "drawable", getPackageName());
        LatLng latLng = new LatLng(Lat, Long);
        Marker pokeMark = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(pokemonName)
                .snippet(postId)
                .icon( BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pokemon"+pokemonId,2,2))) );
        pokeMark.setTag(pokemonId);
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("MainActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MainActivity", "Can't find style. Error: ", e);
        }
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                for (int i = 0; i < markerArrayList.size(); i++) {
//                    String id = markerArrayList.get(i).getTag() + "";
//                    markerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pokemon" + id, 2, 2)));
//                }
//
//            }
//        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Camera", latLng.latitude + ":" + latLng.longitude);
                final String lat = String.valueOf(latLng.latitude);
                final String longi = String.valueOf(latLng.longitude);

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.customdialog);
                dialog.setCancelable(true);

                search_dialog = (AutoCompleteTextView) dialog.findViewById(R.id.search_dialog);
                search_dialog.setAdapter(getAutoComplete());
                image = (ImageView) dialog.findViewById(R.id.imageDialog);

                search_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("select", search_dialog.getText().toString());
                        Resources resources = getApplicationContext().getResources();
                        int resourceId = resources.getIdentifier(db.getPokemonID(search_dialog.getText().toString()), "drawable",
                                getApplicationContext().getPackageName());
                        image.setImageResource(resourceId);
                    }
                });

                Button button1 = (Button) dialog.findViewById(R.id.sentbutton);
                button1.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View v) {
                        sentRequest(search_dialog.getText().toString(), lat, longi);
//                        retrivePokemon.getPostPokemon();
                        dialog.cancel();
                    }
                });

                Button button2 = (Button) dialog.findViewById(R.id.cancelbutton);
                button2.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mSocket.emit("showPokemonMarker",marker.getSnippet());
                return true;
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void sentRequest(String pokemonName, String lat, String longi) {
        JSONObject addPokemon = new JSONObject();
        try {
            addPokemon.put("postId", gen.nextInt(100000) + "");
            addPokemon.put("userId", Profile.getCurrentProfile().getId());
            addPokemon.put("userName", Profile.getCurrentProfile().getName());
            addPokemon.put("pokemonName", pokemonName);
            addPokemon.put("lat", lat);
            addPokemon.put("long", longi);
            mSocket.emit("addPokemon", addPokemon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth() / width, imageBitmap.getHeight() / height, false);
        return resizedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mSocket.disconnect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        double lat;
        double lng;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();

            LatLng loc = new LatLng(lat, lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,16));
//            on
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

//        //place marker at current position
//        //mGoogleMap.clear();
//        if (currLocationMarker != null) {
//            currLocationMarker.remove();
//        }
//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        currLocationMarker = mMap.addMarker(markerOptions);
//
//        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();
//
//        //zoom to current position:
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng).zoom(14).build();
//
//        mMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));
//
//        //If you only need one location, unregister the listener
//        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        Circle circle = mMap.addCircle(new CircleOptions()
//                .center(new LatLng(location.getLatitude(), location.getLongitude()))
//                .radius(10000)
//                .strokeColor(Color.RED)
//                .fillColor(Color.RED));

    }


}//end Activity
